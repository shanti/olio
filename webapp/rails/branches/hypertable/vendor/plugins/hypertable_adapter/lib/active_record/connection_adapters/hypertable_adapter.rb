require 'active_record/connection_adapters/abstract_adapter'
require 'active_record/connection_adapters/qualified_column'

module ActiveRecord
  class Base
    def self.require_hypertable_thrift_client
      # Include the hypertools driver if one hasn't already been loaded
      unless defined? Hypertable::ThriftClient
        gem 'hypertable-thrift-client'
        require_dependency 'thrift_client'
      end
    end

    def self.hypertable_connection(config)
      config = config.symbolize_keys
      require_hypertable_thrift_client

      raise "Hypertable/ThriftBroker config missing :host" if !config[:host]
      connection = Hypertable::ThriftClient.new(config[:host], config[:port])

      ConnectionAdapters::HypertableAdapter.new(connection, logger, config)
    end
  end

  module ConnectionAdapters
    class HypertableAdapter < AbstractAdapter
      @@read_latency = 0.0
      @@write_latency = 0.0
      cattr_accessor :read_latency, :write_latency

      CELL_FLAG_DELETE_ROW = 0
      CELL_FLAG_DELETE_COLUMN_FAMILY = 1
      CELL_FLAG_DELETE_CELL = 2
      CELL_FLAG_INSERT = 255

      def initialize(connection, logger, config)
        super(connection, logger)
        @config = config
        @hypertable_column_names = {}
      end

      def self.reset_timing
        @@read_latency = 0.0
        @@write_latency = 0.0
      end

      def self.get_timing
        [@@read_latency, @@write_latency]
      end

      def convert_select_columns_to_array_of_columns(s, columns=nil)
        select_rows = s.class == String ? s.split(',').map{|s| s.strip} : s
        select_rows = select_rows.reject{|s| s == '*'}

        if select_rows.empty? and !columns.blank?
          for c in columns
            next if c.name == 'ROW' # skip over the ROW key, always included
            if c.is_a?(QualifiedColumn)
              for q in c.qualifiers
                select_rows << qualified_column_name(c.name, q.to_s)
              end
            else
              select_rows << c.name
            end
          end
        end

        select_rows
      end

      def adapter_name
        'Hypertable'
      end

      def supports_migrations?
        true
      end

      def native_database_types
        {
          :string      => { :name => "varchar", :limit => 255 }
        }
      end

      def sanitize_conditions(options)
        case options[:conditions]
          when Hash
            # requires Hypertable API to support query by arbitrary cell value
            raise "HyperRecord does not support specifying conditions by Hash"
          when NilClass
            # do nothing
          else
            raise "Only hash conditions are supported"
        end
      end

      def execute_with_options(options)
        # Rows can be specified using a number of different options:
        # row ranges (start_row and end_row)
        options[:row_intervals] ||= []

        if options[:row_keys]
          options[:row_keys].flatten.each do |rk|
            row_interval = Hypertable::ThriftGen::RowInterval.new
            row_interval.start_row = rk
            row_interval.start_inclusive = true
            row_interval.end_row = rk
            row_interval.end_inclusive = true
            options[:row_intervals] << row_interval
          end
        elsif options[:start_row]
          raise "missing :end_row" if !options[:end_row]

          options[:start_inclusive] = options.has_key?(:start_inclusive) ? options[:start_inclusive] : true
          options[:end_inclusive] = options.has_key?(:end_inclusive) ? options[:end_inclusive] : true

          row_interval = Hypertable::ThriftGen::RowInterval.new
          row_interval.start_row = options[:start_row]
          row_interval.start_inclusive = options[:start_inclusive]
          row_interval.end_row = options[:end_row]
          row_interval.end_inclusive = options[:end_inclusive]
          options[:row_intervals] << row_interval
        end

        sanitize_conditions(options)

        select_rows = convert_select_columns_to_array_of_columns(options[:select], options[:columns])

        t1 = Time.now
        table_name = options[:table_name]
        scan_spec = convert_options_to_scan_spec(options)
        cells = @connection.get_cells(table_name, scan_spec)
        @@read_latency += Time.now - t1

        cells
      end

      def convert_options_to_scan_spec(options={})
        scan_spec = Hypertable::ThriftGen::ScanSpec.new
        options[:revs] ||= 1
        options[:return_deletes] ||= false

        for key in options.keys
          case key.to_sym
            when :row_intervals
              scan_spec.row_intervals = options[key]
            when :cell_intervals
              scan_spec.cell_intervals = options[key]
            when :start_time
              scan_spec.start_time = options[key]
            when :end_time
              scan_spec.end_time = options[key]
            when :limit
              scan_spec.row_limit = options[key]
            when :revs
              scan_spec.revs = options[key]
            when :return_deletes
              scan_spec.return_deletes = options[key]
            when :table_name, :start_row, :end_row, :start_inclusive, :end_inclusive, :select, :columns, :row_keys, :conditions, :include
              # ignore
            else
              raise "Unrecognized scan spec option: #{key}"
          end
        end

        scan_spec
      end

      def execute(hql, name=nil)
        log(hql, name) { @connection.hql_query(hql) }
      end

      # Returns array of column objects for table associated with this class.
      # Hypertable allows columns to include dashes in the name.  This doesn't
      # play well with Ruby (can't have dashes in method names), so we must
      # maintain a mapping of original column names to Ruby-safe names.
      def columns(table_name, name = nil)#:nodoc:
        # Each table always has a row key called 'ROW'
        columns = [
          Column.new('ROW', '')
        ]
        schema = describe_table(table_name)
        doc = REXML::Document.new(schema)
        column_families = doc.elements['Schema/AccessGroup[@name="default"]'].elements.to_a

        @hypertable_column_names[table_name] ||= {}
        for cf in column_families
          column_name = cf.elements['Name'].text
          rubified_name = rubify_column_name(column_name)
          @hypertable_column_names[table_name][rubified_name] = column_name
          columns << new_column(rubified_name, '')
        end

        columns
      end

      def remove_column_from_name_map(table_name, name)
        @hypertable_column_names[table_name].delete(rubify_column_name(name))
      end

      def add_column_to_name_map(table_name, name)
        @hypertable_column_names[table_name][rubify_column_name(name)] = name
      end

      def add_qualified_column(table_name, column_family, qualifiers=[], default='', sql_type=nil, null=true)
        qc = QualifiedColumn.new(column_family, default, sql_type, null)
        qc.qualifiers = qualifiers
        qualifiers.each{|q| add_column_to_name_map(table_name, qualified_column_name(column_family, q))}
        qc
      end

      def new_column(column_name, default_value='')
        Column.new(rubify_column_name(column_name), default_value)
      end

      def qualified_column_name(column_family, qualifier=nil)
        [column_family, qualifier].compact.join(':')
      end

      def rubify_column_name(column_name)
        column_name.to_s.gsub(/-+/, '_')
      end

      def is_qualified_column_name?(column_name)
        column_family, qualifier = column_name.split(':', 2)
        if qualifier
          [true, column_family, qualifier] 
        else
          [false, nil, nil]
        end
      end

      def quote(value, column = nil)
        case value
          when NilClass then ''
          when String then value
          else super(value, column)
        end
      end

      def quote_column_name(name)
        "'#{name}'"
      end

      def quote_column_name_for_table(name, table_name)
        quote_column_name(hypertable_column_name(name, table_name))
      end

      def hypertable_column_name(name, table_name, declared_columns_only=false)
        n = @hypertable_column_names[table_name][name]
        n ||= name if !declared_columns_only
        n
      end

      def describe_table(table_name)
        @connection.get_schema(table_name)
      end

      def tables(name=nil)
        @connection.get_tables
      end

      def drop_table(table_name, options = {})
        @connection.drop_table(table_name, options[:if_exists] || false)
      end

      def write_cells(table_name, cells)
        return if cells.blank?

        @connection.with_mutator(table_name) do |mutator|
          t1 = Time.now
          @connection.set_cells(mutator, cells.map{|c| cell_from_array(c)})
          @@write_latency += Time.now - t1
        end
      end

      # Cell passed in as [row_key, column_name, value]
      def cell_from_array(array)
        cell = Hypertable::ThriftGen::Cell.new
        cell.row_key = array[0]
        column_family, column_qualifier = array[1].split(':')
        cell.column_family = column_family
        cell.column_qualifier = column_qualifier if column_qualifier
        cell.value = array[2] if array[2]
        cell
      end

      def delete_cells(table_name, cells)
        t1 = Time.now

        @connection.with_mutator(table_name) do |mutator|
          @connection.set_cells(mutator, cells.map{|c|
            cell = cell_from_array(c)
            cell.flag = CELL_FLAG_DELETE_CELL
            cell
          })
        end

        @@write_latency += Time.now - t1
      end

      def delete_rows(table_name, row_keys)
        t1 = Time.now
        cells = row_keys.map do |row_key|
          cell = Hypertable::ThriftGen::Cell.new
          cell.row_key = row_key
          cell.flag = CELL_FLAG_DELETE_ROW
          cell
        end

        @connection.with_mutator(table_name) do |mutator|
          @connection.set_cells(mutator, cells)
        end

        @@write_latency += Time.now - t1
      end

      def insert_fixture(fixture, table_name)
        fixture_hash = fixture.to_hash
        row_key = fixture_hash.delete('ROW')
        cells = []
        fixture_hash.keys.each{|k| cells << [row_key, k, fixture_hash[k]]}
        write_cells(table_name, cells)
      end

      private

        def select(hql, name=nil)
          # TODO: need hypertools run_hql to return result set
          raise "not yet implemented"
        end
    end
  end
end

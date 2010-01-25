#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
require 'rails_generator/generators/components/model/model_generator'
require File.dirname(__FILE__) + '/../rspec_default_values'

class RspecModelGenerator < ModelGenerator

  def manifest

    record do |m|
      # Check for class naming collisions.
      m.class_collisions class_path, class_name

      # Model, spec, and fixture directories.
      m.directory File.join('app/models', class_path)
      m.directory File.join('spec/models', class_path)
      unless options[:skip_fixture]
        m.directory File.join('spec/fixtures', class_path)
      end

      # Model class, spec and fixtures.
      m.template 'model:model.rb',      File.join('app/models', class_path, "#{file_name}.rb")
      m.template 'model_spec.rb',       File.join('spec/models', class_path, "#{file_name}_spec.rb")
      unless options[:skip_fixture]
        m.template 'model:fixtures.yml',  File.join('spec/fixtures', "#{table_name}.yml")
      end

      unless options[:skip_migration]
        m.migration_template 'model:migration.rb', 'db/migrate', :assigns => {
          :migration_name => "Create#{class_name.pluralize.gsub(/::/, '')}"
        }, :migration_file_name => "create_#{file_path.gsub(/\//, '_').pluralize}"
      end

    end
  end

end

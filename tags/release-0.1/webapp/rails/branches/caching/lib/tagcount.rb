module Tagcount
  
  def self.included(base)
    base.extend ClassMethods
  end
  
  module ClassMethods
    # Taken from http://wiki.rubyonrails.org/rails/pages/ActsAsTaggablePluginHowto
    def tags_count(options)
      sql = "SELECT  tags.id AS id, tags.name AS name, COUNT(*) AS count FROM tags, taggings, #{table_name} " 
      sql << "WHERE taggings.taggable_id = #{table_name}.#{primary_key} AND taggings.tag_id = tags.id " 
      sql << "AND #{sanitize_sql(options[:conditions])} " if options[:conditions]
      sql << "GROUP BY tags.name " 
      sql << "HAVING count #{options[:count]} " if options[:count]
      sql << "ORDER BY #{options[:order]} " if options[:order]
      sql << "LIMIT #{options[:limit]} " if options[:limit]
      find_by_sql(sql)
    end
  end
end
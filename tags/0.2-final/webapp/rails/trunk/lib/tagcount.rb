#
#  Licensed to the Apache Software Foundation (ASF) under one
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

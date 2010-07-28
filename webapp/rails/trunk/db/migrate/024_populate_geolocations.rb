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
class PopulateGeolocations < ActiveRecord::Migration
  def self.up
    r = Rails::Configuration.new
    y = YAML::load_file(r.database_configuration_file)    
    if y[RAILS_ENV.to_s]["adapter"] == "mysql"
      system "mysql -u #{y[RAILS_ENV.to_s]["username"]} -p#{y[RAILS_ENV.to_s]["password"]} -D #{y[RAILS_ENV.to_s]["database"]} < ./db/migrate/geolocations.sql"
    #elsif y[RAILS_ENV.to_s]["adapter"] == "sqlite2"
      # Handle SQLite2
    #elsif y[RAILS_ENV.to_s]["adapter"] == "sqlite3"
      # Handle SQLite3
    #elsif y[RAILS_ENV.to_s]["adapter"] == "postgresql"
      # Handle PostgreSQL
    #elsif y[RAILS_ENV.to_s]["adapter"] == "oracle"
      # Handle Oracle
    else
      write "Connection adapter unsupported thus far. Geolocations have not been added to the database."
    end
  end

  def self.down
    execute "TRUNCATE geolocations"
  end
end

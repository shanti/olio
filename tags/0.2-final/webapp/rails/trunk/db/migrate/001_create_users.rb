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
class CreateUsers < ActiveRecord::Migration
  def self.up
    create_table :users do |t|
      t.column  :username, :string, :limit => 25
      t.column  :password, :string, :limit => 25
      t.column  :firstname, :string, :limit => 25
      t.column  :lastname, :string, :limit => 25
      t.column  :email, :string, :limit => 90
      t.column  :telephone, :string, :limit => 25
      t.column  :imageurl, :string, :limit => 100
      t.column  :imagethumburl, :string, :limit => 100
      t.column  :summary, :string, :limit => 2500
      t.column  :timezone, :string, :limit => 25
      t.column  :created_at, :timestamp
      t.column  :updated_at, :timestamp
    end
  end

  def self.down
    drop_table :users
  end
end

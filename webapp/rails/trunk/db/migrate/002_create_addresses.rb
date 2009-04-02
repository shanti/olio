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
class CreateAddresses < ActiveRecord::Migration
  def self.up
    create_table :addresses do |t|
      t.column :street1, :string, :limit => 55
      t.column :street2, :string, :limit => 55
      t.column :city, :string, :limit => 55
      t.column :state, :string, :limit => 25
      t.column :zip, :string, :limit => 12
      t.column :country, :string, :limit => 55
      t.column :latitude, :decimal, :precision => 14, :scale => 10
      t.column :longitude, :decimal, :precision => 14, :scale => 10
    end
  end

  def self.down
    drop_table :addresses
  end
end

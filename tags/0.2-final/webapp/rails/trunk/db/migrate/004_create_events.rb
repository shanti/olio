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
class CreateEvents < ActiveRecord::Migration
  def self.up
    create_table :events, :force => true do |t|
      t.column :title, :string, :limit => 100
      t.column :description, :string, :limit => 500
      t.column :telephone, :string, :limit => 20

      # foreign keys
      t.column :user_id, :integer
      t.column :address_id, :integer

      # file-related
      t.column :image_url, :string
      t.column :image_thumb_url, :string
      t.column :literature_url, :string

      # time-related
      t.column :timezone, :string
      t.column :event_timestamp, :timestamp
      t.column :event_date, :date #redundant
      t.column :created_at, :timestamp 

      # other
      t.column :total_score, :integer
      t.column :num_votes, :integer      
      t.column :disabled, :boolean
    end
    
    add_index :events, :id #not present in PHP version
    add_index :events, :event_date
    add_index :events, :event_timestamp
    add_index :events, :created_at
    add_index :events, :user_id
  end

  def self.down
    remove_index :events, :user_id
    remove_index :events, :created_at
    remove_index :events, :event_timestamp
    remove_index :events, :event_date
    remove_index :events, :id
    
    drop_table :events
  end
end

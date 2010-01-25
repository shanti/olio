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
class CreateImages < ActiveRecord::Migration
  def self.up
    create_table :images do |t|
      # general attachment_fu attributes
      t.column :size, :integer
      t.column :content_type, :string
      t.column :filename, :string    
      
      # image-specific attachment_fu attributes
      t.column :height, :integer
      t.column :width, :integer
      t.column :parent_id, :integer
      t.column :thumbnail, :string
    end
    
    add_index :images, :filename
    add_index :images, :thumbnail
  end

  def self.down
    drop_table :images
  end
end

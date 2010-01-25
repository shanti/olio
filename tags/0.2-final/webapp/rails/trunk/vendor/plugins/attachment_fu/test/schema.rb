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
ActiveRecord::Schema.define(:version => 0) do
  create_table :attachments, :force => true do |t|
    t.column :db_file_id,      :integer
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :aspect_ratio,    :float
  end

  create_table :file_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
    t.column :aspect_ratio,    :float
  end

  create_table :gd2_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
  end

  create_table :image_science_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
  end

  create_table :core_image_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
  end
  
  create_table :mini_magick_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
  end

  create_table :mini_magick_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
  end

  create_table :orphan_attachments, :force => true do |t|
    t.column :db_file_id,      :integer
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
  end
  
  create_table :minimal_attachments, :force => true do |t|
    t.column :size,            :integer
    t.column :content_type,    :string, :limit => 255
  end

  create_table :db_files, :force => true do |t|
    t.column :data, :binary
  end

  create_table :s3_attachments, :force => true do |t|
    t.column :parent_id,       :integer
    t.column :thumbnail,       :string 
    t.column :filename,        :string, :limit => 255
    t.column :content_type,    :string, :limit => 255
    t.column :size,            :integer
    t.column :width,           :integer
    t.column :height,          :integer
    t.column :type,            :string
    t.column :aspect_ratio,    :float
  end
end
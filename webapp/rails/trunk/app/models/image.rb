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
# == Schema Information
# Schema version: 17
#
# Table name: images
#
#  id           :integer(11)     not null, primary key
#  size         :integer(11)     
#  content_type :string(255)     
#  filename     :string(255)     
#  height       :integer(11)     
#  width        :integer(11)     
#  parent_id    :integer(11)     
#  thumbnail    :string(255)     
#

unless Technoweenie::AttachmentFu.content_types.include? 'image/jpeg; charset=ISO-8859-1'
  Technoweenie::AttachmentFu.content_types << 'image/jpeg; charset=ISO-8859-1'
end

class Image < ActiveRecord::Base
  has_attachment :content_type => :image, 
                 :storage => :file_system, 
                 :max_size => 1.megabytes, 
                 :thumbnails => { :thumb => '250x250>' }, 
                 :path_prefix => IMAGE_STORE_PATH

  validates_uniqueness_of :filename
  validates_as_attachment
  
  # Overload the thumbnail name so it's compatible with the php file generator.
  def thumbnail_name_for(thumbnail = nil)
    name = super
    name.sub!(/_thumb/, 't')
    name
  end
  
  include Uploadable
  
end

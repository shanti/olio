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
require File.expand_path(File.join(File.dirname(__FILE__), 'test_helper'))

class OrphanAttachmentTest < Test::Unit::TestCase
  include BaseAttachmentTests
  attachment_model OrphanAttachment
  
  def test_should_create_image_from_uploaded_file
    assert_created do
      attachment = upload_file :filename => '/files/rails.png'
      assert_valid attachment
      assert !attachment.db_file.new_record? if attachment.respond_to?(:db_file)
      assert  attachment.image?
      assert !attachment.size.zero?
    end
  end
  
  def test_should_create_file_from_uploaded_file
    assert_created do
      attachment = upload_file :filename => '/files/foo.txt'
      assert_valid attachment
      assert !attachment.db_file.new_record? if attachment.respond_to?(:db_file)
      assert  attachment.image?
      assert !attachment.size.zero?
    end
  end
  
  def test_should_create_file_from_merb_temp_file
    assert_created do
      attachment = upload_merb_file :filename => '/files/foo.txt'
      assert_valid attachment
      assert !attachment.db_file.new_record? if attachment.respond_to?(:db_file)
      assert  attachment.image?
      assert !attachment.size.zero?
    end
  end
  
  def test_should_create_image_from_uploaded_file_with_custom_content_type
    assert_created do
      attachment = upload_file :content_type => 'foo/bar', :filename => '/files/rails.png'
      assert_valid attachment
      assert !attachment.image?
      assert !attachment.db_file.new_record? if attachment.respond_to?(:db_file)
      assert !attachment.size.zero?
      #assert_equal 1784, attachment.size
    end
  end
  
  def test_should_create_thumbnail
    attachment = upload_file :filename => '/files/rails.png'
    
    assert_raise Technoweenie::AttachmentFu::ThumbnailError do
      attachment.create_or_update_thumbnail(attachment.create_temp_file, 'thumb', 50, 50)
    end
  end
  
  def test_should_create_thumbnail_with_geometry_string
   attachment = upload_file :filename => '/files/rails.png'
    
    assert_raise Technoweenie::AttachmentFu::ThumbnailError do
      attachment.create_or_update_thumbnail(attachment.create_temp_file, 'thumb', 'x50')
    end
  end
end

class MinimalAttachmentTest < OrphanAttachmentTest
  attachment_model MinimalAttachment
end
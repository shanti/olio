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

class ValidationTest < Test::Unit::TestCase
  def test_should_invalidate_big_files
    @attachment = SmallAttachment.new
    assert !@attachment.valid?
    assert @attachment.errors.on(:size)
    
    @attachment.size = 2000
    assert !@attachment.valid?
    assert @attachment.errors.on(:size), @attachment.errors.full_messages.to_sentence
    
    @attachment.size = 1000
    assert !@attachment.valid?
    assert_nil @attachment.errors.on(:size)
  end

  def test_should_invalidate_small_files
    @attachment = BigAttachment.new
    assert !@attachment.valid?
    assert @attachment.errors.on(:size)
    
    @attachment.size = 2000
    assert !@attachment.valid?
    assert @attachment.errors.on(:size), @attachment.errors.full_messages.to_sentence
    
    @attachment.size = 1.megabyte
    assert !@attachment.valid?
    assert_nil @attachment.errors.on(:size)
  end
  
  def test_should_validate_content_type
    @attachment = PdfAttachment.new
    assert !@attachment.valid?
    assert @attachment.errors.on(:content_type)

    @attachment.content_type = 'foo'
    assert !@attachment.valid?
    assert @attachment.errors.on(:content_type)

    @attachment.content_type = 'pdf'
    assert !@attachment.valid?
    assert_nil @attachment.errors.on(:content_type)
  end

  def test_should_require_filename
    @attachment = Attachment.new
    assert !@attachment.valid?
    assert @attachment.errors.on(:filename)
    
    @attachment.filename = 'foo'
    assert !@attachment.valid?
    assert_nil @attachment.errors.on(:filename)
  end
end
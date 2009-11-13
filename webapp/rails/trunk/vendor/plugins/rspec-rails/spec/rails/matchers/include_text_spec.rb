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
require File.expand_path(File.dirname(__FILE__) + '/../../spec_helper')

describe "include_text" do

  describe "where target is a String" do
    it 'should match submitted text using a string' do
      string = 'foo'
      string.should include_text('foo')
    end

    it 'should match if the text is contained' do
      string = 'I am a big piece of text'
      string.should include_text('big piece')
    end

    it 'should not match if text is not contained' do
      string = 'I am a big piece of text'
      string.should_not include_text('corey')
    end
  end

end

describe "include_text", :type => :controller do
  ['isolation','integration'].each do |mode|
    if mode == 'integration'
      integrate_views
    end

    describe "where target is a response (in #{mode} mode)" do
      controller_name :render_spec

      it "should pass with exactly matching text" do
        post 'text_action'
        response.should include_text("this is the text for this action")
      end

      it 'should pass with substring matching text' do
        post 'text_action'
        response.should include_text('text for this')
      end

      it "should fail with incorrect text" do
        post 'text_action'
        lambda {
          response.should include_text("the accordian guy")
        }.should fail_with("expected to find \"the accordian guy\" in \"this is the text for this action\"")
      end

      it "should pass using should_not with incorrect text" do
        post 'text_action'
        response.should_not include_text("the accordian guy")
      end

      it "should fail when a template is rendered" do
        get 'some_action'
        lambda {
          response.should include_text("this is the text for this action")
        }.should fail_with(/expected to find \"this is the text for this action\"/)
      end
    end
  end
end


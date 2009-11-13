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

describe "have_text" do
  
  describe "where target is a Regexp" do
    it 'should should match submitted text using a regexp' do
      string = 'foo'
      string.should have_text(/fo*/)
    end
  end
  
  describe "where target is a String" do
    it 'should match submitted text using a string' do
      string = 'foo'
      string.should have_text('foo')
    end
  end
  
end

describe "have_text",
  :type => :controller do
  ['isolation','integration'].each do |mode|
    if mode == 'integration'
      integrate_views
    end

    describe "where target is a response (in #{mode} mode)" do
      controller_name :render_spec

      it "should pass with exactly matching text" do
        post 'text_action'
        response.should have_text("this is the text for this action")
      end

      it "should pass with matching text (using Regexp)" do
        post 'text_action'
        response.should have_text(/is the text/)
      end

      it "should fail with matching text" do
        post 'text_action'
        lambda {
          response.should have_text("this is NOT the text for this action")
        }.should fail_with("expected \"this is NOT the text for this action\", got \"this is the text for this action\"")
      end

      it "should fail when a template is rendered" do
        post 'some_action'
        lambda {
          response.should have_text("this is the text for this action")
        }.should fail_with(/expected \"this is the text for this action\", got .*/)
      end
      
      it "should pass using should_not with incorrect text" do
        post 'text_action'
        response.should_not have_text("the accordian guy")
      end
    end
  end
end


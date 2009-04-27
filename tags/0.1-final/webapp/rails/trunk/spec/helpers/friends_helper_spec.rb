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
require File.dirname(__FILE__) + '/../spec_helper'

# Taken from http://www.mintsource.org/2007/9/3/rspec-rjs-helper-specs -Hubert
module RJSSpecHelper
  class HelperRJSPageProxy
    def initialize(context)
      @context = context
    end
  
    def method_missing(method, *arguments)
      block = Proc.new { |page|  @lines = []; page.send(method, *arguments) }
      @context.response.body = ActionView::Helpers::PrototypeHelper::JavaScriptGenerator.new(@context, &block).to_s
      @context.response
    end
  end

  def rjs_for
    HelperRJSPageProxy.new(self)
  end
end

include RJSSpecHelper

describe FriendsHelper do
  include FriendsHelper
  
  it "should test refresh invites" do
    rjs_for.refresh_invites("something").body.should == "if($('something_requests').getElementsBySelector('ol li').length == 0) {\n$(\"something_requests\").visualEffect(\"blind_up\");\n}"
  end
  
end

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
require File.dirname(__FILE__) + '/../../spec_helper'

describe "A shared view example_group", :shared => true do
  it "should have some tag with some text" do
    response.should have_tag('div', 'This is text from a method in the ViewSpecHelper')
  end
end

describe "A view example_group", :type => :view do
  it_should_behave_like "A shared view example_group"
  
  before(:each) do
    render "view_spec/implicit_helper"
  end
end
  

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

describe "assert_equal", :shared => true do
  it "like assert_equal" do
    assert_equal 1, 1
    lambda {
      assert_equal 1, 2
    }.should raise_error(Test::Unit::AssertionFailedError)
  end
end

describe "A model spec should be able to access 'test/unit' assertions", :type => :model do
  it_should_behave_like "assert_equal"
end

describe "A view spec should be able to access 'test/unit' assertions", :type => :view do
  it_should_behave_like "assert_equal"
end

describe "A helper spec should be able to access 'test/unit' assertions", :type => :helper do
  it_should_behave_like "assert_equal"
end

describe "A controller spec with integrated views should be able to access 'test/unit' assertions", :type => :controller do
  controller_name :controller_spec
  integrate_views
  it_should_behave_like "assert_equal"
end

describe "A controller spec should be able to access 'test/unit' assertions", :type => :controller do
  controller_name :controller_spec
  it_should_behave_like "assert_equal"
end

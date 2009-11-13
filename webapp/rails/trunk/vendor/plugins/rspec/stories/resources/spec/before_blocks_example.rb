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
$:.unshift File.join(File.dirname(__FILE__), *%w[.. .. .. lib])
require 'spec'

Spec::Runner.configure do |config|
  config.before(:suite) do
    $before_suite = "before suite"
  end
  config.before(:each) do
    @before_each = "before each"
  end
  config.before(:all) do
    @before_all = "before all"
  end
end

describe "stuff in before blocks" do
  describe "with :suite" do
    it "should be available in the example" do
      $before_suite.should == "before suite"
    end
  end
  describe "with :all" do
    it "should be available in the example" do
      @before_all.should == "before all"
    end
  end
  describe "with :each" do
    it "should be available in the example" do
      @before_each.should == "before each"
    end
  end
end
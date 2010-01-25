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
require File.dirname(__FILE__) + '/test_unit_spec_helper'

describe "ExampleGroup with test/unit/interop" do
  include TestUnitSpecHelper
    
  describe "with passing examples" do
    it "should output 0 failures" do
      output = ruby("#{resources}/spec_that_passes.rb")
      output.should include("1 example, 0 failures")
    end

    it "should return an exit code of 0" do
      ruby("#{resources}/spec_that_passes.rb")
      $?.should == 0
    end
  end

  describe "with failing examples" do
    it "should output 1 failure" do
      output = ruby("#{resources}/spec_that_fails.rb")
      output.should include("1 example, 1 failure")
    end

    it "should return an exit code of 256" do
      ruby("#{resources}/spec_that_fails.rb")
      $?.should == 256
    end
  end

  describe "with example that raises an error" do
    it "should output 1 failure" do
      output = ruby("#{resources}/spec_with_errors.rb")
      output.should include("1 example, 1 failure")
    end

    it "should return an exit code of 256" do
      ruby("#{resources}/spec_with_errors.rb")
      $?.should == 256
    end
  end
  
  describe "options hash" do
    it "should be exposed" do
      output = ruby("#{resources}/spec_with_options_hash.rb")
      output.should include("1 example, 0 failures")
    end
  end
end
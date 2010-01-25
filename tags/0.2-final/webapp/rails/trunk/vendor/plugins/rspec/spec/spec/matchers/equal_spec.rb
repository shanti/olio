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
require File.dirname(__FILE__) + '/../../spec_helper.rb'

module Spec
  module Matchers
    describe "equal" do
      it "should match when actual.equal?(expected)" do
        equal(1).matches?(1).should be_true
      end
      it "should not match when !actual.equal?(expected)" do
        equal("1").matches?("1").should be_false
      end
      it "should describe itself" do
        matcher = equal(1)
        matcher.matches?(1)
        matcher.description.should == "equal 1"
      end
      it "should provide message, expected and actual on #failure_message" do
        matcher = equal("1")
        matcher.matches?(1)
        matcher.failure_message.should == ["expected \"1\", got 1 (using .equal?)", "1", 1]
      end
      it "should provide message, expected and actual on #negative_failure_message" do
        matcher = equal(1)
        matcher.matches?(1)
        matcher.negative_failure_message.should == ["expected 1 not to equal 1 (using .equal?)", 1, 1]
      end
    end
  end
end

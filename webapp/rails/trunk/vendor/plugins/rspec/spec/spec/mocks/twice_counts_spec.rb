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
  module Mocks
    describe "TwiceCounts" do
      before(:each) do
        @mock = mock("test mock")
      end

      it "twice should fail when call count is higher than expected" do
        @mock.should_receive(:random_call).twice
        @mock.random_call
        @mock.random_call
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end
      
      it "twice should fail when call count is lower than expected" do
        @mock.should_receive(:random_call).twice
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end
      
      it "twice should fail when called twice with wrong args on the first call" do
        @mock.should_receive(:random_call).twice.with("1", 1)
        lambda do
          @mock.random_call(1, "1")
        end.should raise_error(MockExpectationError)
        @mock.rspec_reset
      end
      
      it "twice should fail when called twice with wrong args on the second call" do
        @mock.should_receive(:random_call).twice.with("1", 1)
        @mock.random_call("1", 1)
        lambda do
          @mock.random_call(1, "1")
        end.should raise_error(MockExpectationError)
        @mock.rspec_reset
      end
      
      it "twice should pass when called twice" do
        @mock.should_receive(:random_call).twice
        @mock.random_call
        @mock.random_call
        @mock.rspec_verify
      end
      
      it "twice should pass when called twice with specified args" do
        @mock.should_receive(:random_call).twice.with("1", 1)
        @mock.random_call("1", 1)
        @mock.random_call("1", 1)
        @mock.rspec_verify
      end
      
      it "twice should pass when called twice with unspecified args" do
        @mock.should_receive(:random_call).twice
        @mock.random_call("1")
        @mock.random_call(1)
        @mock.rspec_verify
      end
    end
  end
end

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
    describe "OnceCounts" do
      before(:each) do
        @mock = mock("test mock")
      end

      it "once should fail when called once with wrong args" do
        @mock.should_receive(:random_call).once.with("a", "b", "c")
        lambda do
          @mock.random_call("d", "e", "f")
        end.should raise_error(MockExpectationError)
        @mock.rspec_reset
      end

      it "once should fail when called twice" do
        @mock.should_receive(:random_call).once
        @mock.random_call
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end
      
      it "once should fail when not called" do
        @mock.should_receive(:random_call).once
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end

      it "once should pass when called once" do
        @mock.should_receive(:random_call).once
        @mock.random_call
        @mock.rspec_verify
      end

      it "once should pass when called once with specified args" do
        @mock.should_receive(:random_call).once.with("a", "b", "c")
        @mock.random_call("a", "b", "c")
        @mock.rspec_verify
      end

      it "once should pass when called once with unspecified args" do
        @mock.should_receive(:random_call).once
        @mock.random_call("a", "b", "c")
        @mock.rspec_verify
      end
    end
  end
end

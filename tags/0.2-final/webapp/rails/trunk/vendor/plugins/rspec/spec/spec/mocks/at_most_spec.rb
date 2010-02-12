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
    describe "at_most" do
      before(:each) do
        @mock = Mock.new("test mock")
      end

      it "should fail when at most n times method is called n plus 1 times" do
        @mock.should_receive(:random_call).at_most(4).times
        @mock.random_call
        @mock.random_call
        @mock.random_call
        @mock.random_call
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end

      it "should fail when at most once method is called twice" do
        @mock.should_receive(:random_call).at_most(:once)
        @mock.random_call
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end

      it "should fail when at most twice method is called three times" do
        @mock.should_receive(:random_call).at_most(:twice)
        @mock.random_call
        @mock.random_call
        @mock.random_call
        lambda do
          @mock.rspec_verify
        end.should raise_error(MockExpectationError)
      end

      it "should pass when at most n times method is called exactly n times" do
        @mock.should_receive(:random_call).at_most(4).times
        @mock.random_call
        @mock.random_call
        @mock.random_call
        @mock.random_call
        @mock.rspec_verify
      end

      it "should pass when at most n times method is called less than n times" do
        @mock.should_receive(:random_call).at_most(4).times
        @mock.random_call
        @mock.random_call
        @mock.random_call
        @mock.rspec_verify
      end

      it "should pass when at most n times method is never called" do
        @mock.should_receive(:random_call).at_most(4).times
        @mock.rspec_verify
      end

      it "should pass when at most once method is called once" do
        @mock.should_receive(:random_call).at_most(:once)
        @mock.random_call
        @mock.rspec_verify
      end

      it "should pass when at most once method is never called" do
        @mock.should_receive(:random_call).at_most(:once)
        @mock.rspec_verify
      end

      it "should pass when at most twice method is called once" do
        @mock.should_receive(:random_call).at_most(:twice)
        @mock.random_call
        @mock.rspec_verify
      end

      it "should pass when at most twice method is called twice" do
        @mock.should_receive(:random_call).at_most(:twice)
        @mock.random_call
        @mock.random_call
        @mock.rspec_verify
      end

      it "should pass when at most twice method is never called" do
        @mock.should_receive(:random_call).at_most(:twice)
        @mock.rspec_verify
      end
    end
  end
end
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
    describe "wrap_expectation" do
      
      def stub_matcher
        @_stub_matcher ||= simple_matcher do
        end
      end
      
      def failing_matcher
        @_failing_matcher ||= simple_matcher do
          1.should == 2
        end
      end
      
      it "should return true if there is no error" do
        wrap_expectation stub_matcher do
        end.should be_true
      end
      
      it "should return false if there is an error" do
        wrap_expectation failing_matcher do
          raise "error"
        end.should be_false
      end
    end
  end
end
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
    describe ArgumentExpectation do
      it "should consider an object that responds to #matches? and #description to be a matcher" do
        argument_expecatation = Spec::Mocks::ArgumentExpectation.new([])
        obj = mock("matcher")
        obj.should_receive(:respond_to?).with(:matches?).and_return(true)
        obj.should_receive(:respond_to?).with(:description).and_return(true)
        argument_expecatation.is_matcher?(obj).should be_true
      end

      it "should NOT consider an object that only responds to #matches? to be a matcher" do
        argument_expecatation = Spec::Mocks::ArgumentExpectation.new([])
        obj = mock("matcher")
        obj.should_receive(:respond_to?).with(:matches?).and_return(true)
        obj.should_receive(:respond_to?).with(:description).and_return(false)
        argument_expecatation.is_matcher?(obj).should be_false
      end
    end
  end
end

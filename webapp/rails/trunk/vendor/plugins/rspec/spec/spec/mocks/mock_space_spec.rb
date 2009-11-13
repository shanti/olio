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
require 'spec/mocks'

module Spec
  module Mocks
    describe Space do
      before :each do
        @space = Space.new
        klazz = Class.new do
          def rspec_verify
            @verified = true
          end
          def verified?
            @verified
          end
          def rspec_reset
            @reset = true
          end
          def reset?
            @reset
          end
        end
        @m1 = klazz.new
        @m2 = klazz.new
      end
      it "should verify all mocks within" do
        @space.add(@m1)
        @space.add(@m2)
        @space.verify_all
        @m1.should be_verified
        @m2.should be_verified
      end
      it "should reset all mocks within" do
        @space.add(m1 = mock("mock1"))
        @space.add(m2 = mock("mock2"))
        m1.should_receive(:rspec_reset)
        m2.should_receive(:rspec_reset)
        @space.reset_all
      end
      it "should clear internal mocks on reset_all" do
        @space.add(m = mock("mock"))
        @space.reset_all
        @space.instance_eval { mocks.empty? }.should be_true
      end
      it "should only add an instance once" do
        @space.add(m1 = mock("mock1"))
        @space.add(m1)
        m1.should_receive(:rspec_verify)
        @space.verify_all
      end
    end
  end
end
    

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

    describe "an expectation set on nil" do
      
      it "should issue a warning with file and line number information" do
        expected_warning = "An expectation of :foo was set on nil. Called from #{__FILE__}:#{__LINE__+3}. Use allow_message_expectations_on_nil to disable warnings."
        Kernel.should_receive(:warn).with(expected_warning)

        nil.should_receive(:foo)
        nil.foo
      end
      
      it "should issue a warning when the expectation is negative" do
        Kernel.should_receive(:warn)

        nil.should_not_receive(:foo)
      end
      
      it "should not issue a warning when expectations are set to be allowed" do
        allow_message_expectations_on_nil
        Kernel.should_not_receive(:warn)
        
        nil.should_receive(:foo)
        nil.should_not_receive(:bar)
        nil.foo
      end

    end
    
    describe "#allow_message_expectations_on_nil" do
      
      it "should not effect subsequent examples" do
        example_group = Class.new(ExampleGroup)
        example_group.it("when called in one example that doesn't end up setting an expectation on nil") do
                        allow_message_expectations_on_nil
                      end
        example_group.it("should not effect the next exapmle ran") do
                        Kernel.should_receive(:warn)
                        nil.should_receive(:foo)
                        nil.foo
                      end
                              
        example_group.run.should be_true
                  
      end

    end
    
  end
end

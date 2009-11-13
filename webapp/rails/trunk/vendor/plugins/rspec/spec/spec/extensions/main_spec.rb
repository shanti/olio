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
  module Extensions
    describe Main do
      it_should_behave_like "sandboxed rspec_options"
      before(:each) do
        @main = Class.new do; include Main; end
      end

      after(:each) do
        $rspec_story_steps = @original_rspec_story_steps
      end

      it "should create an Options object" do
        @main.send(:rspec_options).should be_instance_of(Spec::Runner::Options)
        @main.send(:rspec_options).should === $rspec_options
      end
  
      specify {@main.should respond_to(:describe)}
      specify {@main.should respond_to(:context)}

      it "should raise when no block given to describe" do
        lambda { @main.describe "foo" }.should raise_error(ArgumentError)
      end

      it "should raise when no description given to describe" do
        lambda { @main.describe do; end }.should raise_error(ArgumentError)
      end

      it "should registered ExampleGroups by default" do
        example_group = @main.describe("The ExampleGroup") do end
        rspec_options.example_groups.should include(example_group)
      end

      it "should not run unregistered ExampleGroups" do
        example_group = @main.describe("The ExampleGroup") do
          unregister
        end

        rspec_options.example_groups.should_not include(example_group)
      end
      
      it "should create a shared ExampleGroup with share_examples_for" do
        group = @main.share_examples_for "all things" do end
        group.should be_an_instance_of(Spec::Example::SharedExampleGroup)
      end
      
      describe "#share_as" do
        before(:each) do
          $share_as_examples_example_module_number ||= 1
          $share_as_examples_example_module_number += 1
          t = Time.new.to_i
          @group_name = "Group#{$share_as_examples_example_module_number}"
        end

        it "should create a shared ExampleGroup" do
          group = @main.share_as @group_name do end
          group.should be_an_instance_of(Spec::Example::SharedExampleGroup)
        end
        
        it "should create a constant that points to a Module" do
          group = @main.share_as @group_name do end
          Object.const_get(@group_name).should equal(group)
        end
        
        it "should bark if you pass it something not-constantizable" do
          lambda do
            @group = @main.share_as "Non Constant" do end
          end.should raise_error(NameError, /The first argument to share_as must be a legal name for a constant/)
        end
        
      end
    end
  end
end
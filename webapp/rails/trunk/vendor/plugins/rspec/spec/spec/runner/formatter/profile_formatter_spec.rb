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
require File.dirname(__FILE__) + '/../../../spec_helper.rb'
require 'spec/runner/formatter/profile_formatter'

module Spec
  module Runner
    module Formatter
      describe ProfileFormatter do
        attr_reader :io, :formatter
        before(:each) do
          @io = StringIO.new
          options = mock('options')
          options.stub!(:colour).and_return(true)
          @formatter = ProfileFormatter.new(options, io)
        end
        
        it "should print a heading" do
          formatter.start(0)
          io.string.should eql("Profiling enabled.\n")
        end
        
        it "should record the current time when starting a new example" do
          now = Time.now
          Time.stub!(:now).and_return(now)
          formatter.example_started('should foo')
          formatter.instance_variable_get("@time").should == now
        end
        
        it "should correctly record a passed example" do
          now = Time.now
          Time.stub!(:now).and_return(now)
          parent_example_group = Class.new(ExampleGroup).describe('Parent')
          child_example_group = Class.new(parent_example_group).describe('Child')

          formatter.add_example_group(child_example_group)
          
          formatter.example_started('when foo')
          Time.stub!(:now).and_return(now+1)
          formatter.example_passed(stub('foo', :description => 'i like ice cream'))

          formatter.start_dump
          io.string.should include('Parent Child')
        end
        
        it "should sort the results in descending order" do
          formatter.instance_variable_set("@example_times", [['a', 'a', 0.1], ['b', 'b', 0.3], ['c', 'c', 0.2]])
          formatter.start_dump
          formatter.instance_variable_get("@example_times").should == [ ['b', 'b', 0.3], ['c', 'c', 0.2], ['a', 'a', 0.1]]
        end
        
        it "should print the top 10 results" do
          example_group = Class.new(::Spec::Example::ExampleGroup).describe("ExampleGroup")
          formatter.add_example_group(example_group)
          formatter.instance_variable_set("@time", Time.now)
          
          15.times do 
            formatter.example_passed(stub('foo', :description => 'i like ice cream'))
          end
          
          io.should_receive(:print).exactly(10)
          formatter.start_dump
        end
      end
    end
  end
end
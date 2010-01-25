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
require File.dirname(__FILE__) + '/spec_helper'
require File.dirname(__FILE__) + '/stack'
require File.dirname(__FILE__) + '/shared_stack_examples'

describe Stack do
  
  before(:each) do
    @stack = Stack.new
  end

  describe "(empty)" do

    it { @stack.should be_empty }
  
    it_should_behave_like "non-full Stack"
  
    it "should complain when sent #peek" do
      lambda { @stack.peek }.should raise_error(StackUnderflowError)
    end
  
    it "should complain when sent #pop" do
      lambda { @stack.pop }.should raise_error(StackUnderflowError)
    end

  end

  describe "(with one item)" do
    
    before(:each) do
      @stack.push 3
      @last_item_added = 3
    end

    it_should_behave_like "non-empty Stack"
    it_should_behave_like "non-full Stack"

  end

  describe "(with one item less than capacity)" do
    
    before(:each) do
      (1..9).each { |i| @stack.push i }
      @last_item_added = 9
    end
  
    it_should_behave_like "non-empty Stack"
    it_should_behave_like "non-full Stack"
  end

  describe "(full)" do
    
    before(:each) do
      (1..10).each { |i| @stack.push i }
      @last_item_added = 10
    end

    it { @stack.should be_full }  

    it_should_behave_like "non-empty Stack"

    it "should complain on #push" do
      lambda { @stack.push Object.new }.should raise_error(StackOverflowError)
    end
  
  end

end

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

class StackExamples < Spec::ExampleGroup
  describe(Stack)
  before(:each) do
    @stack = Stack.new
  end
end

class EmptyStackExamples < StackExamples
  describe("when empty")
  it "should be empty" do
    @stack.should be_empty
  end
end

class AlmostFullStackExamples < StackExamples
  describe("when almost full")
  before(:each) do
    (1..9).each {|n| @stack.push n}
  end
  it "should be full" do
    @stack.should_not be_full
  end
end

class FullStackExamples < StackExamples
  describe("when full")
  before(:each) do
    (1..10).each {|n| @stack.push n}
  end
  it "should be full" do
    @stack.should be_full
  end
end
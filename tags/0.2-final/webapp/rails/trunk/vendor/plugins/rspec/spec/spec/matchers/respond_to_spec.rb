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

describe "should respond_to(:sym)" do
  
  it "should pass if target responds to :sym" do
    Object.new.should respond_to(:methods)
  end
  
  it "should fail target does not respond to :sym" do
    lambda {
      "this string".should respond_to(:some_method)
    }.should fail_with("expected \"this string\" to respond to :some_method")
  end
  
end

describe "should respond_to(message1, message2)" do
  
  it "should pass if target responds to both messages" do
    Object.new.should respond_to('methods', 'inspect')
  end
  
  it "should fail target does not respond to first message" do
    lambda {
      Object.new.should respond_to('method_one', 'inspect')
    }.should fail_with(/expected #<Object:.*> to respond to "method_one"/)
  end
  
  it "should fail target does not respond to second message" do
    lambda {
      Object.new.should respond_to('inspect', 'method_one')
    }.should fail_with(/expected #<Object:.*> to respond to "method_one"/)
  end
  
  it "should fail target does not respond to either message" do
    lambda {
      Object.new.should respond_to('method_one', 'method_two')
    }.should fail_with(/expected #<Object:.*> to respond to "method_one", "method_two"/)
  end
end

describe "should_not respond_to(:sym)" do
  
  it "should pass if target does not respond to :sym" do
    Object.new.should_not respond_to(:some_method)
  end
  
  it "should fail target responds to :sym" do
    lambda {
      Object.new.should_not respond_to(:methods)
    }.should fail_with(/expected #<Object:.*> not to respond to :methods/)
  end
  
end

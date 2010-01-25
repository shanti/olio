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

describe "should have_sym(*args)" do
  it "should pass if #has_sym?(*args) returns true" do
    {:a => "A"}.should have_key(:a)
  end

  it "should fail if #has_sym?(*args) returns false" do
    lambda {
      {:b => "B"}.should have_key(:a)
    }.should fail_with("expected #has_key?(:a) to return true, got false")
  end

  it "should fail if target does not respond to #has_sym?" do
    lambda {
      Object.new.should have_key(:a)
    }.should raise_error(NoMethodError)
  end
  
  it "should reraise an exception thrown in #has_sym?(*args)" do
    o = Object.new
    def o.has_sym?(*args)
      raise "Funky exception"
    end
    lambda { o.should have_sym(:foo) }.should raise_error("Funky exception")
  end
end

describe "should_not have_sym(*args)" do
  it "should pass if #has_sym?(*args) returns false" do
    {:a => "A"}.should_not have_key(:b)
  end

  it "should fail if #has_sym?(*args) returns true" do
    lambda {
      {:a => "A"}.should_not have_key(:a)
    }.should fail_with("expected #has_key?(:a) to return false, got true")
  end

  it "should fail if target does not respond to #has_sym?" do
    lambda {
      Object.new.should have_key(:a)
    }.should raise_error(NoMethodError)
  end
  
  it "should reraise an exception thrown in #has_sym?(*args)" do
    o = Object.new
    def o.has_sym?(*args)
      raise "Funky exception"
    end
    lambda { o.should_not have_sym(:foo) }.should raise_error("Funky exception")
  end
end

describe "has" do
  it "should work when the target implements #send" do
    o = {:a => "A"}
    def o.send(*args); raise "DOH! Library developers shouldn't use #send!" end
    lambda {
      o.should have_key(:a)
    }.should_not raise_error
  end
end

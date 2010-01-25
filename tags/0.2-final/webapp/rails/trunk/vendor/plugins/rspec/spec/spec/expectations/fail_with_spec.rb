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

describe Spec::Expectations, "#fail_with with no diff" do
  before(:each) do
    @old_differ = Spec::Expectations.differ
    Spec::Expectations.differ = nil
  end
  
  it "should handle just a message" do
    lambda {
      Spec::Expectations.fail_with "the message"
    }.should fail_with("the message")
  end
  
  it "should handle an Array" do
    lambda {
      Spec::Expectations.fail_with ["the message","expected","actual"]
    }.should fail_with("the message")
  end

  after(:each) do
    Spec::Expectations.differ = @old_differ
  end
end

describe Spec::Expectations, "#fail_with with diff" do
  before(:each) do
    @old_differ = Spec::Expectations.differ
    @differ = mock("differ")
    Spec::Expectations.differ = @differ
  end
  
  it "should not call differ if no expected/actual" do
    lambda {
      Spec::Expectations.fail_with "the message"
    }.should fail_with("the message")
  end
  
  it "should call differ if expected/actual are presented separately" do
    @differ.should_receive(:diff_as_string).and_return("diff")
    lambda {
      Spec::Expectations.fail_with "the message", "expected", "actual"
    }.should fail_with("the message\nDiff:diff")
  end
  
  it "should call differ if expected/actual are not strings" do
    @differ.should_receive(:diff_as_object).and_return("diff")
    lambda {
      Spec::Expectations.fail_with "the message", :expected, :actual
    }.should fail_with("the message\nDiff:diff")
  end
  
  it "should not call differ if expected or actual are procs" do
    @differ.should_not_receive(:diff_as_string)
    @differ.should_not_receive(:diff_as_object)
    lambda {
      Spec::Expectations.fail_with "the message", lambda {}, lambda {}
    }.should fail_with("the message")
  end
  
  it "should call differ if expected/actual are presented in an Array with message" do
    @differ.should_receive(:diff_as_string).with("actual","expected").and_return("diff")
    lambda {
      Spec::Expectations.fail_with(["the message", "expected", "actual"])
    }.should fail_with(/the message\nDiff:diff/)
  end
  
  after(:each) do
    Spec::Expectations.differ = @old_differ
  end
end

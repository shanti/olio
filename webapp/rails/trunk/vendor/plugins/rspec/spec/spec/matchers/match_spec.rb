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

describe "should match(expected)" do
  it "should pass when target (String) matches expected (Regexp)" do
    "string".should match(/tri/)
  end

  it "should fail when target (String) does not match expected (Regexp)" do
    lambda {
      "string".should match(/rings/)
    }.should fail
  end
  
  it "should provide message, expected and actual on failure" do
    matcher = match(/rings/)
    matcher.matches?("string")
    matcher.failure_message.should == ["expected \"string\" to match /rings/", /rings/, "string"]
  end
end

describe "should_not match(expected)" do
  it "should pass when target (String) matches does not match (Regexp)" do
    "string".should_not match(/rings/)
  end

  it "should fail when target (String) matches expected (Regexp)" do
    lambda {
      "string".should_not match(/tri/)
    }.should fail
  end

  it "should provide message, expected and actual on failure" do
    matcher = match(/tri/)
    matcher.matches?("string")
    matcher.negative_failure_message.should == ["expected \"string\" not to match /tri/", /tri/, "string"]
  end
end

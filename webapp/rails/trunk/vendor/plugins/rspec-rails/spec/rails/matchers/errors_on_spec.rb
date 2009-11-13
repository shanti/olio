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
require File.dirname(__FILE__) + '/../../spec_helper'

describe "error_on" do
  it "should provide a description including the name of what the error is on" do
    have(1).error_on(:whatever).description.should == "should have 1 error on :whatever"
  end
  
  it "should provide a failure message including the number actually given" do
    lambda {
      [].should have(1).error_on(:whatever)
    }.should fail_with("expected 1 error on :whatever, got 0")
  end
end

describe "errors_on" do
  it "should provide a description including the name of what the error is on" do
    have(2).errors_on(:whatever).description.should == "should have 2 errors on :whatever"
  end
  
  it "should provide a failure message including the number actually given" do
    lambda {
      [1].should have(3).errors_on(:whatever)
    }.should fail_with("expected 3 errors on :whatever, got 1")
  end
end
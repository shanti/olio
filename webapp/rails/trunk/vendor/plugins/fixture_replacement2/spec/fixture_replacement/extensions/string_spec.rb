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
require File.dirname(__FILE__) + "/../../spec_helper"

describe "String.random" do
  it "should not be the same as another randomly generated string" do
    String.random.should_not == String.random
  end
  
  it "should by default be 10 characters long" do
    String.random.size.should == 10
  end
  
  it "should be able to specify the length of the random string" do
    String.random(100).size.should == 100
  end
  
  it "should only generate lowercase letters" do
    s = String.random(100)
    s.upcase.should == s.swapcase
  end
end
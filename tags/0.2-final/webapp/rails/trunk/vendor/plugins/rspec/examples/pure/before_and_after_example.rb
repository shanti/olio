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
$global = 0

describe "State created in before(:all)" do
  before :all do
    @sideeffect = 1
    $global +=1
  end

  before :each do
    @isolated = 1
  end
  
  it "should be accessible from example" do
    @sideeffect.should == 1
    $global.should == 1
    @isolated.should == 1

    @sideeffect += 1
    @isolated += 1
  end

  it "should not have sideffects" do
    @sideeffect.should == 1
    $global.should == 2
    @isolated.should == 1

    @sideeffect += 1
    @isolated += 1
  end

  after :each do
    $global += 1
  end
  
  after :all do
    $global.should == 3
    $global = 0
  end
end

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

# Run spec w/ -fs to see the output of this file

describe "Failing examples with no descriptions" do
  
  # description is auto-generated as "should equal(5)" based on the last #should
  it do
    3.should equal(2)
    5.should equal(5)
  end
  
  it { 3.should be > 5 }
  
  it { ["a"].should include("b") }
  
  it { [1,2,3].should_not respond_to(:size) }
  
end

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

class MockableClass
  def self.find id
    return :original_return
  end
end

describe "A partial mock" do

  it "should work at the class level" do
    MockableClass.should_receive(:find).with(1).and_return {:stub_return}
    MockableClass.find(1).should equal(:stub_return)
  end

  it "should revert to the original after each spec" do
    MockableClass.find(1).should equal(:original_return)
  end

  it "can be mocked w/ ordering" do
    MockableClass.should_receive(:msg_1).ordered
    MockableClass.should_receive(:msg_2).ordered
    MockableClass.should_receive(:msg_3).ordered
    MockableClass.msg_1
    MockableClass.msg_2
    MockableClass.msg_3
  end

end

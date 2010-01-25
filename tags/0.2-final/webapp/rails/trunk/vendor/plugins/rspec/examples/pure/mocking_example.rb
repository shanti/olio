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

describe "A consumer of a mock" do
  it "should be able to send messages to the mock" do
    mock = mock("poke me")
    mock.should_receive(:poke)
    mock.poke
  end
end

describe "a mock" do
  it "should be able to mock the same message twice w/ different args" do
    mock = mock("mock")
    mock.should_receive(:msg).with(:arg1).and_return(:val1)
    mock.should_receive(:msg).with(:arg2).and_return(:val2)
    mock.msg(:arg1).should eql(:val1)
    mock.msg(:arg2).should eql(:val2)
  end

  it "should be able to mock the same message twice w/ different args in reverse order" do
    mock = mock("mock")
    mock.should_receive(:msg).with(:arg1).and_return(:val1)
    mock.should_receive(:msg).with(:arg2).and_return(:val2)
    mock.msg(:arg2).should eql(:val2)
    mock.msg(:arg1).should eql(:val1)
  end
end

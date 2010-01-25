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

describe "Mocker" do

  it "should be able to call mock()" do
    mock = mock("poke me")
    mock.should_receive(:poke)
    mock.poke
  end

  it "should fail when expected message not received" do
    mock = mock("poke me")
    mock.should_receive(:poke)
  end
  
  it "should fail when messages are received out of order" do
    mock = mock("one two three")
    mock.should_receive(:one).ordered
    mock.should_receive(:two).ordered
    mock.should_receive(:three).ordered
    mock.one
    mock.three
    mock.two
  end

  it "should get yelled at when sending unexpected messages" do
    mock = mock("don't talk to me")
    mock.should_not_receive(:any_message_at_all)
    mock.any_message_at_all
  end

  it "has a bug we need to fix" do
    pending "here is the bug" do
      # Actually, no. It's fixed. This will fail because it passes :-)
      mock = mock("Bug")
      mock.should_receive(:hello)
      mock.hello
    end
  end
end

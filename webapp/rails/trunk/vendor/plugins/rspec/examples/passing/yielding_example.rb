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

class MessageAppender
  
  def initialize(appendage)
    @appendage = appendage
  end
  
  def append_to(message)
    if_told_to_yield do
      message << @appendage
    end
  end
    
end

describe "a message expectation yielding to a block" do
  it "should yield if told to" do
    appender = MessageAppender.new("appended to")
    appender.should_receive(:if_told_to_yield).and_yield
    message = ""
    appender.append_to(message)
    message.should == "appended to"
  end

  it "should not yield if not told to" do
    appender = MessageAppender.new("appended to")
    appender.should_receive(:if_told_to_yield)
    message = ""
    appender.append_to(message)
    message.should == ""
  end
end
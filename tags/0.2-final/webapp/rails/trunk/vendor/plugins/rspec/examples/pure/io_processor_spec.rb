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
require File.dirname(__FILE__) + '/io_processor'
require 'stringio'

describe "An IoProcessor" do
  before(:each) do
    @processor = IoProcessor.new
  end

  it "should raise nothing when the file is exactly 32 bytes" do
    lambda {
      @processor.process(StringIO.new("z"*32))
    }.should_not raise_error
  end

  it "should raise an exception when the file length is less than 32 bytes" do
    lambda {
      @processor.process(StringIO.new("z"*31))
    }.should raise_error(DataTooShort)
  end
end

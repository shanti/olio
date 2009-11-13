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
require File.dirname(__FILE__) + '/story_helper'

require 'spec/story'

describe Kernel, "#Story" do
  before(:each) do
    Kernel.stub!(:at_exit)
  end

  it "should delegate to ::Spec::Story::Runner.story_runner" do
    ::Spec::Story::Runner.story_runner.should_receive(:Story)
    story = Story("title","narrative"){}
  end
end

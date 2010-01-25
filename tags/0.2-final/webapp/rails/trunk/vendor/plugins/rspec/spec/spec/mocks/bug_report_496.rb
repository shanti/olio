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
require File.dirname(__FILE__) + '/../../spec_helper.rb'

module BugReport496
  class BaseClass
  end

  class SubClass < BaseClass
  end

  describe "a message expectation on a base class object" do
    it "should correctly pick up message sent to it subclass" do
      pending("fix for http://rspec.lighthouseapp.com/projects/5645/tickets/496") do
        BaseClass.should_receive(:new).once
        SubClass.new
      end
    end
  end
end


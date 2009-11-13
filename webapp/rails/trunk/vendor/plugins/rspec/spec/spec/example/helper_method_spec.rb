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
require File.dirname(__FILE__) + '/../../spec_helper'

# This was added to prove that http://rspec.lighthouseapp.com/projects/5645/tickets/211
# was fixed in ruby 1.9.1

module HelperMethodExample
  describe "a helper method" do
    def helper_method
      "received call"
    end
  
    it "is available to examples in the same group" do
      helper_method.should == "received call"
    end
    
    describe "from a nested group" do
      it "is available to examples in a nested group" do
        helper_method.should == "received call"
      end
    end
    
  end
end


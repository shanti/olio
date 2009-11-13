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

module Spec
  module Runner
    describe "NoisyBacktraceTweaker" do
      before(:each) do
        @error = RuntimeError.new
        @tweaker = NoisyBacktraceTweaker.new
      end
        
      it "should leave anything in lib spec dir" do
        ["expectations", "mocks", "runner", "stubs"].each do |child|
          @error.set_backtrace(["/lib/spec/#{child}/anything.rb"])
          @tweaker.tweak_backtrace(@error)
          @error.backtrace.should_not be_empty
        end
      end

      it "should leave anything in spec dir" do
        @error.set_backtrace(["/lib/spec/expectations/anything.rb"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should_not be_empty
      end

      it "should leave bin spec" do
        @error.set_backtrace(["bin/spec:"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should_not be_empty
      end

      it "should not barf on nil backtrace" do
        lambda do
          @tweaker.tweak_backtrace(@error)
        end.should_not raise_error
      end
      
      it "should clean up double slashes" do
        @error.set_backtrace(["/a//b/c//d.rb"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should include("/a/b/c/d.rb")
      end
      
    end
  end
end

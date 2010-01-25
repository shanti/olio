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
    describe "QuietBacktraceTweaker" do
      before(:each) do
        @error = RuntimeError.new
        @tweaker = QuietBacktraceTweaker.new
      end

      it "should not barf on nil backtrace" do
        lambda do
          @tweaker.tweak_backtrace(@error)
        end.should_not raise_error
      end

      it "should remove anything from textmate ruby bundle" do
        @error.set_backtrace(["/Applications/TextMate.app/Contents/SharedSupport/Bundles/Ruby.tmbundle/Support/tmruby.rb:147"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should be_empty
      end

      it "should remove anything in lib spec dir" do
        ["expectations", "mocks", "runner"].each do |child|
          element="/lib/spec/#{child}/anything.rb"
          @error.set_backtrace([element])
          @tweaker.tweak_backtrace(@error)
          unless (@error.backtrace.empty?)
            raise("Should have tweaked away '#{element}'")
          end
        end
      end

      it "should remove mock_frameworks/rspec" do
        element = "mock_frameworks/rspec"
        @error.set_backtrace([element])
        @tweaker.tweak_backtrace(@error)
        unless (@error.backtrace.empty?)
          raise("Should have tweaked away '#{element}'")
        end
      end

      it "should remove bin spec" do
        @error.set_backtrace(["bin/spec:"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should be_empty
      end
      
      it "should clean up double slashes" do
        @error.set_backtrace(["/a//b/c//d.rb"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should include("/a/b/c/d.rb")
      end

      it "should gracefully handle backtraces with newlines" do
        @error.set_backtrace(["we like\nbin/spec:\nnewlines"])
        @tweaker.tweak_backtrace(@error)
        @error.backtrace.should include("we like\nnewlines")
      end
    end
  end
end

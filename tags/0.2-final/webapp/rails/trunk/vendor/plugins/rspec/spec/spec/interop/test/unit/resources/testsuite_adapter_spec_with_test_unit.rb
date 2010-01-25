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
rspec_lib = File.dirname(__FILE__) + "/../../../../../../lib"
$:.unshift rspec_lib unless $:.include?(rspec_lib)
require "test/unit"
require "spec"

module Test
  module Unit
    describe TestSuiteAdapter do
      def create_adapter(group)
        TestSuiteAdapter.new(group)
      end

      describe "#size" do
        it "should return the number of examples in the example group" do
          group = Class.new(Spec::ExampleGroup) do
            describe("some examples")
            it("bar") {}
            it("baz") {}
          end
          adapter = create_adapter(group)
          adapter.size.should == 2
        end
      end

      describe "#delete" do
        it "should do nothing" do
          group = Class.new(Spec::ExampleGroup) do
            describe("Some Examples")
            it("does something") {}
          end
          adapter = create_adapter(group)
          adapter.delete(adapter.examples.first)
          adapter.should be_empty
        end
      end
    end
  end
end

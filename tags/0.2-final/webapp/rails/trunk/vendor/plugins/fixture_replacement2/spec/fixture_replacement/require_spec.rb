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
require File.dirname(__FILE__) + "/../spec_helper"

context = self

describe "FixtureReplacement" do
  it "should raise the error: 'Error in FixtureReplacement plugin: ..." do
    context.stub!(:require).and_raise(LoadError.new("could not find file!"))
    lambda {
      load File.dirname(__FILE__) + "/../../lib/fixture_replacement.rb"
    }.should raise_error(LoadError, "Error in FixtureReplacement Plugin: could not find file!")
  end
  
  it "should raise the error if the error is not a LoadError" do
    context.stub!(:require).and_raise(StandardError.new("foo"))
    lambda {
      load File.dirname(__FILE__) + "/../../lib/fixture_replacement.rb"
    }.should raise_error(StandardError, "foo")
  end
end
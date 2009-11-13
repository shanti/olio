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

describe "The anything() mock argument constraint matcher" do
  specify { anything.should == Object.new }
  specify { anything.should == Class }
  specify { anything.should == 1 }
  specify { anything.should == "a string" }
  specify { anything.should == :a_symbol }
end

describe "The boolean() mock argument constraint matcher" do
  specify { boolean.should == true }
  specify { boolean.should == false }
  specify { boolean.should_not == Object.new }
  specify { boolean.should_not == Class }
  specify { boolean.should_not == 1 }
  specify { boolean.should_not == "a string" }
  specify { boolean.should_not == :a_symbol }
end

describe "The an_instance_of() mock argument constraint matcher" do
  # NOTE - this is implemented as a predicate_matcher - see example_group_methods.rb
  specify { an_instance_of(String).should == "string"  }
end

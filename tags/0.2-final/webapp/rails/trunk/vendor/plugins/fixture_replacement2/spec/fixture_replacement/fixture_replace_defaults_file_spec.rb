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

describe "FixtureReplacement.defaults_file" do
  before :each do
    remove_constant(:RAILS_ROOT)
    @rails_root = "script/../config/../config/.."
    Object.send(:const_set, :RAILS_ROOT, @rails_root)
    FixtureReplacement.instance_variable_set("@defaults_file", nil)
  end
  
  after :each do
    remove_constant(:RAILS_ROOT)
  end
  
  def remove_constant(constant)
    Object.send(:remove_const, constant) if Object.send(:const_defined?, constant)
  end  
  
  it "should be RAILS_ROOT/db/example_data.rb by default" do
    FixtureReplacement.defaults_file.should == "#{@rails_root}/db/example_data.rb"
  end
  
  it "should be foo.rb if set" do
    FixtureReplacement.defaults_file = "foo.rb"
    FixtureReplacement.defaults_file.should == "foo.rb"
  end
end
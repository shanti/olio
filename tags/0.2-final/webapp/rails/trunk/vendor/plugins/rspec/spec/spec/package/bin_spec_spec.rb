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
require File.dirname(__FILE__) + '/../../ruby_forker'

describe "The bin/spec script" do
  include RubyForker
  
  it "should have no warnings" do
    pending "Hangs on JRuby" if PLATFORM =~ /java/
    spec_path = "#{File.dirname(__FILE__)}/../../../bin/spec"

    output = ruby "-w #{spec_path} --help 2>&1"
    output.should_not =~ /warning/n
  end
  
  it "should show the help w/ no args" do
    pending "Hangs on JRuby" if PLATFORM =~ /java/
    spec_path = "#{File.dirname(__FILE__)}/../../../bin/spec"

    output = ruby "-w #{spec_path} 2>&1"
    output.should =~ /^Usage: spec/
  end
end

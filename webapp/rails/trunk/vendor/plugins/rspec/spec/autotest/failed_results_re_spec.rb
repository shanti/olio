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
require File.dirname(__FILE__) + "/autotest_helper"

describe "failed_results_re" do
  it "should match a failure" do
    re = Autotest::Rspec.new.failed_results_re
    re =~ "1)\n'this example' FAILED\nreason\n/path.rb:37:\n\n"
    $1.should == "this example"
    $2.should == "reason\n/path.rb:37:"
  end

  it "should match an Error" do
    re = Autotest::Rspec.new.failed_results_re
    re =~ "1)\nRuntimeError in 'this example'\nreason\n/path.rb:37:\n\n"
    $1.should == "this example"
    $2.should == "reason\n/path.rb:37:"
  end

  it "should match an Error that doesn't end in Error" do
    re = Autotest::Rspec.new.failed_results_re
    re =~ "1)\nInvalidArgument in 'this example'\nreason\n/path.rb:37:\n\n"
    $1.should == "this example"
    $2.should == "reason\n/path.rb:37:"
  end
end
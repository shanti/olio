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

describe "should satisfy { block }" do
  it "should pass if block returns true" do
    true.should satisfy { |val| val }
    true.should satisfy do |val|
      val
    end
  end

  it "should fail if block returns false" do
    lambda {
      false.should satisfy { |val| val }
    }.should fail_with("expected false to satisfy block")
    lambda do
      false.should satisfy do |val|
        val
      end
    end.should fail_with("expected false to satisfy block")
  end
end

describe "should_not satisfy { block }" do
  it "should pass if block returns false" do
    false.should_not satisfy { |val| val }
    false.should_not satisfy do |val|
      val
    end
  end

  it "should fail if block returns true" do
    lambda {
      true.should_not satisfy { |val| val }
    }.should fail_with("expected true not to satisfy block")
  end
end

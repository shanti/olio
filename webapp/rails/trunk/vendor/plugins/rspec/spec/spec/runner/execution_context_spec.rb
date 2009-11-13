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

describe "ExecutionContext" do
  
  it "should provide duck_type()" do
    dt = duck_type(:length)
    dt.should be_an_instance_of(Spec::Mocks::DuckTypeArgConstraint)
    dt.matches?([]).should be_true
  end
  
  it "should provide hash_including" do
    hi = hash_including(:a => 1)
    hi.should be_an_instance_of(Spec::Mocks::HashIncludingConstraint)
    hi.matches?(:a => 1).should be_true
  end
  
  it "should violate when violated()" do
    lambda do
      violated
    end.should raise_error(Spec::Expectations::ExpectationNotMetError)
  end

  it "should provide mock()" do
    mock("thing").should be_an_instance_of(Spec::Mocks::Mock)
  end

  it "should provide stub()" do
    thing_stub = stub("thing").should be_an_instance_of(Spec::Mocks::Mock)
  end
  
  it "should add method stubs to stub()" do
    thing_stub = stub("thing", :a => "A", :b => "B")
    thing_stub.a.should == "A"
    thing_stub.b.should == "B"
  end

end

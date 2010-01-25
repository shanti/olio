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
  module Mocks
    describe "mock failure" do
      
      it "should tell you when it receives the right message with the wrong args" do
        m = mock("foo")
        m.should_receive(:bar).with("message")
        lambda {
          m.bar("different message")
        }.should raise_error(Spec::Mocks::MockExpectationError, %Q{Mock 'foo' expected :bar with ("message") but received it with ("different message")})
        m.bar("message") # allows the spec to pass
      end

      it "should tell you when it receives the right message with the wrong args if you stub the method" do
        pending("fix bug 15719")
        # NOTE - for whatever reason, if you use a the block style of pending here,
        # rcov gets unhappy. Don't know why yet.
        m = mock("foo")
        m.stub!(:bar)
        m.should_receive(:bar).with("message")
        lambda {
          m.bar("different message")
        }.should raise_error(Spec::Mocks::MockExpectationError, %Q{Mock 'foo' expected :bar with ("message") but received it with ("different message")})
        m.bar("message") # allows the spec to pass
      end
    end
  end
end
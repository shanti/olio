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
    describe "calling :should_receive with an options hash" do
      it "should report the file and line submitted with :expected_from" do
        begin
          mock = Spec::Mocks::Mock.new("a mock")
          mock.should_receive(:message, :expected_from => "/path/to/blah.ext:37")
          mock.rspec_verify
        rescue => e
        ensure
          e.backtrace.to_s.should =~ /\/path\/to\/blah.ext:37/m
        end
      end

      it "should use the message supplied with :message" do
        lambda {
          m = Spec::Mocks::Mock.new("a mock")
          m.should_receive(:message, :message => "recebi nada")
          m.rspec_verify
        }.should raise_error("recebi nada")
      end
      
      it "should use the message supplied with :message after a similar stub" do
        lambda {
          m = Spec::Mocks::Mock.new("a mock")
          m.stub!(:message)
          m.should_receive(:message, :message => "from mock")
          m.rspec_verify
        }.should raise_error("from mock")
      end
    end
  end
end

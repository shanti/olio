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

module ActionController
  describe "Rescue", "#rescue_action in default mode" do
    before(:each) do
      @fixture = Object.new
      @fixture.extend ActionController::Rescue
      class << @fixture
        public :rescue_action
      end
    end

    it "should raise the passed in exception so examples fail fast" do
      proc {@fixture.rescue_action(RuntimeError.new("Foobar"))}.should raise_error(RuntimeError, "Foobar")
    end
  end

  class RescueOverriddenController < ActionController::Base
    def rescue_action(error)
      "successfully overridden"
    end
  end

  describe "Rescue", "#rescue_action, when overridden" do
    before(:each) do
      @fixture = RescueOverriddenController.new
    end

    it "should do whatever the overridden method does" do
      @fixture.rescue_action(RuntimeError.new("Foobar")).should == "successfully overridden"
    end
  end

  class SearchController < ActionController::Base
  end

  describe "Rescue", "#rescue_action when told to use rails error handling" do
    before(:each) do
      @controller = SearchController.new
      @controller.use_rails_error_handling!
      class << @controller
        public :rescue_action
      end
    end

    it "should use Rails exception handling" do
      exception = RuntimeError.new("The Error")
      exception.stub!(:backtrace).and_return(caller)
      @controller.should_receive(:rescue_action_locally).with(exception)

      @controller.rescue_action(exception)
    end
  end
end

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
require File.dirname(__FILE__) + "/../../../spec_helper"

module FixtureReplacementController
  describe MethodGenerator do
    before :each do
      @user_attributes = mock("UserAttributeCollection")
      @user_attributes.stub!(:merge!)
      @module = mock("FixtureReplacement")
      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      @generator = MethodGenerator.new(@user_attributes)
    end
    
    it "should have the class method generate_methods" do
      MethodGenerator.should respond_to(:generate_methods)
    end
        
    it "should be able to respond to generate_default_method" do
      @generator.should respond_to(:generate_default_method)
    end

    it "should respond to generate_create_method" do
      @generator.should respond_to(:generate_create_method)
    end

    it "should respond to generate_new_method" do
      @generator.should respond_to(:generate_new_method)
    end
  end
end
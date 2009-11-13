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
  describe "MethodGenerator#default_*", :shared => true do
    it "should return a DelayedEvaluationProc" do
      @generator.generate_default_method
      self.send("default_#{@fixture_name}").class.should == DelayedEvaluationProc
    end
    
    it %(should return the special proc, which in turn should return an array 
        of the name of the model ('user') if no params were given) do
      @generator.generate_default_method
      self.send("default_#{@fixture_name}").call.should == [@attributes, {}]
    end
    
    it %(should return the special proc, which in turn should return an array
        of the name of the model ('user') and the params given) do
      @generator.generate_default_method
      self.send("default_#{@fixture_name}", @params_hash).call.should == [@attributes, @params_hash]
    end
    
    it "should generate the method default_user in the module" do
      @generator.generate_default_method
      @module.instance_methods.should include("default_#{@fixture_name}")
    end
  end

  describe MethodGenerator, "default_user" do
    before :each do
      @module = Module.new
      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      
      @struct = OpenStruct.new({:key => "val"})
      @attributes = AttributeCollection.new(:user, :attributes => @struct)
      @generator = MethodGenerator.new(@attributes)
      
      @fixture_name = "user"
      extend @module
      
      @params_hash = {:username => "foo"}
    end
    
    it_should_behave_like "MethodGenerator#default_*"    
  end
  
  describe MethodGenerator, "default_admin" do
    before :each do
      @module = Module.new
      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      
      @struct = OpenStruct.new({:key => "val"})
      @attributes = AttributeCollection.new(:admin, :attributes => @struct)
      @attributes.stub!(:merge!)
      @generator = MethodGenerator.new(@attributes)
      
      @fixture_name = "admin"
      @params_hash = {:username => "scott"}
      extend @module
    end
    
    it_should_behave_like "MethodGenerator#default_*"
  end
end
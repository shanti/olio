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
  describe "MethodGenerator.generate_methods" do
    before :each do
      @attributes = mock AttributeCollection
      AttributeCollection.stub!(:instances).and_return [@attributes]
      @module = mock "A Module"
      ClassFactory.stub!(:fixture_replacement_controller).and_return @module
      @method_generator = mock MethodGenerator
      @method_generator.stub!(:generate_methods)
      MethodGenerator.stub!(:new).and_return @method_generator
    end    
    
    it "should find each of the attributes" do
      AttributeCollection.should_receive(:instances).and_return [@attributes]
      MethodGenerator.generate_methods
    end
    
    it "should create a new MethodGenerator for each attribute" do
      MethodGenerator.should_receive(:new).with(@attributes).and_return @method_generator
      MethodGenerator.generate_methods
    end
    
    it "should generate the methods for each new MethodGenerator created" do
      @method_generator.should_receive(:generate_methods)
      MethodGenerator.generate_methods
    end
  end  
  
  describe MethodGenerator, "generate_methods (the instance method)" do
    before :each do
      @attributes = mock 'AttributeCollection'
      @attributes.stub!(:merge!)
      @module = mock 'A Module'
      ClassFactory.stub!(:fixture_replacement_controller).and_return @module
      
      @generator = MethodGenerator.new(@attributes)
      @generator.stub!(:generate_default_method)
      @generator.stub!(:generate_new_method)
      @generator.stub!(:generate_create_method)
    end
    
    it "should generate the default method" do
      @generator.should_receive(:generate_default_method)
      @generator.generate_methods
    end
    
    it "should generate the new method" do
      @generator.should_receive(:generate_new_method)
      @generator.generate_methods
    end
    
    it "should generate the create method" do
      @generator.should_receive(:generate_create_method)
      @generator.generate_methods
    end
  end  
end

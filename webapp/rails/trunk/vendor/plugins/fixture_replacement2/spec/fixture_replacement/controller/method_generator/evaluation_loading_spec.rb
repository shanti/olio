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
  describe MethodGenerator, "Evaluation loading" do
    before :each do
      @module = Module.new
      extend @module

      item_attributes = lambda do |o|
        o.category = default_category
      end
      
      writing_attributes = lambda do |w|
        w.name = "foo"
      end

      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      @item_attributes = AttributeCollection.new(:item, :attributes => item_attributes)
      @writing_attributes = AttributeCollection.new(:writing, :from => :item, :attributes => writing_attributes, :class => Writing)
      AttributeCollection.new(:category)
    end

    it "should not raise an error if the a default_* method is referenced before it is defined" do
      lambda {
        MethodGenerator.generate_methods
      }.should_not raise_error
    end 
    
    it "should merge the hash with item and writing when new_writing is called" do
      MethodGenerator.generate_methods
      @writing_attributes.should_receive(:merge!)
      new_writing
    end   
    
    it "should merge the has with item and writing when create_writing is called" do
      MethodGenerator.generate_methods
      @writing_attributes.should_receive(:merge!)
      create_writing
    end
  end
end
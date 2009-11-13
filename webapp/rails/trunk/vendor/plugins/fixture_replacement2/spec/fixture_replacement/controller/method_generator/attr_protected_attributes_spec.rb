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
  describe "create_user with attr_protected attributes" do
    before :each do
      @module = Module.new
      extend @module
      
      @attributes = AttributeCollection.new(:admin, :attributes => lambda { |u|
        u.admin_status = true
        u.name = "Scott"
      })

      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      @generator = FixtureReplacementController::MethodGenerator.new(@attributes)
      @generator.generate_new_method
      @generator.generate_create_method
    end
  
    it "should not complain when an apparent mass assignment has happened with default values" do
      lambda {
        create_admin
      }.should_not raise_error
    end
    
    it "should not be a new record" do
      create_admin.should_not be_a_new_record
    end
  
    it "should have admin_status equal to the default value (when it has not been overwritten)" do
      create_admin.admin_status.should == true
    end
  
    it "should have admin_status equal to the overwritten value" do
      create_admin(:admin_status => false).admin_status.should be_false
    end
  
    it "should have the other attributes assigned when the attr_value has been overwritten" do
      create_admin(:admin_status => false).name.should == "Scott"
    end
  
    it "should have the other attributes assigned even when the attr_value has not been overwritten" do
      create_admin.name.should == "Scott"
    end    
  end

  describe "new_user with attr_protected attributes" do
    before :each do
      @module = Module.new
      extend @module
      
      @struct = OpenStruct.new(@hash)
      @attributes = AttributeCollection.new(:admin, :attributes => lambda { |s|
        s.admin_status = true
        s.name = "Scott"
      })

      ClassFactory.stub!(:fixture_replacement_module).and_return @module
      @generator = FixtureReplacementController::MethodGenerator.new(@attributes)
      @generator.generate_new_method
    end
    
    it "should return a new Admin with new_admin" do
      new_admin.should be_a_kind_of(Admin)
    end

    it "should have admin_status equal to the default value (when it has not been overwritten)" do
      new_admin.admin_status.should == true
    end

    it "should have admin_status equal to the overwritten value" do
      new_admin(:admin_status => false).admin_status.should be_false
    end

    it "should have the other attributes assigned when the attr_value has been overwritten" do
      new_admin(:admin_status => false).name.should == "Scott"
    end

    it "should have the other attributes assigned even when the attr_value has not been overwritten" do
      new_admin.name.should == "Scott"
    end    
  end  
end
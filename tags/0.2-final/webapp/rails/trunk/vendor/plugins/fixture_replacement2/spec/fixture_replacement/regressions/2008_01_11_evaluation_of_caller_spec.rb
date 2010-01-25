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
require File.dirname(__FILE__) + "/../../spec_helper"

module FixtureReplacementController
  describe AttributeCollection do
    before :each do
      lambda_expression = nil
      
      @module = Module.new do
        class << self
          include FixtureReplacement::ClassMethods
        end

        lambda_expression = lambda { |o|
          o.bar = a_method
          o.baz = a_baz_method
        }
        
        def a_method
          :bar
        end
        
        def a_baz_method
          :baz
        end
      end

      @attributes = AttributeCollection.new(:foo, :attributes => lambda_expression)
    end
    
    it "should evaluate the proc in the binding of the caller which is passed" do
      @attributes.hash[:bar].should == :bar
    end
    
    it "should get the correct value for the method called" do
      @attributes.hash[:baz].should == :baz
    end
  end
end
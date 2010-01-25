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

module Spec
  module Example
    class ExampleGroupSubclass < ExampleGroup
      def self.examples_ran
        @examples_ran
      end

      def self.examples_ran=(examples_ran)
        @examples_ran = examples_ran
      end

      @@class_variable = :class_variable
      CONSTANT = :constant

      before do
        @instance_variable = :instance_variable
      end
      
      after(:all) do
        self.class.examples_ran = true
      end

      it "should have access to instance variables" do
        @instance_variable.should == :instance_variable
      end

      it "should have access to class variables" do
        @@class_variable.should == :class_variable
      end

      it "should have access to constants" do
        CONSTANT.should == :constant
      end

      it "should have access to methods defined in the Example Group" do
        a_method.should == 22
      end
      
      def a_method
        22
      end
    end

    describe ExampleGroupSubclass do
      it "should run" do
        ExampleGroupSubclass.examples_ran.should be_true
      end
    end
  end
end
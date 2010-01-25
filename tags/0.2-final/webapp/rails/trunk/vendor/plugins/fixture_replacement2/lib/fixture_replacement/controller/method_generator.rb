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
module FixtureReplacementController
  class MethodGenerator
    
    class << self
      def generate_methods
        AttributeCollection.instances.each do |attributes_instance|
          new(attributes_instance).generate_methods
        end
      end
    end
    
    def initialize(object_attributes)
      @object_attributes = object_attributes
    end
    
    def generate_methods
      generate_default_method
      generate_new_method
      generate_create_method
    end
    
    def generate_default_method
      obj = @object_attributes
      
      ClassFactory.fixture_replacement_module.module_eval do
        define_method("default_#{obj.fixture_name}") do |*args|
          hash = args[0] || Hash.new
          DelayedEvaluationProc.new { 
            [obj, hash]
          }
        end
      end
    end
    
    def generate_create_method
      obj = @object_attributes
      
      ClassFactory.fixture_replacement_module.module_eval do
        define_method("create_#{obj.fixture_name}") do |*args|
          obj.to_created_class_instance(args[0], self)
        end
      end
    end
    
    def generate_new_method
      obj = @object_attributes
      
      ClassFactory.fixture_replacement_module.module_eval do
        define_method("new_#{obj.fixture_name}") do |*args|
          obj.to_new_class_instance(args[0], self)
        end
      end
    end
  end
end
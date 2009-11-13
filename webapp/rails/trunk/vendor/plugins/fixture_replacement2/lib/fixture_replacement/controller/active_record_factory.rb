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
  # I am a Factory which creates ActiveRecord Class instances.
  # Give me a collection of attributes, and an additional hash,
  # and I will merge them into a new ActiveRecord Instance (either a new
  # instance with ActiveRecord::Base#new, or a created one ActiveRecord::Base#create!).
  class ActiveRecordFactory
    
    def initialize(attributes, hash={}, original_caller=self)
      @attributes = attributes
      @hash_given_to_constructor = hash
      @caller = original_caller
    end
    
    def to_new_instance
      new_object = @attributes.active_record_class.new
      assign_values_to_instance new_object
      return new_object
    end
    
    def to_created_instance
      created_obj = self.to_new_instance
      created_obj.save!
      return created_obj
    end

  protected
  
    def hash_given_to_constructor
      @hash_given_to_constructor || Hash.new
    end
    
  private

    def assign_values_to_instance(instance_object)
      all_attributes.each do |key, value|
        value = evaluate_possible_delayed_proc(value)
        instance_object.__send__("#{key}=", value)             
      end
    end

    def evaluate_possible_delayed_proc(value)
      case value
      when Array
        value.map! { |element| evaluate_possible_delayed_proc element }
      when ClassFactory.delayed_evaluation_proc
        value.evaluate(@caller)
      else
        value
      end
    end
    
    def all_attributes
      @attributes.merge!
      @all_merged_attributes ||= attributes_hash.merge(self.hash_given_to_constructor)
    end
    
    def attributes_hash
      @attributes.hash
    end
    
  end
end
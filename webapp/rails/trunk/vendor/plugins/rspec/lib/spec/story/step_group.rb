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
module Spec
  module Story

    class StepGroupHash < Hash
      def initialize
        super do |h,k|
          h[k] = Spec::Story::StepGroup.new
        end
      end
    end

    class StepGroup
      def self.steps(&block)
        @step_group ||= StepGroup.new(false)
        @step_group.instance_eval(&block) if block
        @step_group
      end
      
      def initialize(init_defaults=true, &block)
        @hash_of_lists_of_steps = Hash.new {|h, k| h[k] = []}
        if init_defaults
          self.class.steps.add_to(self)
        end
        instance_eval(&block) if block
      end
      
      def find(type, name)
        @hash_of_lists_of_steps[type].each do |step|
          return step if step.matches?(name)
        end
        return nil
      end

      def GivenScenario(name, &block)
        create_matcher(:given_scenario, name, &block)
      end
      
      def Given(name, &block)
        create_matcher(:given, name, &block)
      end
      
      def When(name, &block)
        create_matcher(:when, name, &block)
      end
      
      def Then(name, &block)
        create_matcher(:then, name, &block)
      end

      alias :given_scenario :GivenScenario
      alias :given :Given
      alias :when :When
      alias :then :Then
      
      def add(type, steps)
        (@hash_of_lists_of_steps[type] << steps).flatten!
      end
      
      def clear
        @hash_of_lists_of_steps.clear
      end
      
      def empty?
        [:given_scenario, :given, :when, :then].each do |type|
          return false unless @hash_of_lists_of_steps[type].empty?
        end
        return true
      end
      
      def add_to(other_step_matchers)
        [:given_scenario, :given, :when, :then].each do |type|
          other_step_matchers.add(type, @hash_of_lists_of_steps[type])
        end
      end
      
      def <<(other_step_matchers)
        other_step_matchers.add_to(self) if other_step_matchers.respond_to?(:add_to)
      end
      
      # TODO - make me private
      def create_matcher(type, name, &block)
        matcher = Step.new(name, &block)
        @hash_of_lists_of_steps[type] << matcher
        matcher
      end
      
    end
  end
end

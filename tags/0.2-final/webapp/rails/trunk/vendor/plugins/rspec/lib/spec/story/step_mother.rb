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
    class StepMother
      def initialize
        @steps = StepGroup.new
      end
      
      def use(new_step_group)
        @steps << new_step_group
      end
      
      def store(type, step)
        @steps.add(type, step)
      end
      
      def find(type, unstripped_name)
        name = unstripped_name.strip
        if @steps.find(type, name).nil?
          @steps.add(type,
          Step.new(name) do
            raise Spec::Example::ExamplePendingError.new("Unimplemented step: #{name}")
          end
          )
        end
        @steps.find(type, name)
      end
      
      def clear
        @steps.clear
      end
      
      def empty?
        @steps.empty?
      end
      
    end
  end
end

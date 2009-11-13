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
    class Story
      attr_reader :title, :narrative
      
      def initialize(title, narrative, params = {}, &body)
        @body = body
        @title = title
        @narrative = narrative
        @params = params
      end
      
      def [](key)
        @params[key]
      end
      
      def run_in(obj)
        obj.instance_eval(&@body)
      end
      
      def assign_steps_to(assignee)
        if steps=@params[:steps_for]
          steps = [steps] unless steps.is_a?(Array)
          steps.each do |step|
            if step.is_a?(StepGroup)
              assignee.use(step)
            else
              assignee.use(steps_for(step))
            end
          end
        end
      end
      
      def steps_for(key)
        $rspec_story_steps[key]
      end
    end
  end
end

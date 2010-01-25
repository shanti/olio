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
require 'spec/story/runner/scenario_collector.rb'
require 'spec/story/runner/scenario_runner.rb'
require 'spec/story/runner/story_runner.rb'
require 'spec/story/runner/story_parser.rb'
require 'spec/story/runner/story_mediator.rb'
require 'spec/story/runner/plain_text_story_runner.rb'

module Spec
  module Story
    module Runner
      def self.run_options # :nodoc:
        Spec::Runner.options
      end
      
      def self.story_runner # :nodoc:
        unless @story_runner
          @story_runner = create_story_runner
          run_options.story_formatters.each do |formatter|
            register_listener(formatter)
          end
          self.register_exit_hook
        end
        @story_runner
      end
      
      def self.scenario_runner # :nodoc:
        @scenario_runner ||= ScenarioRunner.new
      end
      
      def self.world_creator # :nodoc:
        @world_creator ||= World
      end
      
      def self.create_story_runner
        Runner::StoryRunner.new(scenario_runner, world_creator)
      end
      
      # Use this to register a customer output formatter.
      def self.register_listener(listener)
        story_runner.add_listener(listener) # run_started, story_started, story_ended, #run_ended
        world_creator.add_listener(listener) # found_scenario, step_succeeded, step_failed, step_failed
        scenario_runner.add_listener(listener) # scenario_started, scenario_succeeded, scenario_pending, scenario_failed
      end
      
      def self.register_exit_hook # :nodoc:
        at_exit do
          exit Runner.story_runner.run_stories unless $!
        end
      end
      
      def self.dry_run
        run_options.dry_run
      end
      
    end
  end
end

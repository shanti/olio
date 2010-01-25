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
    module Runner
      class PlainTextStoryRunner
        # You can initialize a PlainTextStoryRunner with the path to the
        # story file or a block, in which you can define the path using load.
        #
        # == Examples
        #   
        #   PlainTextStoryRunner.new('path/to/file')
        #
        #   PlainTextStoryRunner.new do |runner|
        #     runner.load 'path/to/file'
        #   end
        def initialize(*args)
          @options = Hash === args.last ? args.pop : {}
          @story_file = args.empty? ? nil : args.shift
          yield self if block_given?
        end
        
        def []=(key, value)
          @options[key] = value
        end
        
        def load(path)
          @story_file = path
        end
        
        def run(story_runner=Spec::Story::Runner.story_runner)
          raise "You must set a path to the file with the story. See the RDoc." if @story_file.nil?
          mediator = Spec::Story::Runner::StoryMediator.new(steps, story_runner, @options)
          parser = Spec::Story::Runner::StoryParser.new(mediator)

          story_text = File.read(@story_file)          
          parser.parse(story_text.split("\n"))

          mediator.run_stories
        end
        
        def steps
          @step_group ||= Spec::Story::StepGroup.new
          yield @step_group if block_given?
          @step_group
        end
      end
    end
  end
end

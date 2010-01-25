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
require File.dirname(__FILE__) + '/story_helper'

module Spec
  module Story
    describe Story do
      it 'should run itself in a given object' do
        # given
        $instance = nil
        story = Story.new 'title', 'narrative' do
          $instance = self
        end
        object = Object.new
        
        # when
        story.run_in(object)
        
        # then
        $instance.should be(object)
      end
      
      it 'should not raise an error if no block is supplied' do
        # when
        error = exception_from { Story.new 'title', 'narrative' }
        
        # then
        error.should be_nil
      end
      
      it "should raise an error when an error is raised running in another object" do
        #given
        story = Story.new 'title', 'narrative' do
          raise "this is raised in the story"
        end
        object = Object.new
        
        # when/then
        lambda do
          story.run_in(object)
        end.should raise_error
      end
      
      it "should use the steps it is told to using a StepGroup" do
        story = Story.new("title", "narrative", :steps_for => steps = StepGroup.new) do end
        assignee = mock("assignee")
        assignee.should_receive(:use).with(steps)
        story.assign_steps_to(assignee)
      end

      it "should use the steps it is told to using a key" do
        begin
          orig_rspec_story_steps = $rspec_story_steps
          $rspec_story_steps = StepGroupHash.new
          $rspec_story_steps[:foo] = steps = Object.new
        
          story = Story.new("title", "narrative", :steps_for => :foo) do end
          assignee = mock("assignee")
        
          assignee.should_receive(:use).with(steps)
          story.assign_steps_to(assignee)
        ensure
          $rspec_story_steps = orig_rspec_story_steps
        end
      end
      
      it "should use the steps it is told to using multiple keys" do
        begin
          orig_rspec_story_steps = $rspec_story_steps
          $rspec_story_steps = StepGroupHash.new
          $rspec_story_steps[:foo] = foo_steps = Object.new
          $rspec_story_steps[:bar] = bar_steps = Object.new
        
          story = Story.new("title", "narrative", :steps_for => [:foo, :bar]) do end
          assignee = mock("assignee")
        
          assignee.should_receive(:use).with(foo_steps)
          assignee.should_receive(:use).with(bar_steps)
          story.assign_steps_to(assignee)
        ensure
          $rspec_story_steps = orig_rspec_story_steps
        end
      end
    end
  end
end

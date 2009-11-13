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
require File.dirname(__FILE__) + '/../../../../spec_helper.rb'
require 'spec/runner/formatter/story/html_formatter'

module Spec
  module Runner
    module Formatter
      module Story
        describe HtmlFormatter do
          before :each do
            @out = StringIO.new
            @options = mock('options')
            @reporter = HtmlFormatter.new(@options, @out)
          end
          
          it "should just be poked at" do
            @reporter.run_started(1)
            @reporter.story_started('story_title', 'narrative')

            @reporter.scenario_started('story_title', 'succeeded_scenario_name')
            @reporter.step_succeeded('given', 'succeded_step', 'one', 'two')
            @reporter.scenario_succeeded('story_title', 'succeeded_scenario_name')

            @reporter.scenario_started('story_title', 'pending_scenario_name')
            @reporter.step_pending('when', 'pending_step', 'un', 'deux')
            @reporter.scenario_pending('story_title', 'pending_scenario_name', 'not done')

            @reporter.scenario_started('story_title', 'failed_scenario_name')
            @reporter.step_failed('then', 'failed_step', 'en', 'to')
            @reporter.scenario_failed('story_title', 'failed_scenario_name', NameError.new('sup'))
            
            @reporter.scenario_started('story_title', 'scenario_with_given_scenario_name')
            @reporter.found_scenario('given scenario', 'succeeded_scenario_name')
            
            @reporter.story_ended('story_title', 'narrative')
            @reporter.run_ended
          end
          
          it "should create spans for params" do
            @reporter.step_succeeded('given', 'a $coloured $animal', 'brown', 'dog')
            @reporter.scenario_ended
            @reporter.story_ended('story_title', 'narrative')

            @out.string.should include("                <li class=\"passed\">Given a <span class=\"param\">brown</span> <span class=\"param\">dog</span></li>\n")
          end
          
          it 'should create spanes for params in regexp steps' do
            @reporter.step_succeeded :given, /a (pink|blue) (.*)/, 'brown', 'dog'
            @reporter.scenario_ended
            @reporter.story_ended('story_title', 'narrative')
            
            @out.string.should include("                <li class=\"passed\">Given a <span class=\"param\">brown</span> <span class=\"param\">dog</span></li>\n")
          end

          it "should create a ul for collected_steps" do
            @reporter.collected_steps(['Given a $coloured $animal', 'Given a $n legged eel'])
            @out.string.should == (<<-EOF)
      <ul id="stock_steps" style="display: none;">
        <li>Given a $coloured $animal</li>
        <li>Given a $n legged eel</li>
      </ul>
EOF
          end
          
          it 'should document additional givens using And' do
            # when
            @reporter.step_succeeded :given, 'step 1'
            @reporter.step_succeeded :given, 'step 2'
            @reporter.scenario_ended
            @reporter.story_ended '', ''

            # then
            @out.string.should include("Given step 1")
            @out.string.should include("And step 2")
          end

          it 'should document additional events using And' do
            # when
            @reporter.step_succeeded :when, 'step 1'
            @reporter.step_succeeded :when, 'step 2'
            @reporter.scenario_ended
            @reporter.story_ended '', ''

            # then
            @out.string.should include("When step 1")
            @out.string.should include("And step 2")
          end

          it 'should document additional outcomes using And' do
            # when
            @reporter.step_succeeded :then, 'step 1'
            @reporter.step_succeeded :then, 'step 2'
            @reporter.scenario_ended
            @reporter.story_ended '', ''

            # then
            @out.string.should include("Then step 1")
            @out.string.should include("And step 2")
          end

          it 'should document a GivenScenario followed by a Given using And' do
            # when
            @reporter.step_succeeded :'given scenario', 'a scenario'
            @reporter.step_succeeded :given, 'a context'
            @reporter.scenario_ended
            @reporter.story_ended '', ''

            # then
            @out.string.should include("Given scenario a scenario")
            @out.string.should include("And a context")
          end
          
          it "should create a failed story if one of its scenarios fails" do
              @reporter.story_started('story_title', 'narrative')
              @reporter.scenario_started('story_title', 'succeeded_scenario_name')
              @reporter.step_failed('then', 'failed_step', 'en', 'to')
              @reporter.scenario_failed('story_title', 'failed_scenario_name', NameError.new('sup'))
              @reporter.story_ended('story_title', 'narrative')
            
              @out.string.should include("      <dl class=\"story failed\">\n        <dt>Story: story_title</dt>\n")
          end
          
          it "should create a failed scenario if one of its steps fails" do
            @reporter.scenario_started('story_title', 'failed_scenario_name')
            @reporter.step_failed('then', 'failed_step', 'en', 'to')
            @reporter.scenario_failed('story_title', 'failed_scenario_name', NameError.new('sup'))
            @reporter.story_ended('story_title', 'narrative')
          
            @out.string.should include("<dl class=\"failed\">\n              <dt>Scenario: failed_scenario_name</dt>\n")
          end
          
        end
      end
    end
  end
end
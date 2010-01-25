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
require File.dirname(__FILE__) + '/../story_helper'

module Spec
  module Story
    module Runner
      describe ScenarioCollector do
        it 'should construct scenarios with the supplied story' do
          # given
          story = stub_everything('story')
          scenario_collector = ScenarioCollector.new(story)
          
          # when
          scenario_collector.Scenario 'scenario1' do end
          scenario_collector.Scenario 'scenario2' do end
          scenarios = scenario_collector.scenarios
          
          # then
          scenario_collector.should have(2).scenarios
          scenarios.first.name.should == 'scenario1'
          scenarios.first.story.should equal(story)
          scenarios.last.name.should == 'scenario2'
          scenarios.last.story.should equal(story)
        end
      end
    end
  end
end

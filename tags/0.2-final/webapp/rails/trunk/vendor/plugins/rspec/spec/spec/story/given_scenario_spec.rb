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
    describe GivenScenario do
      it 'should execute a scenario from the current story in its world' do
        # given
        class MyWorld
          attr :scenario_ran
        end
        instance = World.create(MyWorld)
        scenario = ScenarioBuilder.new.to_scenario do
          @scenario_ran = true
        end
        Runner::StoryRunner.should_receive(:scenario_from_current_story).with('scenario name').and_return(scenario)
        
        step = GivenScenario.new 'scenario name'
        
        # when
        step.perform(instance, nil)
        
        # then
        instance.scenario_ran.should be_true
      end
    end
  end
end

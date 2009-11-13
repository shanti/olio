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
$:.push File.join(File.dirname(__FILE__), *%w[.. .. lib])
require 'spec'

class AdditionMatchers < Spec::Story::StepGroup
  steps do |add|
    add.given("an addend of $addend") do |addend|
      @adder ||= Adder.new
      @adder << addend.to_i
    end
  end
end

steps = AdditionMatchers.new do |add|  
  add.then("the sum should be $sum") do |sum|
    @sum.should == sum.to_i
  end
end

steps.when("they are added") do
  @sum = @adder.sum
end

# This Story uses steps (see above) instead of blocks
# passed to Given, When and Then

Story "addition", %{
  As an accountant
  I want to add numbers
  So that I can count some beans
}, :steps_for => steps do
  Scenario "2 + 3" do
    Given "an addend of 2"
    And "an addend of 3"
    When "they are added"
    Then "the sum should be 5"
  end
  
  # This scenario uses GivenScenario, which silently runs
  # all the steps in a previous scenario.
  
  Scenario "add 4 more" do
    GivenScenario "2 + 3"
    Given "an addend of 4"
    When "they are added"
    Then "the sum should be 9"
  end
end

# And the class that makes the story pass

class Adder
  def << addend
    addends << addend
  end
  
  def sum
    @addends.inject(0) do |result, addend|
      result + addend.to_i
    end
  end
  
  def addends
    @addends ||= []
  end
end

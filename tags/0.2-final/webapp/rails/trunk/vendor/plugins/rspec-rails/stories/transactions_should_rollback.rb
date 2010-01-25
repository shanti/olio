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
require File.join(File.dirname(__FILE__), *%w[helper])

Story "transactions should rollback", %{
  As an RSpec/Rails Story author
  I want transactions to roll back between scenarios
  So that I can have confidence in the state of the database
}, :type => RailsStory do
  Scenario "add one Person" do
    When "I add a Person" do
      Person.create!(:name => "Foo")
    end
  end
  
  Scenario "add another person" do
    GivenScenario "add one Person"
    Then "there should be one person" do
      Person.count.should == 1
    end
  end

  Scenario "add yet another person" do
    GivenScenario "add one Person"
    Then "there should be one person"
  end
end
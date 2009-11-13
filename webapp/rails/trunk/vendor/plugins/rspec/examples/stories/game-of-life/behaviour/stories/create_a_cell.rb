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

Story "I can create a cell",
  %(As a game producer
    I want to create a cell
    So that I can show the grid to people), :steps_for => :life do
  
  Scenario "nothing to see here" do
    Given "a game with dimensions", 3, 3 do |rows,cols|
      @game = Game.new(rows,cols)
    end
    
    Then "the grid should look like", %(
      ...
      ...
      ...
    )
  end
  
  Scenario "all on its lonesome" do
    Given "a game with dimensions", 2, 2
    When "I create a cell at", 1, 1 do |row,col|
      @game.create_at(row,col)
    end
    Then "the grid should look like", %(
      ..
      .X
    )
  end
  
  Scenario "the grid has three cells" do
    Given "a game with dimensions", 3, 3
    When "I create a cell at", 0, 0
    When "I create a cell at", 0, 1
    When "I create a cell at", 2, 2
    Then "the grid should look like", %(
      XX.
      ...
      ..X
    )
  end
  
  Scenario "more cells more more" do
    GivenScenario "the grid has three cells"
    When "I create a cell at", 2, 0
    Then "the grid should look like", %(
      XX.
      ...
      X.X
    )
  end
end

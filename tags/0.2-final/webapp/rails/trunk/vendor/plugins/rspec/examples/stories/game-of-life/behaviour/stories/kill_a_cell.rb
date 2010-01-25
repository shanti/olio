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

Story 'I can kill a cell',
  %(As a game producer
  I want to kill a cell
  So that when I make a mistake I don't have to start again), :steps_for => :life do
  
  Scenario "bang, you're dead" do
    
    Given 'a game that looks like', %(
      XX.
      .X.
      ..X
    ) do |dots|
      @game = Game.from_string dots
    end
    When 'I destroy the cell at', 0, 1 do |row,col|
      @game.destroy_at(row,col)
    end
    Then 'the grid should look like', %(
      X..
      .X.
      ..X
    )
  end
end

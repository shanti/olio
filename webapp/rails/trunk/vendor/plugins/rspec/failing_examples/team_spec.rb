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
require File.dirname(__FILE__) + '/spec_helper'


class Team
  attr_reader :players
  def initialize
    @players = Players.new
  end
end

class Players
  def initialize
    @players = []
  end
  def size
    @players.size
  end
  def include? player
    raise "player must be a string" unless player.is_a?(String)
    @players.include? player
  end
end

describe "A new team" do
  
  before(:each) do
    @team = Team.new
  end
  
  it "should have 3 players (failing example)" do
    @team.should have(3).players
  end
  
  it "should include some player (failing example)" do
    @team.players.should include("Some Player")
  end

  it "should include 5 (failing example)" do
    @team.players.should include(5)
  end
  
  it "should have no players"
  
end

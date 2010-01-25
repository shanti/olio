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
require 'life'

describe Game do
  it 'should have a grid' do
    # given
    game = Game.new(5, 5)
    
    # then
    game.grid.should be_kind_of(Grid)
  end
  
  it 'should create a cell' do
    # given
    game = Game.new(2, 2)
    expected_grid = Grid.from_string( 'X. ..' )
    
    # when
    game.create_at(0, 0)
    
    # then
    game.grid.should == expected_grid
  end
  
  it 'should destroy a cell' do
    # given
    game = Game.new(2,2)
    game.grid = Grid.from_string('X. ..')
    
    # when
    game.destroy_at(0,0)
    
    # then
    game.grid.should == Grid.from_string('.. ..')
  end
end

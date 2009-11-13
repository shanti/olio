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
class Game
  attr_accessor :grid
  def initialize(rows,cols)
    @grid = Grid.new(rows, cols)
  end
  
  def create_at(row,col)
    @grid.create_at(row,col)
  end
  
  def destroy_at(row,col)
    @grid.destroy_at(row, col)
  end
  
  def self.from_string(dots)
    grid = Grid.from_string(dots)
    game = new(grid.rows, grid.columns)
    game.instance_eval do
      @grid = grid
    end
    return game
  end
end

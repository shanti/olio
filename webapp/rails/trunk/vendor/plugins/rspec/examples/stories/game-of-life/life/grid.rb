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
class Grid
  
  attr_accessor :contents
  
  def initialize(rows, cols)
    @contents = []
    rows.times do @contents << [0] * cols end
  end
  
  def rows
    @contents.size
  end
  
  def columns
    @contents[0].size
  end
  
  def ==(other)
    self.contents == other.contents
  end
  
  def create_at(row,col)
    @contents[row][col] = 1
  end
  
  def destroy_at(row,col)
    @contents[row][col] = 0
  end
  
  def self.from_string(str)
    row_strings = str.split(' ')
    grid = new(row_strings.size, row_strings[0].size)
    
    row_strings.each_with_index do |row, row_index|
      row_chars = row.split(//)
      row_chars.each_with_index do |col_char, col_index|
        grid.create_at(row_index, col_index) if col_char == 'X'
      end
    end
    return grid
  end
  
end

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
class StackUnderflowError < RuntimeError
end

class StackOverflowError < RuntimeError
end

class Stack
  
  def initialize
    @items = []
  end
  
  def push object
    raise StackOverflowError if @items.length == 10
    @items.push object
  end
  
  def pop
    raise StackUnderflowError if @items.empty?
    @items.delete @items.last
  end
  
  def peek
    raise StackUnderflowError if @items.empty?
    @items.last
  end
  
  def empty?
    @items.empty?
  end

  def full?
    @items.length == 10
  end
  
end

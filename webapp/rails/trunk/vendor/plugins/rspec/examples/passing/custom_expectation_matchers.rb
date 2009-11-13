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
module AnimalSpecHelper
  class Eat
    def initialize(food)
      @food = food
    end
    
    def matches?(animal)
      @animal = animal
      @animal.eats?(@food)
    end
    
    def failure_message
      "expected #{@animal} to eat #{@food}, but it does not"
    end
    
    def negative_failure_message
      "expected #{@animal} not to eat #{@food}, but it does"
    end
  end
    
  def eat(food)
    Eat.new(food)
  end
end

module Animals
  class Animal
    def eats?(food)
      return foods_i_eat.include?(food)
    end
  end
  
  class Mouse < Animal
    def foods_i_eat
      [:cheese]
    end
  end

  describe Mouse do
    include AnimalSpecHelper
    before(:each) do
      @mouse = Animals::Mouse.new
    end
  
    it "should eat cheese" do
      @mouse.should eat(:cheese)
    end
  
    it "should not eat cat" do
      @mouse.should_not eat(:cat)
    end
  end

end

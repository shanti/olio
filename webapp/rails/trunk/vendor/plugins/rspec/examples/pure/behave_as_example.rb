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

def behave_as_electric_musician
  respond_to(:read_notes, :turn_down_amp)
end

def behave_as_musician
  respond_to(:read_notes)
end

module BehaveAsExample
  
  class BluesGuitarist
    def read_notes; end
    def turn_down_amp; end
  end
  
  class RockGuitarist
    def read_notes; end
    def turn_down_amp; end
  end
  
  class ClassicGuitarist
    def read_notes; end
  end
  
  describe BluesGuitarist do
    it "should behave as guitarist" do
      BluesGuitarist.new.should behave_as_electric_musician
    end
  end

  describe RockGuitarist do
    it "should behave as guitarist" do
      RockGuitarist.new.should behave_as_electric_musician
    end
  end

  describe ClassicGuitarist do
    it "should not behave as guitarist" do
      ClassicGuitarist.new.should behave_as_musician
    end
  end
  
end

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
require File.dirname(__FILE__) + '/../../spec_helper.rb'

class Substance
  def initialize exists, description
    @exists = exists
    @description = description
  end
  def exist?
    @exists
  end
  def inspect
    @description
  end
end
  
class SubstanceTester
  include Spec::Matchers
  def initialize substance
    @substance = substance
  end
  def should_exist
    @substance.should exist
  end
end

describe "should exist," do
  
  before(:each) do
    @real = Substance.new true, 'something real'
    @imaginary = Substance.new false, 'something imaginary'
  end

  describe "within an example group" do
  
    it "should pass if target exists" do
      @real.should exist
    end
  
    it "should fail if target does not exist" do
      lambda { @imaginary.should exist }.should fail
    end
    
    it "should pass if target doesn't exist" do
      lambda { @real.should_not exist }.should fail
    end
  end

  describe "outside of an example group" do

    it "should pass if target exists" do
      real_tester = SubstanceTester.new @real
      real_tester.should_exist
    end

  end

end

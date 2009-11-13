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
# greeter.rb
#
# Based on http://glu.ttono.us/articles/2006/12/19/tormenting-your-tests-with-heckle
#
# Run with:
#
#   spec greeter_spec.rb --heckle Greeter
#
class Greeter
  def initialize(person = nil)
    @person = person
  end

  def greet
    @person.nil? ? "Hi there!" : "Hi #{@person}!"
  end
end

describe "Greeter" do
  it "should say Hi to person" do
    greeter = Greeter.new("Kevin")
    greeter.greet.should == "Hi Kevin!"
  end

  it "should say Hi to nobody" do
    greeter = Greeter.new
    # Uncomment the next line to make Heckle happy
    #greeter.greet.should == "Hi there!"
  end
end

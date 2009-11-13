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

class BddFramework
  def intuitive?
    true
  end

  def adopted_quickly?
    #this will cause failures because it reallly SHOULD be adopted quickly
    false
  end
end

describe "BDD framework" do

  before(:each) do
    @bdd_framework = BddFramework.new
  end

  it "should be adopted quickly" do
    #this will fail because it reallly SHOULD be adopted quickly
    @bdd_framework.should be_adopted_quickly
  end

  it "should be intuitive" do
    @bdd_framework.should be_intuitive
  end

  it "should not respond to test" do
    #this will fail
    @bdd_framework.test
  end

end

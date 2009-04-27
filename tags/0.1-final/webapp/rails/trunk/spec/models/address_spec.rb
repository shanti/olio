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
require File.dirname(__FILE__) + '/../spec_helper'

describe Address do
  
  before(:each) do
    @address = Address.new
  end
  
  it "should be valid" do
    address = new_address
    address.should be_valid
  end
  
  it "should be valid with the minimal parameters" do
    @address.street1 = "Soda Hall"
    @address.city = "Berkeley"
    @address.country = "United States"
    @address.zip = 94720
    @address.should be_valid
  end
  
  it "should have a street address" do 
    @address.should have(1).errors_on(:street1)
  end
  
  it "should have a city" do
    @address.should have(1).errors_on(:city)
  end
  
  it "should have a zip code" do
    @address.should have(1).errors_on(:zip)
  end
  
  it "should have a country" do
    @address.should have(1).errors_on(:country)
  end
  
  it "should allow duplicate addresses" do
    address = new_address
    @address.street1 = address.street1
    @address.city = address.city
    @address.country = address.country
    @address.zip = address.zip
    address.should be_valid
    @address.should be_valid
  end
  
end

#
#  Licensed to the Apache Software Foundation (ASF) under one
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
# == Schema Information
# Schema version: 17
#
# Table name: addresses
#
#  id        :integer(11)     not null, primary key
#  street1   :string(55)      
#  street2   :string(55)      
#  city      :string(55)      
#  state     :string(25)      
#  zip       :string(12)      
#  country   :string(55)      
#  latitude  :decimal(14, 10) 
#  longitude :decimal(14, 10) 
#

class Address < ActiveRecord::Base
  
  # Our schema doesn't have foreign keys that go this way,
  # so this is changed to has_one to fit Rails convention. -Hubert
  # belongs_to :user
  # belongs_to :event
  has_one :user
  has_one :event
  
  validates_presence_of :street1, :city, :zip, :country
  
end

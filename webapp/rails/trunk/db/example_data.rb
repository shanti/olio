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
module FixtureReplacement
  attributes_for :address do |a|
    a.street1 = String.random(32)
    a.city = String.random(10)
    a.state = String.random(2)
    a.zip = '10000'
    a.country = 'USA'
    a.longitude = 141.001
    a.latitude = 120.001 
	end

  attributes_for :comment do |a|
    a.user = default_user
    a.event = default_event
    a.comment = String.random
    a.rating = 5
	end

  attributes_for :document do |a|
    a.content_type = 'application/pdf'
    a.size = 100000
    a.filename = String.random
	end
	
  attributes_for :event do |a|
    a.title = String.random
    a.description = String.random
    a.telephone = '555-555-5555'
    a.event_timestamp = Time.now
    a.address = default_address
    a.user = default_user
    a.image = default_image
    a.document = default_document
	end
	
	attributes_for :simple_event, :class => Event do |a|
    a.title = String.random
    a.description = String.random
    a.telephone = '555-555-5555'
    a.event_timestamp = Time.now
    a.address = default_address
    a.user = default_user
	end

  attributes_for :image do |a|
    a.content_type = 'image/jpeg'
    a.size = 100000
    a.filename = String.random
	end

  attributes_for :invite do |a|
    a.user = default_user
    a.user_target = default_user
	end

  attributes_for :tagging do |a|
    
	end

  attributes_for :tag do |a|
    
	end

  attributes_for :user do |a|
    a.username = String.random
    a.password = String.random
    a.firstname = String.random
    a.lastname = String.random
    a.email = 'xxx@xxx.com'
    a.timezone = 'PST5PDT'
    a.telephone = '555-555-5555'
    a.summary = String.random
    a.address = default_address
	end

  attributes_for :bob, :from => :user do |a|
    a.password = 'kitty'
    a.username = 'bob'
  end
end

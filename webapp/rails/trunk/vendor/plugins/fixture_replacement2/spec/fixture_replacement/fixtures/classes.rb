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
class User < ActiveRecord::Base
  belongs_to :gender  
  validates_presence_of :key
end

class Player < ActiveRecord::Base
end

class Alien < ActiveRecord::Base
  belongs_to :gender
end

class Admin < ActiveRecord::Base
  attr_protected :admin_status
end

class Gender < ActiveRecord::Base; end
class Actress < ActiveRecord::Base; end

class Item < ActiveRecord::Base
  belongs_to :category
end

class Writing < Item; end

class Category < ActiveRecord::Base
  has_many :items
end

class Subscriber < ActiveRecord::Base
  has_and_belongs_to_many :subscriptions
end

class Subscription < ActiveRecord::Base
  has_and_belongs_to_many :subscribers
end
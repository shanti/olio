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
ActiveRecord::Schema.define(:version => 1) do

  create_table :channels, :force => true do |t|
    t.column :name, :string
  end

  create_table :shows, :force => true do |t|
    t.column :name, :string
    t.column :channel_id, :integer
  end

  # people
  create_table :people, :force => true do |t|
    t.column :name, :string
  end

  # people_people
  create_table :people_people, {:id => false} do |t|
    t.column :person_id, :integer, :null => false
    t.column :person_id_target, :integer, :null => false
  end
  
  # invites
  create_table :invites, :force => true do |t|
    t.column :person_id,  :integer, :null => false
    t.column :person_id_target,   :integer, :null => false
    t.column :message, :text
    t.column :is_accepted, :boolean
  end
  
  # friends
  create_table :friends, {:id => false} do |t|
    t.column :person_id, :integer, :null => false
    t.column :person_id_friend, :integer, :null => false
  end
  
end
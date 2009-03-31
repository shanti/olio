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
# Table name: invites
#
#  id             :integer(11)     not null, primary key
#  user_id        :integer(11)     not null
#  user_id_target :integer(11)     not null
#  is_accepted    :boolean(1)      
#

class Invite < ActiveRecord::Base
  
  belongs_to :user
  belongs_to :user_target, :class_name => 'User', :foreign_key => 'user_id_target'
  validates_presence_of :user, :user_target
  validates_uniqueness_of :user_id, :scope => :user_id_target, :message => "can only request friendship once"
  
  def self.existing(source, target)
    i = Invite.find_by_user_id_and_user_id_target(source, target)
    i ||= Invite.find_by_user_id_and_user_id_target(target, source)
  end
  
  def accept
    self.is_accepted = true
    self.save!
  end
  
  def validate
    if user_id == user_id_target
      errors.add(:user_id_target, "is invalid; you cannot be friends with yourself")
    end    
  end
end

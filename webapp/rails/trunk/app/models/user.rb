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
# Table name: users
#
#  id         :integer(11)     not null, primary key
#  username   :string(25)      
#  password   :string(25)      
#  firstname  :string(25)      
#  lastname   :string(25)      
#  email      :string(90)      
#  telephone  :string(25)      
#  summary    :string(2500)    
#  timezone   :string(25)      
#  created_at :datetime        
#  updated_at :datetime        
#  address_id :integer(11)     
#

class User < ActiveRecord::Base
  
  belongs_to :address
  belongs_to :image
  has_and_belongs_to_many :events
  acts_as_network :friends, :through => :invites, :conditions => ["is_accepted = ?", true]
  
  validates_presence_of :username, :password, :firstname, :lastname, :email, :timezone, :address_id
  validates_uniqueness_of :username
  validates_length_of :summary, :maximum => 2500, :allow_nil => true
  # QUESTION: same email allowed?
  validates_length_of :telephone, :in => 7..20
  validates_format_of :telephone, 
                      :with => /^[\d\-]*$/,
                      :on => :save, 
                      :message => "must contain only 1-9 and/or -"
  validates_confirmation_of :password, :on => :save
  
  def self.authenticate(username, password)
    user = self.find_by_username_and_password(username, password)
  end
  
  def invitations
    self.invites_in.find(:all, :conditions => ['is_accepted <> ?', true])
  end
  
  def incoming_friend_requests
    self.invites_in.delete_if {|i| i.is_accepted == true }
  end
  
  def outgoing_friend_requests
    self.invites_out.delete_if {|i| i.is_accepted == true }
  end
  
  def outgoing_invite_ids
    outgoing_friend_requests.map {|i| i.user_id_target }
  end
  
  def incoming_invite_ids
    incoming_friend_requests.map {|i| i.user_id }
  end
  
  def invite(friend)
    i = Invite.create(:user => self, :user_target => friend)
    (i.new_record?) ? false : true
  end
  
  def accept(friend)
    if invite = self.invites_in.find_by_user_id(friend)
      invite.accept
      true
    else
      false
    end
  end
  
  def unfriend(friend)
    if invite = Invite.existing(self, friend)
      invite.destroy
    else
      false
    end
  end
  
  # future events this user is attending
  def upcoming_events
    events.find(:all, :conditions => ['event_timestamp > ?', Time.now], :order => 'event_timestamp', :limit => 3)
  end
  
  def self.search(query, page)
    paginate(:all, :conditions => ["username LIKE ? OR firstname LIKE ? OR lastname LIKE ? OR email LIKE ?", "%#{query}%", "%#{query}%", "%#{query}%", "%#{query}%"], :order => :username, :page => page)
  end
  
  # Overload the image and document assignment methods to provide 
  # cascade destroy.
  alias set_image image=
  
  # Associate an image with this event. Need to simulate dependent destroy
  def image=(image)
    self.image.destroy unless self.image.nil?
    set_image(image)
  end
  
  def posted_events
    Event.find(:all, :conditions => "user_id = #{self.id}", :order => "event_timestamp DESC")
  end
  
end

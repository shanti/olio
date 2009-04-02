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

describe 'Invite model: Invite' do
  before do
    @invite = Invite.new
  end
  
  it 'should be valid' do
    invite = new_invite
    invite.should be_valid
  end
  
  it "should have a (source) user" do
    @invite.should have(1).errors_on(:user)
  end

  it "should have a target user" do
    @invite.should have(1).errors_on(:user_target)
  end
  
  it "should return nil for a non existing invite" do
    art = create_user
    Invite.existing(art, art).should be_nil
  end
  
  it "should have a different source and target user" do
    art = create_user
    @invite.user_id, @invite.user_id_target = art.id, art.id
    @invite.should have(1).errors_on(:user_id_target)
  end

  it "should be unique for two users, regardless of who is source and target" do
    art = create_user
    bob = create_user
    @invite.user_id, @invite.user_id_target = art.id, bob.id
    @invite.save!
    
    invite = Invite.new(:user_id => art, :user_id_target => bob)
    invite.should have(1).errors_on(:user_id_target)
    
    invite = Invite.new(:user_id => bob, :user_id_target => art)
    invite.should have(1).errors_on(:user_id_target)
  end
  
end

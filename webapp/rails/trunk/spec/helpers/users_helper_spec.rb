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

describe UsersHelper do
  include UsersHelper
  
  def login
    @user = mock_model(User)
    User.stub!(:find).and_return(@user)
    session[:user_id] = @user.id
  end
  
  describe "handling display_name" do
  
    before(:each) do
      @user = mock_model(User)
    end
    
    it "without logging in should display 'Your'" do
      stub!(:'you?').and_return(true)
      display_name(@user).should == "Your"
    end
    
    it "with logging in should append an 'S'" do
      stub!(:'you?').and_return(false)
      @user.stub!(:username).and_return("j0nathan")
      display_name(@user).should == "j0nathan's"
    end
  
  end
  
  describe "handling search_results_header" do
  
    it "should make the search results string" do
      @query = "asdf"
      search_results_header.should == "Search Results for \"asdf\""
    end
    
    it "should handle nil strings" do
      search_results_header.should == "Search Results for \"\""
    end
  
  end
  
  describe "handling friendship_action" do
  
    before(:each) do
      @user1 = mock_model(User)
      @user2 = mock_model(User)
    end
    
    it "should not allow friendships with yourself" do
      should_not_receive(:render)
      friendship_action(@user1,@user1)
    end
    
    it "should render remove_link if they are already friends" do
      @user1.should_receive(:friends).and_return([@user2])
      should_receive(:render).with({:partial => 'friends/remove_link', :locals => { :user => @user1, :target => @user2 }})
      friendship_action(@user1,@user2)
    end
    
    it "should render approve/reject links if user2 is incoming_invite" do
      @user1.stub!(:friends).and_return([])
      @incoming_invite_ids = [@user2.id]
      should_receive(:render).with({:partial => 'friends/approve_link', :locals => { :user => @user1, :target => @user2 }})
      should_receive(:render).with({:partial => 'friends/reject_link', :locals => { :user => @user1, :target => @user2 }})
      nil.stub!(:'+') # the "out" string gets concatenated with each render
      friendship_action(@user1,@user2)
    end
    
    it "should render revoke links if user2 was invited" do
      @user1.stub!(:friends).and_return([])
      @incoming_invite_ids = []
      @outgoing_invite_ids = [@user2.id]
      should_receive(:render).with({:partial => 'friends/revoke_link', :locals => { :user => @user1, :target => @user2 }})
      friendship_action(@user1,@user2)
    end
    
    it "should render add links if there has been no user1/user2 invites" do
      @user1.stub!(:friends).and_return([])
      @incoming_invite_ids = []
      @outgoing_invite_ids = []
      should_receive(:render).with({:partial => 'friends/add_link', :locals => { :user => @user2 }})
      friendship_action(@user1,@user2)
    end
  
  end
  
end

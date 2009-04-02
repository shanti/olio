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

describe FriendsController do
  describe "handling GET /users/1/friends" do

    before(:each) do
      @invite = mock_model(Invite)
      Invite.stub!(:find).and_return([@invite])
      @user = mock_model(User)
      User.stub!(:find).with('1').and_return(@user)
      @user.stub!(:friends).and_return([@invite])
    end
  
    def do_get
      get :index, :user_id => '1'
    end
  
    it "should be successful" do
      do_get
      response.should be_success
    end

    it "should render index template" do
      do_get
      response.should render_template('index')
    end
    
    it "should assign the found invites for the view" do
      do_get
      assigns[:friends].should == [@invite]
    end
  end

  describe "handling GET /users/1/friends.xml" do

    before(:each) do
      @invite = mock_model(Invite, :to_xml => "XML")
      Invite.stub!(:find).and_return(@invite)
      @user = mock_model(User)
      User.stub!(:find).with('1').and_return(@user)
      @user.stub!(:friends).and_return(@invite)
      @user.friends.stub!(:paginate).and_return(@invite)
    end
  
    def do_get
      @request.env["HTTP_ACCEPT"] = "application/xml"
      get :index, :user_id => '1'
    end
  
    it "should be successful" do
      do_get
      response.should be_success
    end

    it "should find all invites" do
      @user.should_receive(:friends).and_return([@invite])
      do_get
    end
  
    it "should render the found invites as xml" do
      @invite.should_receive(:to_xml).and_return("XML")
      do_get
      response.body.should == "XML"
    end
  end

  describe "handling POST /users/1/friends" do

    before(:each) do
      @user = mock_model(User)
      session[:user_id] = @user.id.to_s
      @target = mock_model(User)
      @user.stub!(:invite).with(@target).and_return(true)
      User.stub!(:find).with(@user.id.to_s).and_return(@user)
      User.stub!(:find).with(@target.id.to_s).and_return(@target)
    end
    
    describe "with successful save" do
  
      def do_post
        post :create, :user_id => @target.id.to_s
      end
  
      it "should create a new invite" do
        do_post
      end

      it "should redirect to the new invite" do
        @user.should_receive(:invite).with(@target).and_return(true)
        do_post
        response.should redirect_to(search_users_path)
      end
      
    end
    
    describe "with failed save" do

      def do_post(target = @target)
        @user.should_receive(:invite).with(target).and_return(false)
        post :create, :user_id => target.id.to_s
      end
  
      it "should redirect to users" do
        do_post
        response.should redirect_to(search_users_path)
      end
      
      it "should not allow you to friend yourself" do
        do_post(@user)
        flash[:error].should == "Could not request friendship."
      end

      it "should not allow users to initiate duplicate requests" do
        Invite.create(:user => @user, :user_target => @target)
        do_post(@target)
        flash[:error].should == "Could not request friendship."
      end
      
    end
    
    describe "with invalid users" do 
    
      def do_post(xhr_request = false)
        User.should_receive(:find).with("0").and_raise(ActiveRecord::RecordNotFound)
        if xhr_request 
          xhr :post, :create, :user_id => "0"
        else 
          post :create, :user_id => "0"
        end
      end
    
      it "should not allow invalid users to create friendships" do 
        do_post        
        flash[:error].should == "Invalid source or target user."
        response.should redirect_to(search_users_path)
      end
    
      it "should not allow invalid users to create friendships (with AJAX)" do 
        do_post(true)
        flash[:error].should == "Invalid source or target user."
      end
      
    end
        
  end

  describe "handling PUT /users/1/friends/1" do

    before(:each) do
      @user = mock_model(User)
      session[:user_id] = @user.id
      @target = mock_model(User)
      @user.stub!(:accept).with(@target).and_return(true)
      @user.stub!(:friends).and_return([])
      @target.stub!(:accept).with(@user).and_return(true)
      User.stub!(:find).with(@user.id).and_return(@user)
      User.stub!(:find).with(@user.id.to_s).and_return(@user)
      User.stub!(:find).with(@target.id.to_s).and_return(@target)
    end
    
    describe "with successful update" do

      def do_put
        put :update, :id => @target.id.to_s, :user_id => @user.id.to_s
      end

      it "should find the invite requested" do
        @user.should_receive(:accept).with(@target).and_return(true)
        do_put
      end

      it "should update the found invite" do
        do_put
        assigns(:invite).should equal(@invite)
      end

      it "should assign the found invite for the view" do
        do_put
        assigns(:invite).should equal(@invite)
      end

      it "should redirect to the invite" do
        do_put
        response.should redirect_to(user_url(@user))
      end

    end
    
    describe "with failed update" do

      def do_put(xhr_request = false)
        if xhr_request
          xhr :put, :update, :id => @target.id.to_s, :user_id => @user.id.to_s
        else
          put :update, :id => @target.id.to_s, :user_id => @user.id.to_s
        end
      end

      def do_put_for_invalid(xhr_request = false)
        User.should_receive(:find).with("0").and_raise(ActiveRecord::RecordNotFound)
        @user.should_not_receive(:accept)
        yield
        flash[:error].should == "Invalid source or target user."
      end

      it "should re-render 'edit'" do
        @user.should_receive(:accept).with(@target).and_return(false)
        do_put
        response.should redirect_to(user_url(@user))
      end
      
      it "should show an error message on a failed AJAX request" do
        @user.should_receive(:accept).with(@target).and_return(false)
        do_put(true)
        flash[:error].should == 'Friendship could not be approved.'
      end

      it "should not succeed with an invalid source user" do
        do_put_for_invalid do
          put :update, :user_id => "0", :id => @target.id.to_s
        end        
      end

      it "should not succeed with an invalid target user" do
        do_put_for_invalid do
          put :update, :user_id => @user.id.to_s, :id => "0"
        end
      end

    end
  end

  describe "handling DELETE /users/1/friends/1" do

    before(:each) do
      @user = mock_model(User)
      session[:user_id] = @user.id
      @target = mock_model(User)
      @user.stub!(:unfriend).with(@target).and_return(true)
      @user.stub!(:friends).and_return([])
      @target.stub!(:username).and_return(@target.username)
      User.stub!(:find).with(@user.id).and_return(@user)
      User.stub!(:find).with(@user.id.to_s).and_return(@user)
      User.stub!(:find).with(@target.id.to_s).and_return(@target)
    end
    
    def do_delete(action = "remove")
      delete :destroy, :id => @target.id.to_s, :user_id => @user.id.to_s, :friend_action => action
    end
    
    it "should find the invite requested" do
      do_delete
    end
    
    it "should call destroy on the found invite" do
      @user.should_receive(:unfriend).with(@target).and_return(true)
      do_delete
    end
    
    it "should redirect to the invites list" do
      do_delete
      response.should redirect_to(user_url(@user))
    end

    it "should be able to revoke an invite" do
      Invite.create(:user => @user, :user_target => @target)
      @user.should_receive(:unfriend).with(@target).and_return(true)
      do_delete("revoke")
      flash[:notice].should == "You've revoked your friendship request to #{@target.username}."
      response.should redirect_to(user_path(@user))
    end
    
    it "should not be able to revoke another's invite" do
      Invite.create(:user => @target, :user_target => @user)
      @user.should_receive(:unfriend).with(@target).and_return(false)
      do_delete("revoke")
      flash[:error].should == "You have not requested to be #{@target.username}'s friend."
      response.should redirect_to(user_path(@user))
    end
    
    it "should be able to reject an invite" do
      Invite.create(:user => @user, :user_target => @target)
      @user.should_receive(:unfriend).with(@target).and_return(true)
      do_delete("reject")
      flash[:notice].should == "You rejected #{@target.username}'s friendship request."
      response.should redirect_to(user_path(@user))
    end
    
    it "should not be able to reject another's invite" do
      Invite.create(:user => @target, :user_target => @user)
      @user.should_receive(:unfriend).with(@target).and_return(false)
      do_delete("reject")
      flash[:error].should == "#{@target.username} did not request to be your friend."
      response.should redirect_to(user_path(@user))
    end
    
    it "should not do anything destructive given an invalid source user" do
      @user.should_not_receive(:unfriend)
      User.should_receive(:find).with("0").and_raise(ActiveRecord::RecordNotFound)
      delete :destroy, :id => "0", :user_id => @user.id.to_s, :friend_action => "remove"
      flash[:error].should == "Invalid source or target user."
    end
    
    it "should not do anything destructive given an invalid target user" do
      @user.should_not_receive(:unfriend)
      User.should_receive(:find).with("0").and_raise(ActiveRecord::RecordNotFound)
      delete :destroy, :id => @target.id.to_s, :user_id => "0", :friend_action => "remove"
      flash[:error].should == "Invalid source or target user."
    end
  end
end

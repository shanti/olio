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

describe ApplicationController do

  def login
    @user = mock_model(User)
    User.stub!(:find).and_return(@user)
    session[:user_id] = @user.id
  end

  class FooController < ApplicationController
    
    before_filter :authorize, :only => [ :index ]
    before_filter :validate_event, :only => [:valid_event]

    def index
      render :text => "Success!"
    end
    
    def login_as
      if logged_in_as(params[:user])
        render :text => "Success! Logged_in_as..."
      else
        render :text => "Not logged_in_as..."
      end
    end
    
    def valid_event
      render :text => "Success! Valid_event..."
    end
    
    def friends
      @user = User.find(params[:id])
      generate_friend_cloud @user.friends
      render :text => "Success! Friend cloud..."
    end
    
  end
  
  before(:all) do
    ActionController::Routing::Routes.draw do |map|
      map.resources :foo, :collection => { :login_as => :get, 
                                           :valid_event => :get,
                                           :friends => :get }
      map.root :controller => "foo"
    end
  end
  
  after(:all) do
    eval IO.read(RAILS_ROOT + "/config/routes.rb")
  end
  
  describe "handling access while logged in" do
  
    controller_name :foo
  
    before(:each) do
      login
    end
  
    def do_get
      get :index
    end
    
    it "should be successful" do
      do_get
      response.should be_success
    end
    
    it "should find user logged in" do
      User.should_receive(:find).and_return(@user)
      do_get
    end
    
    it "should assign the found user for the view" do
      do_get
      assigns[:user].should == @user
    end
    
    it "should render a success message" do
      do_get
      response.body.should == "Success!"
    end

  end
  
  describe "handling access while not logged in" do
  
    controller_name :foo
    
    before(:each) do
      User.stub!(:find).and_return(nil)
    end
  
    def do_get
      get :index
    end
  
    it "should not be successful" do
      do_get
      response.should_not be_success
    end
    
    it "should give a flash error" do
      do_get
      flash[:error].should == "You must log in before accessing that page."
    end
    
    it "should redirect the page" do
      do_get
      response.should redirect_to(root_path)
    end
  
  end
  
  describe "handling logged_in_as" do
  
    describe "with nil user" do
    
      controller_name :foo
      
      def do_login_without_user
        get :login_as
      end
      
      it "should be successful" do
        do_login_without_user
        response.should be_success
      end
      
      it "should render the text" do
        do_login_without_user
        response.body.should == "Not logged_in_as..."
      end
    
    end
    
    describe "with logged in user" do
    
      controller_name :foo
    
      def do_login_as_user
        login
        get :login_as, :user => @user
      end
      
      it "should be successful" do
        do_login_as_user
        response.should be_success
      end
      
      it "should render the text" do
        do_login_as_user
        response.body.should == "Success! Logged_in_as..."
      end
    
    end
  
  end
  
  describe "handling generate_friends_cloud" do
  
    controller_name :foo
    
    before(:each) do
      @user = mock_model(User)
      User.stub!(:find).and_return(@user)
    end
    
    def mock_friends
      @f1 = mock_model(User)
      @f2 = mock_model(User)
      @f3 = mock_model(User)
      @f4 = mock_model(User)
      @f5 = mock_model(User)
      @f6 = mock_model(User)
      @f7 = mock_model(User)
    end
    
    def expect_7_friends
      mock_friends
      @array = [ @f1, @f2, @f3, @f4, @f5, @f6, @f7 ]
      @user.should_receive(:friends).and_return(@array)
    end
    
    def expect_3_friends
      mock_friends
      @array = [ @f1, @f2, @f3 ]
      @user.should_receive(:friends).and_return(@array)
    end
    
    def do_get
      get :friends, :id => "2"
    end
    
    it "should generate successful text" do
      expect_3_friends
      do_get
      response.body.should == "Success! Friend cloud..."
    end
    
    it "should generate friend cloud of size 6 (if user has > 7 friends)" do
      expect_7_friends
      do_get
      assigns[:friendcloud].size.should == 6
      assigns[:friendcloud].uniq.size.should == 6
    end
    
    it "should generate friend cloud of size < 6 (if user has < 6 friends)" do
      expect_3_friends
      do_get
      assigns[:friendcloud].size.should < 6
      assigns[:friendcloud].size.should == 3
    end
    
    it "should include only first 3 mock users" do
      expect_3_friends
      do_get
      assigns[:friendcloud].should include(@f1)
      assigns[:friendcloud].should include(@f2)
      assigns[:friendcloud].should include(@f3)
      assigns[:friendcloud].should_not include(@f4)
    end
  
  end
  
  describe "handling validate_events" do
  
    def do_get
      get :valid_event, :event_id => "2"
    end
    
    describe "with valid events" do
    
      controller_name :foo
    
      before(:each) do
        @event = mock_model(Event)
        Event.stub!(:find).and_return(@event)
      end

      it "should be successful" do
        do_get
        response.should be_success
      end
      
      it "should render text successfully" do
        do_get
        response.body.should == "Success! Valid_event..."
      end
      
      it "should perform a search on events" do
        Event.should_receive(:find).and_return(@event)
        do_get
      end
      
    end
    
    describe "with 'invalid' events" do
    
      controller_name :foo
    
      before(:each) do
        @event = mock_model(Event)
        Event.stub!(:find).and_raise(ActiveRecord::RecordNotFound)
      end

      it "should be successful" do
        do_get
        response.should_not be_success
      end
      
      it "should render flash error" do
        do_get
        flash[:error] == "Event does not exist."
      end
      
      it "should not be able to find the event" do
        Event.should_receive(:find).and_raise(ActiveRecord::RecordNotFound)
        do_get
      end
      
      it "should redirect the page" do
        do_get
        response.should redirect_to(root_path)
      end
      
    end
  
  end

end

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

describe UsersController do

  def login
    @user = mock_model(User)
    User.stub!(:find).and_return(@user)
    session[:user_id] = @user.id
  end

  describe "handling GET /users" do
  
    before(:each) do
      @users = mock_model(User)
      User.stub!(:find).and_return([@users])
    end
  
    def do_get
      get :index
    end
  
    it "should be successful" do
      do_get
      response.should be_success
    end
  
    it "should render index template" do
      do_get
      response.should render_template('index')
    end
  
    it "should find all of the users" do
      User.should_receive(:paginate).and_return([@users])
      do_get
    end
    
    it "should assign the found users for the view" do
      do_get
      assigns[:users].should == [@users]
    end
  
  end

  #describe "handling GET /users.xml" do
  #end

  describe "handling GET /users/search" do
    
    before(:each) do
      login
      @users = mock_model(User, :lastname => "smith")
      User.stub!(:search).and_return(@users)
      @user.stub!(:outgoing_invite_ids).and_return([@users.id])
      @user.stub!(:incoming_invite_ids).and_return([@users.id])
    end
   
    def do_get
      get :search, :query => "smit"
    end
      
    it "should be successful" do
      do_get
      response.should be_success
    end
      
    it "should render the search template" do
      do_get
      response.should render_template('search')
    end

    it "should perform a search" do
      User.should_receive(:search).and_return(@users)
      do_get
    end
    
    it "should call invites_for_friend_links if logged in" do
      @user.should_receive(:outgoing_invite_ids)
      @user.should_receive(:incoming_invite_ids)
      do_get
    end
    
    it "should assign the found users for the view" do
      do_get
      assigns[:users].should == @users
      assigns[:query].should == params[:query]
    end
    
  end
  
  #describe "handling GET /users/search.xml" do
  #end
  
  describe "handling GET /users/1" do

    before(:each) do
      @address = mock_model(Address)
      @image = mock_model(Image)
      
      Address.stub!(:find).and_return(@address)
      
      @user = mock_model(User, :address_id => @address.id, :image_id => @image.id)
      @you = mock_model(User)
      
      @user.should_receive(:address_id).and_return(@address_id)
      @user.should_receive(:image).and_return(@image)
      @user.should_receive(:posted_events).and_return([])
    end
    
    def expect_friends
      @friend = mock_model(User)
      @user.should_receive(:friends).and_return([@friend])
      @user.should_receive(:incoming_friend_requests).and_return([@friend])
      @user.should_receive(:outgoing_friend_requests).and_return([@friend])
      @you.should_receive(:outgoing_invite_ids).and_return([@friend.id])
      @you.should_receive(:incoming_invite_ids).and_return([@friend.id])
    end
    
    def expect_no_friends
      @user.should_receive(:friends).and_return([])
      @user.should_receive(:incoming_friend_requests).and_return([])
      @user.should_receive(:outgoing_friend_requests).and_return([])
    end
    
    def do_get
      get :show, :id => "1"
    end
    
    describe "without logging in" do
    
      before(:each) do
        User.should_receive(:find).and_return(@user, @you)
        expect_no_friends
      end
    
      it "should be successful" do
        do_get
        response.should be_success
      end
    
      it "should render the show template" do
        do_get
        response.should render_template("show")
      end
      
      it "should assign the proper variables for the view" do
        do_get
        assigns[:user].should == @user
        assigns[:you].should == @you
        assigns[:address].should == @address
        assigns[:image].should == @image
        assigns[:posted].should == []
        assigns[:friends].should == []
        assigns[:incoming].should == []
        assigns[:outgoing].should == []
        assigns[:incoming_invite_ids].should == []
        assigns[:outgoing_invite_ids].should == []
      end
    
    end
    
    describe "with logging in" do
    
      before(:each) do
        session[:user_id] = @you.id
        User.should_receive(:find).and_return(@user, @you, @you)
      end
      
      describe "with friends do" do
      
        before(:each) do
          expect_friends
          do_get
        end
      
        it "should be successful" do
          response.should be_success
        end
        
        it "should render the show template" do
          response.should render_template("show")
        end
        
        it "should render variables for the view" do
          assigns[:user].should == @user
          assigns[:you].should == @you
          assigns[:address].should == @address
          assigns[:image].should == @image
          assigns[:posted].should == []
          assigns[:friends].should == [@friend]
          assigns[:incoming].should == [@friend]
          assigns[:outgoing].should == [@friend]
          assigns[:incoming_invite_ids].should == [@friend.id]
          assigns[:outgoing_invite_ids].should == [@friend.id]
        end
        
      end
      
      describe "without friends/invites" do
        
        before(:each) do
          @you.should_receive(:outgoing_invite_ids).and_return([])
          @you.should_receive(:incoming_invite_ids).and_return([])
          expect_no_friends
          do_get
        end
        
        it "should be successful" do
          response.should be_success
        end
        
        it "should render the show template" do
          response.should render_template("show")
        end
        
        it "should render variables for the view" do
          assigns[:user].should == @user
          assigns[:you].should == @you
          assigns[:address].should == @address
          assigns[:image].should == @image
          assigns[:posted].should == []
          assigns[:friends].should == []
          assigns[:incoming].should == []
          assigns[:outgoing].should == []
          assigns[:incoming_invite_ids].should == []
          assigns[:outgoing_invite_ids].should == []
        end
      
      end
    
    end
    
  end
  
  #describe "handling GET /users/1.xml" do
  #end
  
  describe "handling GET /users/new" do
    before(:each) do
      @user = mock_model(User)
      @address = mock_model(User) 
      User.stub!(:new).and_return(@user)
      Address.stub!(:new).and_return(@address)
    end
    
    def do_get
      get :new
    end
    
    it "should be successful" do
      do_get
      response.should be_success
    end
    
    it "should render new template" do
      do_get
      response.should render_template('new')
    end
    
    it "should create a new user and address" do
      User.should_receive(:new).and_return(@user)
      Address.should_receive(:new).and_return(@address)
      do_get
    end
    
    it "should not save the new user and address" do
      @user.should_not_receive(:save)
      @address.should_not_receive(:save)
      do_get
    end
    
    it "should assign the new user and address for the view" do
      do_get
      assigns[:user].should == @user
      assigns[:address].should == @address
    end
    
  end

  describe "handling GET /users/new.xml" do

    before(:each) do
      @user = mock_model(User)
      @address = mock_model(User)
      User.stub!(:new).and_return(@user)
      Address.stub!(:new).and_return(@address)
    end
    
    def do_get
      @request.env["HTTP_ACCEPT"] = "application/xml"
      get :new
    end
    
    it "should be successful" do
      do_get
      response.should be_success
    end
    
    it "should not render new template" do
      do_get
      response.should_not render_template('new')
    end
    
    it "should create a new user and address" do
      User.should_receive(:new).and_return(@user)
      Address.should_receive(:new).and_return(@address)
      do_get
    end
    
    it "should not save the new user and address" do
      @user.should_not_receive(:save)
      @address.should_not_receive(:save)
      do_get
    end
    
    it "should assign the new user and address for the view" do
      do_get
      assigns[:user].should == @user
      assigns[:address].should == @address
    end

    it "should render the new user as xml" do
      @user.should_receive(:to_xml).and_return("XML")
      do_get
      response.body.should == "XML"
    end
    
  end
  
  describe "handling POST /users" do
  
    before(:each) do
      @address = mock_model(Address, :id => 12)
      @user = mock_model(User, :address_id => 12)
      User.stub!(:new).and_return(@user)
      Address.stub!(:new).and_return(@address)
    end
    
    describe "with successful transaction" do
    
      before(:each) do
        @user.stub!(:'save!').and_return(true)
        @address.stub!(:'save!').and_return(true)
        @user.stub!(:'address_id=')
      end
    
      def do_post
        @address.stub!(:'save!').and_return(true)
        @user.stub!(:'save!').and_return(true)
        post :create, :user => {}, :address => {}, :user_image => ''
      end
      
      it "should not be successful" do
        do_post
        response.should_not be_success
      end
      
      it "should create a new user and address" do
        User.should_receive(:new).and_return(@user)
        Address.should_receive(:new).and_return(@address)
        do_post
      end
      
      it "should save the user and address" do
        @address.should_receive(:'save!').and_return(true)
        @user.should_receive(:'save!').and_return(true)
        do_post
      end
      
      it "should have a successful flash notice" do
        do_post
        flash[:notice].should == "Succeeded in creating user."
      end
      
      it "should redirect you" do
        do_post
        response.should redirect_to(root_path)
      end
      
      it "should assign variables for use with the view" do
        do_post
        assigns[:user].should == @user
        assigns[:address].should == @address
      end
      
      describe "with image parameter" do
      
        def do_post_with_image
          Image.should_receive(:make_from_upload).and_return(true)
          @user.should_receive(:'image=').and_return(true)
          post :create, :user => {}, :address => {}, :user_image => 'picture.jpg'
        end
      
        it "should be successful if there is an image parameter" do
          do_post_with_image
          response.should_not be_success
          flash[:notice].should == "Succeeded in creating user."
        end
        
        it "should redirect you with an image parameter" do
          do_post_with_image
          response.should redirect_to(root_path)
        end
        
        it "should assign variables for use with the view" do
          do_post_with_image
          assigns[:user].should == @user
          assigns[:address].should == @address
        end
      
      end
      
    end
    
    describe "with unsuccessful transaction" do
    
      before(:each) do
        @user.stub!(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        @address.stub!(:'save!').and_return(true)
        @user.stub!(:'address_id=')
      end
    
      def do_post
        post :create, :user => {}, :address => {}, :user_image => ''
      end
      
      it "should create be successful" do
        do_post
        response.should be_success
      end
      
      it "should not save address or user" do
        @address.should_receive(:'save!').and_return(false)
        @user.should_receive(:'save!').and_return(false)
        do_post
      end
      
      it "should have a flash error" do
        do_post
        flash[:error].should == "Failed to create user."
      end
      
      it "should should render the new template" do
        do_post
        response.should render_template("new")
      end
    
    end
  
  end

  describe "handling GET /users/1/edit" do
    
    before(:each) do
      login
      @address = mock_model(Address)
      Address.stub!(:find).and_return(@address)
      @user.stub!(:address_id).and_return(@address.id)
    end

    describe "with logging in" do

      def do_get
        get :edit, :id => 1
      end

      it "should be successful" do
        do_get
        response.should be_success
      end
      
      it "should allow you to find user" do
        User.should_receive(:find).and_return(@user, @user)
        do_get
      end
      
      it "should allow you to find the address" do
        Address.should_receive(:find).and_return(@address)
        do_get
      end
      
      it "should render edit template" do
        do_get
        response.should render_template(:edit)
      end
      
      it "should assign variables for view" do
        do_get
        assigns[:user].should == @user
        assigns[:address].should == @address
      end
      
    end
    
    describe "without logging in" do
    
      before(:each) do
        session[:user_id] = nil
      end
    
      def do_get
        get :edit, :id => 1
      end
    
      it "should not be successful" do
        do_get
        response.should_not be_success
      end
      
      it "should not render the edit template" do
        do_get
        response.should_not render_template("edit")
      end
      
      it "should allow you to find user" do
        User.should_receive(:find).and_return(@user, @user)
        do_get
      end
      
      it "should allow you to find the address" do
        Address.should_receive(:find).and_return(@address)
        do_get
      end
      
      it "should give error message" do
        do_get
        flash[:error].should == "Failed to edit user."
      end
      
      it "should redirect you if you're not logged in" do
        do_get
        response.should redirect_to(events_path)
      end
      
    end
  
  end

  describe "handling PUT /users/1" do
  
    before(:each) do
      login
      @address = mock_model(Address)
      @user.stub!(:username).and_return(@user.username)
      @user.stub!(:address).and_return(@address)
      @address.stub!(:'attributes=')
      @user.stub!(:'attributes=')
    end

    describe "with successful save" do
    
      before(:each) do
        @address.stub!(:'save!').and_return(true)
        @user.stub!(:'save!').and_return(true)
      end
    
      def do_put
        put :update, :id => 1, :user_image => ''
      end
      
      it "should be successful" do
        do_put
        response.should_not be_success
      end
      
      it "should give a successful flash notice" do
        do_put
        flash[:notice].should == "Succeeded in updating user."
      end
      
      it "should not render the update template" do
        do_put
        response.should_not render_template("user")
      end
      
      it "should redirect you to the user page" do
        do_put
        response.should redirect_to(user_path(@user))
      end
      
      it "should find the user" do
        User.should_receive(:find).with("1").and_return(@user)
        do_put
      end
      
      it "should successfully attempt to change the attributes" do
        @address.should_receive(:'attributes=')
        @user.should_receive(:'attributes=')
        do_put
      end
      
      it "should successfully save the user and address" do
        @address.should_receive(:'save!').and_return(true)
        @user.should_receive(:'save!').and_return(true)
        do_put
      end
      
      it "should properly assign the session username" do
        session[:user_name].should == @user.username
        do_put
      end
      
      it "should assign the variables for use with the view" do
        do_put
        assigns[:user].should == @user
        assigns[:address].should == @user.address
        assigns[:image].should == nil
      end
      
      describe "with image parameter" do
      
        def do_put_with_image
          Image.should_receive(:make_from_upload).and_return(true)
          @user.should_receive(:'image=').and_return(true)
          put :update, :id => 1, :user_image => 'picture.jpg'
        end
      
        it "should be successful if there is an image parameter" do
          do_put_with_image
          response.should_not be_success
          flash[:notice].should == "Succeeded in updating user."
        end
        
        it "should redirect you with an image parameter" do
          do_put_with_image
          response.should redirect_to(user_path(@user))
        end
        
        it "should assign variables for use with the view" do
          do_put_with_image
          assigns[:user].should == @user
          assigns[:address].should == @address
        end
      
      end
  
    end
    
    describe "with unsuccessful save" do
    
      def do_put
        @address.stub!(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        @user.stub!(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        put :update, :id => 1, :user_image => ''
      end
      
      it "should be successful" do
        do_put
        response.should be_success
      end
      
      it "should render the edit template" do
        do_put
        response.should render_template("edit")
      end
      
      it "should find the user" do
        User.should_receive(:find).with("1").and_return(@user)
        do_put
      end
      
      it "should receive unsuccessful save" do
        @address.should_receive(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        @user.should_not_receive(:'save!')
        do_put
      end
      
      it "should assign the variables for use with the view" do
        do_put
        assigns[:user].should == @user
        assigns[:address].should == @user.address
        assigns[:image].should == nil
      end
      
    end
  
  end

  describe "handling GET /users/login" do
    
    def do_get
      get :login
    end
    
    it "should be successful" do
      do_get
      response.should be_success
    end
    
    it "should render the login template" do
      do_get
      response.should render_template('login')
    end
    
    it "should set session[:user_id] to nil even if logged in" do
      login
      do_get
      session[:user_id].should == nil
    end
  
  end
  
  describe "handling POST /users/login" do
  
    before(:each) do
      @user = mock_model(User)
    end
    
    def do_post
      post :login, :users => {}
    end
    
    describe "with successful login" do
    
      before(:each) do
        User.stub!(:find_by_username_and_password).and_return(@user)
        @user.stub!(:username).and_return(@user.username)
        @user.stub!(:password).and_return(@user.password)
        @user.stub!(:incoming_friend_requests).and_return([])
        @user.stub!(:upcoming_events).and_return([])
      end
    
      it "should not be successful" do
        do_post
        response.should_not be_success
      end
      
      it "should redirect you to original URL" do
        session[:original_uri] = "/comments"
        do_post
        response.should redirect_to("/comments")
      end
      
      it "should redirect you to event path w/o original URL" do
        do_post
        response.should redirect_to(events_path)
      end
      
      it "should give a successful notice" do
        do_post
        flash[:notice].should == "Successfully logged in!"
      end
      
      it "should ask for your password" do
        @user.should_receive(:password).and_return(@user.password)
        do_post
      end
      
      it "should ask for your friend requests and upcoming events" do
        @user.should_receive(:incoming_friend_requests).and_return([])
        @user.should_receive(:upcoming_events).and_return([])
        do_post
      end
      
      it "should properly apply session parameters" do
        do_post
        session[:user_id].should == @user.id
        session[:user_name].should == @user.username
        session[:friend_requests].should == 0
        session[:upcoming].should == []
      end
      
    end
    
    describe "with unsuccessful login" do
    
      before(:each) do
        User.stub!(:"find_by_username_and_password=").and_return(nil)
        do_post
      end
    
      it "should not be successful" do
        response.should_not be_success
      end
      
      it "should redirect you" do
        response.should redirect_to(root_path)
      end
      
      it "should display the right message" do
        flash[:notice].should == "Invalid user/password combination."
      end
  
      it "should have nil parameters" do
        params[:email].should be_nil
        params[:password].should be_nil
      end
  
    end
  
  end

  describe "handling GET /users/logout" do

    before(:each) do
      login
      do_get
    end
    
    def do_get
      get :logout
    end
    
    it "should not be successful" do
      response.should_not be_success
    end
    
    it "should redirect" do
      response.should redirect_to(root_path)
    end
    
    it "should logout users that are currently logged in" do
      session[:user_id].should be_nil
      session[:user_name].should be_nil
    end
    
  end

  describe "handing POST /users/check_name" do
  
    describe "with check_name" do
      
      before(:each) do
        @user = mock_model(User)
        User.stub!(:find_by_username).and_return(@user)
      end
      
      def do_post
        post :check_name, :name => "john"
      end
      
      it "should be successful" do
        do_post
        response.should be_success
      end
      
      it "should ask for username" do
        User.should_receive(:find_by_username).and_return(@user)
        do_post
      end
      
      it "should display if username is taken" do
        do_post
        response.body.should == "Name taken"
      end
      
      it "should display if username is available" do
        # If username is not in database
        User.stub!(:find_by_username).and_return(false)
        do_post
        response.body.should == "Valid name"
      end
    
    end
    
    describe "with upcoming events" do
      
      before(:each) do
        login
        @event = mock_model(Event, :event_timestamp => "3008-06-13 11:54:12")
        User.stub!(:find_by_id).and_return(@user)
        @user.stub!(:upcoming_events).and_return(@event)
        @event.stub!(:paginate).with({:per_page=>10, :page=>"1"}).and_return([@event])
      end
      
      def do_post
        post :upcoming_events, :id => 10, :page => "1"
      end
      
      it "should be successful" do
        do_post
        response.should be_success
      end
      
      it "should render format" do
        do_post
        response.should render_template('upcoming_events')
      end
      
      it "should do the proper queries" do
        User.should_receive(:find_by_id).and_return(@user)
        @user.should_receive(:upcoming_events).and_return(@event)
        @event.should_receive(:paginate).with({:per_page=>10, :page=>"1"})
        do_post
      end
      
      it "should assign the variables for use with the view" do
        do_post
        assigns[:upcoming].should == [@event]
      end
      
    end
    
    describe "with posted events" do
      
      before(:each) do
        login
        @event = mock_model(Event, :event_timestamp => "3008-06-13 11:54:12", :user_id => @user.id)
        User.stub!(:find_by_id).and_return(@user)
        @user.stub!(:posted_events).and_return(@event)
        @event.stub!(:paginate).with({:per_page=>10, :page=>"1"}).and_return([@event])
      end
      
      def do_post
        post :posted_events, :id => 10, :page => 1
      end
      
      it "should be successful" do
        do_post
        response.should be_success
      end
      
      it "should render posted_events template" do
        do_post
        response.should render_template('posted_events')
      end
      
      it "should find the events posted by user" do
        User.should_receive(:find_by_id).with("10").and_return(@user)
        @user.should_receive(:posted_events).and_return(@event)
        @event.should_receive(:paginate).with({:per_page=>10, :page=>"1"})
        do_post
      end
      
      it "should assign the variables for use with the view" do
        do_post
        assigns[:posted].should == [@event]
      end
      
    end
  
  end

end

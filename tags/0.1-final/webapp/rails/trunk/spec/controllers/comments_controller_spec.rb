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

describe CommentsController do
  
  def login
    @user = mock_model(User)
    User.stub!(:find).and_return(@user)
    session[:user_id] = @user.id
  end
  
  def login_as_author
    login
    Comment.stub!(:find).and_return(@comment)
    @comment.stub!(:author).and_return(@user)
  end
  
  def login_as_other
    login
    @notauthor = mock_model(User)
    User.stub!(:find).and_return(@notauthor)
    Comment.stub!(:find).and_return(@comment)
    @comment.stub!(:author).and_return(@notauthor)
    session[:user_id] = @comment.author
  end
  
  before(:each) do
    @event = mock_model(Event)
    Event.stub!(:find).and_return(@event)
    @comment = mock_model(Comment)
  end
  
  describe "CommentsController assumptions" do
    
    it "should not be successful for invalid events" do
      Event.stub!(:find).and_raise(ActiveRecord::RecordNotFound)
      get :index, :event_id => 1
      response.should_not be_success
    end
    
    it "should redirect you to the root page for invalid events" do
      Event.stub!(:find).and_raise(ActiveRecord::RecordNotFound)
      get :index, :event_id => 1
      response.should redirect_to(root_path)
    end
    
    it "should not be successful for invalid comments given valid events" do
      Comment.stub!(:find).and_raise(ActiveRecord::RecordNotFound)
      get :show, :event_id => @event.id, :id => 1
      response.should_not be_success
    end
    
    it "should redirect you to the event page for invalid comments given valid events" do
      Comment.stub!(:find).and_raise(ActiveRecord::RecordNotFound)
      get :show, :event_id => @event.id, :id => 1
      response.should redirect_to(event_path(params[:event_id]))
    end
    
  end
  
  describe "CommentsController#new" do
    
    before(:each) do
      Comment.stub!(:new).with(:rating => 0).and_return(@comment)
    end
    
    it "should not allow access to users who are not logged in" do
      get :new, :event_id => 1, :id => 1
      response.should_not be_success
      response.should redirect_to(root_path)
    end
    
    it "should allow access to users who are logged in" do
      login
      get :new, :event_id => 1, :id => 1
      response.should be_success
    end
    
    it "should initialize the comment correctly" do
      login
      Comment.should_receive(:new).with(:rating => 0).and_return(@comment)
      get :new, :event_id => 1, :id => 1
    end
    
    it "should render new.html.erb" do
      login
      get :new, :event_id => 1, :id => 1
      response.should render_template("new")
    end
    
  end
  
  describe "CommentsController#index" do
    
    before(:each) do
      Comment.stub!(:find_all_by_event_id).and_return([@comment])
    end
    
    it "should be successful" do
      Comment.should_receive(:find_all_by_event_id).and_return([@comment])
      get :index, :event_id => 1
      response.should be_success
    end
    
    it "should render index.html.erb" do
      get :index, :event_id => 1
      response.should render_template("index")
    end
    
  end
  
  describe "CommentsController#show" do
    
    before(:each) do
      Comment.stub!(:find).and_return(@comment)
    end
    
    it "should be successful" do
      Comment.should_receive(:find).and_return(@comment)
      get :show, :event_id => 1, :id => 1
      response.should be_success
    end
    
    it "should render show.html.erb" do
      get :show, :event_id => 1, :id => 1
      response.should render_template("show")
    end
        
  end
  
  describe "CommentsController#create" do
    
    before(:each) do
      login
      Comment.should_receive(:new).and_return(@comment)
      @comment.should_receive(:'event_id=').and_return(params[:event_id])
      @comment.should_receive(:'user_id=').and_return(session[:user_id])
    end
    
    it "should create the comment" do
      @comment.should_receive(:'save').and_return(true)
      post :create, :event_id => @event.id, :comment => {}
      flash[:notice].should == "Thanks for your comment."
    end
    
    it "should redirect to the event path if successful" do
      @comment.should_receive(:'save').and_return(true)
      post :create, :event_id => @event.id, :comment => {}
      response.should redirect_to(event_path(@event.id))
    end
    
    it "should re-render the action if failed" do
      @comment.should_receive(:'save').and_return(false)
      post :create, :event_id => @event.id, :comment => {}
      flash[:error].should == "Sorry, your comment could not be created."
      response.should render_template("new")
    end
    
    it "should update the flash on AJAX error" do
      @comment.should_receive(:'save').and_return(false)
      xhr :post, :create, :event_id => @event.id, :comment => {}
      flash[:error].should == "Sorry, your comment could not be created."
      response.should be_success
    end
    
  end
  
  describe "CommentsController#edit" do
    
    it "should allow the author to edit" do
      login_as_author
      get :edit, :event_id => 1, :id => 1
      response.should be_success
    end
    
    it "should not allow others to edit" do
      login_as_other
      get :edit, :event_id => 1, :id => 1
      response.should_not be_success
    end
    
    it "should render edit.html.erb" do
      login_as_author
      get :edit, :event_id => 1, :id => 1
      response.should render_template("edit")
    end
    
  end
  
  describe "CommentsController#update" do
    
    it "should not allow access to others" do
      login_as_other
      put :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      flash[:error].should == "You did not write this comment."
      response.should redirect_to(event_path(@event.id))
    end
    
    it "should not allow access to others via AJAX" do
      login_as_other
      @comment.should_not_receive(:'update_attributes')
      xhr :put, :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      flash[:error].should == "You did not write this comment."      
    end
    
    it "should allow the author to update the comment" do
      login_as_author
      @comment.should_receive(:'update_attributes').and_return(true)
      put :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      flash[:notice].should == "Comment updated."
    end
    
    it "should redirect to the event path if successful" do
      login_as_author
      @comment.should_receive(:'update_attributes').and_return(true)
      put :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      response.should redirect_to(event_path(@event.id))
    end
    
    it "should re-render the action if failed" do
      login_as_author
      @comment.should_receive(:'update_attributes').and_return(false)
      put :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      flash[:error].should == "Unable to update comment."
      response.should render_template("edit")
    end
    
    it "should update the flash on AJAX error" do
      login_as_author
      @comment.should_receive(:'update_attributes').and_return(false)
      xhr :put, :update, :event_id => @event.id, :id => @comment.id, :comment => {}
      flash[:error].should == "Unable to update comment."
    end
    
  end
  
  describe "CommentsController#delete" do
    
    before(:each) do
      Comment.stub!(:find).and_return(@comment)
      login_as_author
    end
    
    it "should be successful" do
      Comment.should_receive(:find).and_return(@comment)
      get :delete, :event_id => 1, :id => 1
      response.should be_success
    end
    
    it "should render delete.html.erb" do
      get :delete, :event_id => 1, :id => 1
      response.should render_template("delete")
    end
    
  end
  
  describe "CommentsController#destroy" do
    
    it "should not allow access to others" do
      login_as_other
      @comment.should_not_receive(:'destroy')
      delete :destroy, :event_id => @event.id, :id => @comment.id
      flash[:error].should == "You can't delete someone else's comment."
      response.should redirect_to(event_path(@event.id))
    end
    
    it "should allow the author to update the comment" do
      login_as_author
      @comment.should_receive(:'destroy')
      delete :destroy, :event_id => @event.id, :id => @comment.id
      flash[:notice].should == "Comment deleted."
    end
    
    it "should redirect to the event path if successful" do
      login_as_author
      @comment.should_receive(:'destroy')
      delete :destroy, :event_id => @event.id, :id => @comment.id
      response.should redirect_to(event_path(@event.id))
    end
    
    it "should update the flash on AJAX error" do
      login_as_other
      @comment.should_not_receive(:'destroy')
      xhr :delete, :destroy, :event_id => @event.id, :id => @comment.id
      flash[:error].should == "You can't delete someone else's comment."
      response.should be_success
    end
    
  end
  
end

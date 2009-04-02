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
  
  describe "route generation" do

    it "should map { :controller => 'friends', :action => 'index', :user_id => '1' } to /users/1/friends" do
      route_for(:controller => "friends", :action => "index", :user_id => "1").should == "/users/1/friends"
    end
  
    it "should map { :controller => 'friends', :action => 'new', :user_id => '1' } to /users/1/friends/new" do
      route_for(:controller => "friends", :action => "new", :user_id => "1").should == "/users/1/friends/new"
    end
  
    it "should map { :controller => 'friends', :action => 'show', :id => 1, :user_id => '1' } to /friends/1" do
      route_for(:controller => "friends", :action => "show", :id => 1, :user_id => "1").should == "/users/1/friends/1"
    end
  
    it "should map { :controller => 'friends', :action => 'edit', :id => 1, :user_id => '1' } to /friends/1/edit" do
      route_for(:controller => "friends", :action => "edit", :id => 1, :user_id => "1").should == "/users/1/friends/1/edit"
    end
  
    it "should map { :controller => 'friends', :action => 'update', :id => 1, :user_id => '1' } to /friends/1" do
      route_for(:controller => "friends", :action => "update", :id => 1, :user_id => "1").should == "/users/1/friends/1"
    end
  
    it "should map { :controller => 'friends', :action => 'destroy', :id => 1, :user_id => '1' } to /friends/1" do
      route_for(:controller => "friends", :action => "destroy", :id => 1, :user_id => "1").should == "/users/1/friends/1"
    end
    
  end

  describe "route recognition" do

    it "should generate params { :controller => 'friends', action => 'index', :user_id => '1'  } from GET /users/1/friends" do
      params_from(:get, "/users/1/friends").should == {:controller => "friends", :action => "index", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'new', :user_id => '1'  } from GET /users/1/friends/new" do
      params_from(:get, "/users/1/friends/new").should == {:controller => "friends", :action => "new", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'create', :user_id => '1'  } from POST /users/1/friends" do
      params_from(:post, "/users/1/friends").should == {:controller => "friends", :action => "create", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'show', id => '1', :user_id => '1'  } from GET /users/1/friends/1" do
      params_from(:get, "/users/1/friends/1").should == {:controller => "friends", :action => "show", :id => "1", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'edit', id => '1', :user_id => '1'  } from GET /users/1/friends/1/edit" do
      params_from(:get, "/users/1/friends/1/edit").should == {:controller => "friends", :action => "edit", :id => "1", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'update', id => '1', :user_id => '1'  } from PUT /users/1/friends/1" do
      params_from(:put, "/users/1/friends/1").should == {:controller => "friends", :action => "update", :id => "1", :user_id => "1"}
    end
  
    it "should generate params { :controller => 'friends', action => 'destroy', id => '1', :user_id => '1'  } from DELETE /users/1/friends/1" do
      params_from(:delete, "/users/1/friends/1").should == {:controller => "friends", :action => "destroy", :id => "1", :user_id => "1"}
    end
    
  end
  
end

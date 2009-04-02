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
  
  describe "route generation" do
    
    it "should map { :controller => 'comments', :action => 'index', :event_id => '1' } to /events/1/comments" do
      route_for(:controller => "comments", :action => "index", :event_id => "1").should == "/events/1/comments"
    end
    
    it "should map { :controller => 'comments', :action => 'new', :event_id => '1' } to /events/1/comments/new" do
      route_for(:controller => "comments", :action => "new", :event_id => "1").should == "/events/1/comments/new"
    end
    
    it "should map { :controller => 'comments', :action => 'show', :id => 1, :event_id => '1' } to /events/1/comments/1" do
      route_for(:controller => "comments", :action => "show", :id => 1, :event_id => "1").should == "/events/1/comments/1"
    end
    
    it "should map { :controller => 'comments', :action => 'edit', :id => 1, :event_id => '1' } to /events/comments/1/edit" do
      route_for(:controller => "comments", :action => "edit", :id => 1, :event_id => "1").should == "/events/1/comments/1/edit"
    end
    
    it "should map { :controller => 'comments', :action => 'update', :id => 1, :event_id => '1' } to /events/1/comments/1" do
      route_for(:controller => "comments", :action => "update", :id => 1, :event_id => "1").should == "/events/1/comments/1"
    end
    
    it "should map { :controller => 'comments', :action => 'destroy', :id => 1, :event_id => '1' } to /events/1/comments/1" do
      route_for(:controller => "comments", :action => "destroy", :id => 1, :event_id => "1").should == "/events/1/comments/1"
    end
    
    it "should map { :controller => 'comments', :action => 'delete', :id => 1, :event_id => '1' } to /events/1/comments/1/delete" do
      route_for(:controller => "comments", :action => "delete", :id => 1, :event_id => "1").should == "/events/1/comments/1/delete"
    end
    
  end

  describe "route recognition" do

    it "should generate params { :controller => 'comments', action => 'index', :event_id => '1'  } from GET /events/1/comments" do
      params_from(:get, "/events/1/comments").should == {:controller => "comments", :action => "index", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'new', :event_id => '1'  } from GET /events/1/comments/new" do
      params_from(:get, "/events/1/comments/new").should == {:controller => "comments", :action => "new", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'create', :event_id => '1'  } from POST /events/1/comments" do
      params_from(:post, "/events/1/comments").should == {:controller => "comments", :action => "create", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'show', id => '1', :event_id => '1'  } from GET /events/1/comments/1" do
      params_from(:get, "/events/1/comments/1").should == {:controller => "comments", :action => "show", :id => "1", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'edit', id => '1', :event_id => '1'  } from GET /events/1/comments/1/edit" do
      params_from(:get, "/events/1/comments/1/edit").should == {:controller => "comments", :action => "edit", :id => "1", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'update', id => '1', :event_id => '1'  } from PUT /events/1/comments/1" do
      params_from(:put, "/events/1/comments/1").should == {:controller => "comments", :action => "update", :id => "1", :event_id => "1"}
    end
  
    it "should generate params { :controller => 'comments', action => 'destroy', id => '1', :event_id => '1'  } from DELETE /events/1/comments/1" do
      params_from(:delete, "/events/1/comments/1").should == {:controller => "comments", :action => "destroy", :id => "1", :event_id => "1"}
    end
    
    it "should generate params { :controller => 'comments', action => 'delete', id => '1', :event_id => '1' } from GET /events/1/comments/1/delete" do
      params_from(:get, "/events/1/comments/1/delete").should == {:controller => "comments", :action => "delete", :id => "1", :event_id => "1"}
    end
    
  end
  
end

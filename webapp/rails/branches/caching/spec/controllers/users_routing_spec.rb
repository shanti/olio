require File.dirname(__FILE__) + '/../spec_helper'

describe UsersController do
  
  describe "route generation" do
    
    it "should map { :controller => 'users', :action => 'index' } to /users" do
      route_for(:controller => "users", :action => "index").should == "/users"
    end
    
    it "should map { :controller => 'users', :action => 'search' } to /users/search" do
      route_for(:controller => "users", :action => "search").should == "/users/search"
    end
    
    it "should map { :controller => 'users', :action => 'show', :id => 1 } to /users" do
      route_for(:controller => "users", :action => "show", :id => 1).should == "/users/1"
    end
    
    it "should map { :controller => 'users', :action => 'new' } to /users/new" do
      route_for(:controller => "users", :action => "new").should == "/users/new"
    end
    
    it "should map { :controller => 'users', :action => 'create' } to /users" do
      route_for(:controller => "users", :action => "create").should == "/users"
    end
    
    it "should map { :controller => 'users', :action => 'edit', :id => 1 } to /users/1/edit" do
      route_for(:controller => "users", :action => "edit", :id => 1).should == "/users/1/edit"
    end
    
    it "should map { :controller => 'users', :action => 'login' } to /users/login" do
      route_for(:controller => "users", :action => "login").should == "/users/login"
    end
    
    it "should map { :controller => 'users', :action => 'logout' } to /users/logout" do
      route_for(:controller => "users", :action => "logout").should == "/users/logout"
    end
    
    it "should map { :controller => 'users', :action => 'check_name' } to /users/check_name" do
      route_for(:controller => "users", :action => "check_name").should == "/users/check_name"
    end
    
  end

  describe "route recognition" do

    it "should generate params { :controller => 'users', :action => 'index' } from GET /users" do
      params_from(:get, "/users").should == {:controller => "users", :action => "index"}
    end
    
    it "should generate params { :controller => 'users', :action => 'search' } from GET /users/search" do
      params_from(:get, "/users/search").should == {:controller => "users", :action => "search"}
    end
    
    it "should generate params { :controller => 'users', :action => 'show', :id => '1' } from GET /users/1" do
      params_from(:get, "/users/1").should == {:controller => "users", :action => "show", :id => "1"}
    end
    
    it "should generate params { :controller => 'users', :action => 'new' } from GET /users/new" do
      params_from(:get, "/users/new").should == {:controller => "users", :action => "new"}
    end
    
    it "should generate params { :controller => 'users', :action => 'create' } from POST /users/new" do
      params_from(:post, "/users").should == {:controller => "users", :action => "create"}
    end
    
    it "should generate params { :controller => 'users', :action => 'edit', :id => '1' } from GET /users/1/edit" do
      params_from(:get, "/users/1/edit").should == {:controller => "users", :action => "edit", :id => "1"}
    end
    
    it "should generate params { :controller => 'users', :action => 'update', :id => '1' } from PUT /users/1" do
      params_from(:put, "/users/1").should == {:controller => "users", :action => "update", :id => "1"}
    end
    
    it "should generate params { :controller => 'users', :action => 'login' } from GET /users/login" do
      params_from(:get, "/users/login").should == {:controller => "users", :action => "login"}
    end
    
    it "should generate params { :controller => 'users', :action => 'login' } from POST /users/login" do
      params_from(:post, "/users/login").should == {:controller => "users", :action => "login"}
    end
    
    it "should generate params { :controller => 'users', :action => 'logout' } from GET /users/logout" do
      params_from(:get, "/users/logout").should == {:controller => "users", :action => "logout"}
    end
    
    it "should generate params { :controller => 'users', :action => 'check_name' } from GET /users/check_name" do
      params_from(:post, "/users/check_name").should == {:controller => "users", :action => "check_name"}
    end
    
  end
  
end

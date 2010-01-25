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
require File.expand_path(File.dirname(__FILE__) + '<%= '/..' * class_nesting_depth %>/../spec_helper')

describe <%= controller_class_name %>Controller do
  describe "route generation" do
    it "should map #index" do
      route_for(:controller => "<%= table_name %>", :action => "index").should == "/<%= table_name %>"
    end
  
    it "should map #new" do
      route_for(:controller => "<%= table_name %>", :action => "new").should == "/<%= table_name %>/new"
    end
  
    it "should map #show" do
      route_for(:controller => "<%= table_name %>", :action => "show", :id => 1).should == "/<%= table_name %>/1"
    end
  
    it "should map #edit" do
      route_for(:controller => "<%= table_name %>", :action => "edit", :id => 1).should == "/<%= table_name %>/1<%= resource_edit_path %>"
    end
  
    it "should map #update" do
      route_for(:controller => "<%= table_name %>", :action => "update", :id => 1).should == "/<%= table_name %>/1"
    end
  
    it "should map #destroy" do
      route_for(:controller => "<%= table_name %>", :action => "destroy", :id => 1).should == "/<%= table_name %>/1"
    end
  end

  describe "route recognition" do
    it "should generate params for #index" do
      params_from(:get, "/<%= table_name %>").should == {:controller => "<%= table_name %>", :action => "index"}
    end
  
    it "should generate params for #new" do
      params_from(:get, "/<%= table_name %>/new").should == {:controller => "<%= table_name %>", :action => "new"}
    end
  
    it "should generate params for #create" do
      params_from(:post, "/<%= table_name %>").should == {:controller => "<%= table_name %>", :action => "create"}
    end
  
    it "should generate params for #show" do
      params_from(:get, "/<%= table_name %>/1").should == {:controller => "<%= table_name %>", :action => "show", :id => "1"}
    end
  
    it "should generate params for #edit" do
      params_from(:get, "/<%= table_name %>/1<%= resource_edit_path %>").should == {:controller => "<%= table_name %>", :action => "edit", :id => "1"}
    end
  
    it "should generate params for #update" do
      params_from(:put, "/<%= table_name %>/1").should == {:controller => "<%= table_name %>", :action => "update", :id => "1"}
    end
  
    it "should generate params for #destroy" do
      params_from(:delete, "/<%= table_name %>/1").should == {:controller => "<%= table_name %>", :action => "destroy", :id => "1"}
    end
  end
end

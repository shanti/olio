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
require File.dirname(__FILE__) + "/../spec_helper"

describe FixtureReplacement do
  it "should have the method attributes_for" do
    FixtureReplacement.should respond_to(:attributes_for)
  end
end

describe FixtureReplacement, "attributes_for" do
  before :each do
    @fixture_attribute = mock "FixtureAttribute"
    @fixture_attribute.stub!(:new)
    
    @attributes_proc = lambda {}
    Kernel.stub!(:lambda).and_return @attributes_proc
  end
  
  it "should take a fixture name" do
    FixtureReplacement.attributes_for(:foo) {}
  end
  
  it "should take a fixture name along with a hash of attributes" do
    FixtureReplacement.attributes_for(:foo, :bar => :baz, :baz => :zed) {}
  end
  
  it "should create a new FixtureAttribute with the name given" do
    @fixture_attribute.should_receive(:new).with(:foo, {:class => nil, :from => nil, :attributes => @attributes_proc})
    FixtureReplacement.attributes_for(:foo, {}, @fixture_attribute, &@attributes_proc) 
  end
  
  it "should create a new FixtureAttribute with the name given and class given" do
    @fixture_attribute.should_receive(:new).with(:foo, {:class => Object, :from => nil, :attributes => @attributes_proc})
    FixtureReplacement.attributes_for(:foo, {:class => Object}, @fixture_attribute, &@attributes_proc)
  end
  
  it "should create a new FixtureAttribute with the name given and the attributes from" do
    @fixture_attribute.should_receive(:new).with(:foo, {:class => nil, :from => :bar, :attributes => @attributes_proc})
    FixtureReplacement.attributes_for(:foo, {:from => :bar}, @fixture_attribute, &@attributes_proc)
  end
  
  it "should create a new FixtureAttribute with the name and block given" do
    @fixture_attribute.should_receive(:new).with(:foo, {:class => nil, :from => nil, :attributes => @attributes_proc})
    FixtureReplacement.attributes_for(:foo, {}, @fixture_attribute, &@attributes_proc)
  end
end

describe "FixtureReplacement.attributes_for" do
  it "should yield an OpenStruct" do
    FixtureReplacement.attributes_for :user do |u|      
      u.should be_a_kind_of(OpenStruct)
    end
  end
  
  it "should not raise an error if no block is given" do
    lambda {
      FixtureReplacement.attributes_for :scott, :from => :user  
    }.should_not raise_error
  end
  
  it "should create a new Attribute with the class, attributes_from, and the attributes as a lambda" do
    @proc = lambda { |os| }
    Kernel.stub!(:lambda).and_return @proc
    FixtureReplacementController::AttributeCollection.stub!(:new)
    FixtureReplacementController::AttributeCollection.should_receive(:new).with(:scott, {
      :class => User,
      :from => :user,
      :attributes => @proc
    })

    FixtureReplacement.attributes_for(:scott, :from => :user, :class => User, &@proc)
  end  
end
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

describe ApplicationHelper do
  include ApplicationHelper
  
  describe "handling user-centric actions" do
  
    before(:each) do
      @user = mock_model(User)
    end
  
    it "should not be logged in" do
      logged_in?.should be_false
    end
    
    it "should be logged in" do
      stub!(:session).and_return({:user_id => 1})
      logged_in?.should_not be_false
    end
    
    it "should say if 'you' are logged in" do
      you?(@user).should be_false
    end
    
    it "should say if 'you' are logged in" do
      session[:user_id] = @user.id
      you?(@user).should be_true
    end
    
    it "should display your full name" do
      @user.should_receive(:firstname).and_return("John")
      @user.should_receive(:lastname).and_return("Michael")
      full_name(@user).should == "John Michael"
    end
    
    it "should display your full name with nil strings" do
      @user.should_receive(:firstname)
      @user.should_receive(:lastname).and_return("Michael")
      full_name(@user).should == " Michael"
    end
    
    it "should display the display name if it isn't you" do
      should_receive(:'you?').and_return(false)
      @user.should_receive(:username).and_return("mcdonalD")
      display_name(@user).should == "mcdonalD"
    end
    
    it "should display 'you' as display name if you're looking at yourself" do
      should_receive(:'you?').and_return(true)
      display_name(@user).should == "You"
    end
    
    it "should create a login link" do
      login_link.should == "<a href='#login'>Login</a>"
    end
    
  end
  
  # GENERIC ACTIONS
  
  it "should generate the normal sized thumbnail when no image is specified" do
    thumbnail(nil).should == %{<div style='width: 150px; height: 150px; border: 1px solid #CCC; color: #666; text-align: center; vertical-align: middle; display: table-cell;'>No image</div>}
  end
  
  it "should generate the small sized thumbnail when no image is specified and small is given" do
    thumbnail(nil, nil, :small).should == %{<div style='width: 90px; height: 90px; border: 1px solid #CCC; color: #666; text-align: center; vertical-align: middle; display: table-cell;'>No image</div>}
  end

  it "should generate a default link if no link is specified" do
    img = mock_model(Image)
    img.should_receive(:public_filename).twice.and_return('image.jpg')
    style = "display: block; width: 150px; height: 150px; background: url(image.jpg) no-repeat center center;"
    should_receive(:link_to).with('', 'image.jpg', {:style => style})
    thumbnail(img)
  end
  
  describe "handling get_image from object" do
  
    before(:each) do
      @image = mock_model(Image)
    end
  
    it "should get image from user" do
      user = mock_model(User)
      user.should_receive(:image).and_return(@image)
      get_image(user).should == @image
    end
    
    it "should return nil from user if image is nil" do
      user = mock_model(User)
      user.should_receive(:image).and_return(nil)
      get_image(user).should == nil
    end
    
    it "should get image from event" do
      event = mock_model(Event)
      event.should_receive(:image).and_return(@image)
      get_image(event).should == @image
    end
    
    it "should return nil from event if image is nil" do
      event = mock_model(Event)
      event.should_receive(:image).and_return(nil)
      get_image(event).should == nil
    end
  
  end
  
  # EventsController#tag_cloud never even called so I'm just taking this out -Hubert
  #describe "handling links to tag cloud" do
  #
  #  it "should default to events controller" do
      # ** tag_cloud action?
  #    link_to_tag_cloud.should == link_to( 'tag cloud', {:controller => 'events', :action => 'tag_cloud'} )
  #  end
  #
  #end
  
  it "should generate tag cloud items" do
    tag1 = mock_model(Tag)
    tag1.should_receive(:'[]').with(:name).and_return('foo')
    tag1.should_receive(:'[]').with(:count).at_least(4).times.and_return(10)
    
    tag2 = mock_model(Tag)
    tag2.should_receive(:'[]').with(:name).and_return('bar')
    tag2.should_receive(:'[]').with(:count).at_least(4).times.and_return(20)
    
    should_receive(:link_to).twice do |name, url, opts|
      name.should =~ /^(foo|bar)$/
      url.should == {:controller => 'events', :action => 'tagged', :tag => name}
      size = name == 'foo' ? 2.0 : 3.0
      opts.should == {:style => "font-size: #{size}em;"}
    end
    
    tags = [tag1, tag2]
    tag_cloud_items(tags)
  end

  it "should generate tag cloud items with equal counts" do
    tag1 = mock_model(Tag)
    tag1.should_receive(:'[]').with(:name).and_return('foo')
    tag1.should_receive(:'[]').with(:count).at_least(4).times.and_return(10)
    
    tag2 = mock_model(Tag)
    tag2.should_receive(:'[]').with(:name).and_return('bar')
    tag2.should_receive(:'[]').with(:count).at_least(4).times.and_return(10)
    
    should_receive(:link_to).twice do |name, url, opts|
      name.should =~ /^(foo|bar)$/
      url.should == {:controller => 'events', :action => 'tagged', :tag => name}
      size = name == 'foo' ? 11.0 : 11.0 # Should be 11 per the formula base + count = 1 + 10 = 11  -hube
      opts.should == {:style => "font-size: #{size}em;"}
    end
    
    tags = [tag1, tag2]
    tag_cloud_items(tags)
  end
  
  describe "handling tags for taggable models" do
  
    it "should be nil if model is not taggable" do
      image = mock_model(Image)
      #image.should_receive(:tags).and_return(false)
      #tags_for(image).should == ""
    end
    
    before(:each) do
      @event = mock_model(Event)
    end
    
    it "should render if there exists tags" do
      @event.stub!(:tags).twice.and_return(["meeting"])
      stub!(:render).and_return("")
      #tags_for(@event).should == (render :partial => 'tag', :collection => @event.tags)
      tags_for(@event).should == ""
    end
    
    it "should return no tags if event has tags" do
      @event.should_receive(:tags).and_return([])
      tags_for(@event).should == "no tags <br />"
    end
  
  end

end

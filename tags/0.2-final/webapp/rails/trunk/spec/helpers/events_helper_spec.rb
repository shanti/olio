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

describe EventsHelper do
  include EventsHelper
  
  def login
    @user = mock_model(User)
    session[:user_id] = @user.id
    stub!(:'logged_in?').and_return(true)
  end
  
  describe "handling attendance links" do
    
    before(:each) do
      @event = mock_model(Event)
    end
  
    it "should have no links if not logged in" do
      should_receive(:'logged_in?').and_return(false)
      attendance_links(@event).should == "\n"
    end
    
    describe "while logged in" do
    
      before(:each) do
        login
      end
      
      it "should make an unattend link if the user is attending" do
        @event.should_receive(:users).and_return([@user])
        expected = (form_remote_tag :url => unattend_event_path(@event), :method => :post, :html => {:method => :post}) + (submit_tag "Unattend") + "</form>" + "\n"
        attendance_links(@event).should == expected
      end
      
      it "should make an attend link if the user is not attending" do
        @event.should_receive(:users).and_return([])
        expected = (form_remote_tag :url => attend_event_path(@event), :method => :post, :html => {:method => :post}) + (submit_tag "Attend") + "</form>" + "\n"
        attendance_links(@event).should == expected
      end
    
    end
    
    describe "handling edit delete links" do
    
      before(:each) do
        @event = mock_model(Event)
        # for edit_event_path
        stub!(:polymorphic_path).and_return("/#{@event.class.to_s.underscore.pluralize}/#{@event.id}/edit")
      end

      it "should have no links if not logged in" do
        should_receive(:'logged_in?').and_return(false)
        edit_delete_links(@event).should == "\n"
      end
      
      describe "while logged in" do
      
        before(:each) do
          login
        end
      
        it "should have no links if events don't include logged in user" do
          @event.should_receive(:user_id).and_return(@user.id+10)
          edit_delete_links(@event).should == "\n"
        end
        
        it "should create the links of logged in user created the event" do
          @event.should_receive(:user_id).and_return(@user.id)
          expected = (button_to 'Edit', edit_event_path(@event), :method => :get) + " " + (button_to 'Delete', @event, :confirm => 'Are you sure?', :method => :delete) + "\n"
          edit_delete_links(@event).should == expected
        end
        
      end
    
    end
    
    describe "handling created at radio button" do
    
      it "should make the created_at radio button" do
        session[:order] = "created_at"
        created_at_radio_button.should == (radio_button_tag 'order', 'created_at', true)
      end
      
      it "should not make the created_at radio button" do
        session[:order] = "asdfa"
        created_at_radio_button.should == (radio_button_tag 'order', 'created_at', false)
      end
      
      it "should handle nil orders" do
        session[:order] = nil
        created_at_radio_button.should == (radio_button_tag 'order', 'created_at', false)
      end
    
    end
    
    describe "handling event date radio button" do
    
      it "should make the event_date radio button" do
        session[:order] = "event_date"
        event_date_radio_button.should == (radio_button_tag 'order', 'event_date', true)
      end
      
      it "should not make the event_date radio button" do
        session[:order] = "asdfa"
        event_date_radio_button.should == (radio_button_tag 'order', 'event_date', false)
      end
      
      it "should handle nil orders" do
        session[:order] = nil
        event_date_radio_button.should == (radio_button_tag 'order', 'event_date', false)
      end
    
    end
    
    describe "handling zipcode filter" do
    
      it "should return zip code text field" do
        zipcode_filter(94720).should == (text_field_tag 'zipcode', 94720)
      end
      
      it "should handle string combitnations" do
        zipcode_filter("94720").should == (text_field_tag 'zipcode', 94720)
        zipcode_filter(94720).should == (text_field_tag 'zipcode', "94720")
        zipcode_filter("94720").should == (text_field_tag 'zipcode', "94720")
      end
      
      it "should handle nil inputs" do
        zipcode_filter(nil).should == (text_field_tag 'zipcode', "")
      end
    
    end
  
  end
  
end

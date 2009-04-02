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


describe 'Comment model: Comment' do
  
  before(:each) do
    @comment = Comment.new
  end
  
  it "should be valid" do
    comment = new_comment
    comment.should be_valid
  end
  
  it 'should be valid with the minimal parameters' do
    @comment.user_id = 1337
    @comment.event_id = 1337
    @comment.rating = 0
    @comment.comment = "The comment goes here."
    @comment.should be_valid
  end
  
  it "should have some text" do
    @comment.should have(1).errors_on(:comment)
  end
  
  it "should have a numeric rating" do
    @comment.should have(3).errors_on(:rating)
    @comment.rating = 'a'
    @comment.should have(1).errors_on(:rating)
    @comment.errors_on(:rating).should == ["must be an integer number"]
  end
  
  it "should belong to a user" do 
    @comment.should have(1).errors_on(:user_id)
  end
  
  it "should belong to an event" do 
    @comment.should have(1).errors_on(:event_id)
  end
  
  it "should have an integer rating between 0 and 5" do
    @comment.rating = -1
    @comment.errors_on(:rating).should == ["must be between 0 and 5"]
    @comment.rating = 6
    @comment.errors_on(:rating).should == ["must be between 0 and 5"]
    @comment.rating = 1.5
    @comment.errors_on(:rating).should == ["must be an integer number"]
    @comment.rating = 0
    @comment.should have(0).errors_on(:rating)
    @comment.rating = 1
    @comment.should have(0).errors_on(:rating)
    @comment.rating = 2
    @comment.should have(0).errors_on(:rating)
    @comment.rating = 3
    @comment.should have(0).errors_on(:rating)
    @comment.rating = 4
    @comment.should have(0).errors_on(:rating)
    @comment.rating = 5
    @comment.should have(0).errors_on(:rating)
  end
  
  it "should allow duplicate comments" do
    comment = new_comment
    @comment.comment = comment.comment
    @comment.rating = comment.rating
    @comment.user_id = comment.user_id
    @comment.event_id = comment.event_id
    comment.should be_valid
    @comment.should be_valid
  end
  
end

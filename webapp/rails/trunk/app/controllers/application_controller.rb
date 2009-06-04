#
#  Licensed to the Apache Software Foundation (ASF) under one
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
# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  
  def authorize
    begin
      @user = User.find(session[:user_id])
    rescue
      @user = nil
    end
    
    unless @user
      session[:original_uri] = request.request_uri
      flash[:error] = "You must log in before accessing that page."      
      redirect_to(root_path)
    end
  end
  
  def logged_in_as(user = nil)
    logged_in_as_user_id = false
    if !user.nil?
      if user.id == session[:user_id]
        logged_in_as_user_id = true
      end
    end
    user = nil
    logged_in_as_user_id
  end
  
  def generate_friend_cloud(all_friends)
    friends = all_friends.clone
    @friendcloud = []
    6.times do
      random_friend = rand(friends.size)
      @friendcloud << friends[random_friend] unless friends.empty?
      friends.delete_at(random_friend)
    end    
  end
    
  def validate_event
    begin
      @event = Event.find(params[:event_id])
    rescue ActiveRecord::RecordNotFound
      respond_to do |format|
        flash[:error] = "Event does not exist."
        format.html { redirect_to root_path }
      end
    end
  end
  
end

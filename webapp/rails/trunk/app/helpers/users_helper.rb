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
module UsersHelper
  
  def display_name(user)
    (you? user) ? "Your" : "#{user.username}'s"
  end

  def search_results_header
    "Search Results for \"#{h @query}\""
  end
  
  def friendship_action(source, target)
    unless source == target
      if source.friends.include?(target)
        render :partial => 'friends/remove_link', :locals => { :user => source, :target => target }
      elsif @incoming_invite_ids.include?(target.id)
        out = render :partial => 'friends/approve_link', :locals => { :user => source, :target => target }
        out += render :partial => 'friends/reject_link', :locals => { :user => source, :target => target }
      elsif @outgoing_invite_ids.include?(target.id)
        render :partial => 'friends/revoke_link', :locals => { :user => source, :target => target }
      else
        render :partial => 'friends/add_link', :locals => { :user => target }
      end
    end
  end
  
end

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
module EventsHelper
  
  def attendance_links(event)
    links = ""
    if logged_in?
      attending = @attendees.find { |u| u.id == session[:user_id] }
      if attending
        links += form_remote_tag :url => unattend_event_path(event), :method => :post, :html => {:method => :post}
        links += submit_tag "Unattend"
        links += "</form>"
      else
        links += form_remote_tag :url => attend_event_path(event), :method => :post, :html => {:method => :post}
        links += submit_tag "Attend"
        links += "</form>"
      end
      
    end
    links += "\n"
  end
  
  def edit_delete_links(event)
    links = ""
    if logged_in? and event.user_id == session[:user_id]
      links += button_to 'Edit', edit_event_path(event), :method => :get
      links += " "
      links += button_to 'Delete', event, :confirm => 'Are you sure?', :method => :delete
    end
    links += "\n"
  end
  
  def created_at_radio_button
    if session[:order] == 'created_at'
      radio_button_tag 'order', 'created_at', true
    else
      radio_button_tag 'order', 'created_at', false
    end
  end
  
  def event_date_radio_button
    if session[:order] == 'event_date'
      radio_button_tag 'order', 'event_date', true
    else
      radio_button_tag 'order', 'event_date', false
    end
  end
  
  def zipcode_filter(zip)
    text_field_tag 'zipcode', "#{zip}"
  end
  
end

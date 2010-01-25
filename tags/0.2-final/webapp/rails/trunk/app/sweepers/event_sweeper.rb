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
class EventSweeper < ActionController::Caching::Sweeper

  observe Event, Comment
  
  def after_create(record)
    expire_record(record)
  end

  def after_save(record)
    expire_record(record)
  end

  def after_destroy(record)
    expire_record(record)
  end
  
  private ############################################

  def expire_record(record)
    unless session.nil?
      expire_fragment(:controller => 'events', :action => 'index', :part => 'tag_cloud')
    end
    
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "event_description")
    
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "main_event_details")
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "main_event_details", :creator => true)
    
    expire_page(root_path)
  end

end


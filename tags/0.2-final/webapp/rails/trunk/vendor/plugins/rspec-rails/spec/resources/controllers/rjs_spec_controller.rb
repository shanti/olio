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
class RjsSpecController < ApplicationController
  set_view_path File.join(File.dirname(__FILE__), "..", "views")
  
  def replace_html
  end
  
  def insert_html
  end
  
  def replace
  end
  
  def hide_div
  end
  
  def hide_page_element
  end

  def replace_html_with_partial
  end

  def render_replace_html
    render :update do |page|
      page.replace_html 'mydiv', 'replacement text'
      page.replace_html 'myotherdiv', 'other replacement text'
    end
  end
  
  def render_replace_html_with_partial
    render :update do |page|
      page.replace_html 'mydiv', :partial => 'rjs_spec/replacement_partial'
    end
  end
  
  def render_insert_html
    render :update do |page|
      page.insert_html 'mydiv', 'replacement text'
    end
  end
  
  def render_replace
    render :update do |page|
      page.replace 'mydiv', 'replacement text'
    end
  end
  
  def render_hide_div
    render :update do |page|
      page.hide 'mydiv'
    end
  end
  
  def render_hide_page_element
    render :update do |page|
      page['mydiv'].hide
    end
  end
end

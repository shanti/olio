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
require File.dirname(__FILE__) + '/../../spec_helper'

class DescriptionGenerationSpecController < ActionController::Base
  def render_action
  end

  def redirect_action
    redirect_to :action => :render_action
  end
end

describe "Description generation", :type => :controller do
  controller_name :description_generation_spec
  
  after(:each) do
    Spec::Matchers.clear_generated_description
  end

  it "should generate description for render_template" do
    get 'render_action'
    response.should render_template("render_action")
    Spec::Matchers.generated_description.should == "should render template \"render_action\""
  end

  it "should generate description for render_template with full path" do
    get 'render_action'
    response.should render_template("description_generation_spec/render_action")
    Spec::Matchers.generated_description.should == "should render template \"description_generation_spec/render_action\""
  end

  it "should generate description for redirect_to" do
    get 'redirect_action'
    response.should redirect_to("http://test.host/description_generation_spec/render_action")
    Spec::Matchers.generated_description.should == "should redirect to \"http://test.host/description_generation_spec/render_action\""
  end

end

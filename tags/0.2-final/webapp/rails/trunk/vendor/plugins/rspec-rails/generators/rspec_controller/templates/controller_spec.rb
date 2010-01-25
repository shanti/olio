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
require File.expand_path(File.dirname(__FILE__) + '<%= '/..' * class_nesting_depth %>/../spec_helper')

describe <%= class_name %>Controller do

<% if actions.empty? -%>
  #Delete this example and add some real ones
<% else -%>
  #Delete these examples and add some real ones
<% end -%>
  it "should use <%= class_name %>Controller" do
    controller.should be_an_instance_of(<%= class_name %>Controller)
  end

<% unless actions.empty? -%>
<% for action in actions -%>

  describe "GET '<%= action %>'" do
    it "should be successful" do
      get '<%= action %>'
      response.should be_success
    end
  end
<% end -%>
<% end -%>
end

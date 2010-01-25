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
require File.join(File.dirname(__FILE__), *%w[.. .. .. lib autotest rails_rspec])
require File.join(File.dirname(__FILE__), *%w[.. .. .. .. rspec spec autotest autotest_matchers])

describe Autotest::RailsRspec, "file mapping" do
  before(:each) do
    @autotest = Autotest::RailsRspec.new
    @autotest.hook :initialize
  end
  
  it "should map model example to model" do
    @autotest.should map_specs(['spec/models/thing_spec.rb']).
                            to('app/models/thing.rb')
  end
  
  it "should map controller example to controller" do
    @autotest.should map_specs(['spec/controllers/things_controller_spec.rb']).
                            to('app/controllers/things_controller.rb')
  end
  
  it "should map view.rhtml" do
    @autotest.should map_specs(['spec/views/things/index.rhtml_spec.rb']).
                            to('app/views/things/index.rhtml')
  end
  
  it "should map view.rhtml with underscores in example filename" do
    @autotest.should map_specs(['spec/views/things/index_rhtml_spec.rb']).
                            to('app/views/things/index.rhtml')
  end
  
  it "should map view.html.erb" do
    @autotest.should map_specs(['spec/views/things/index.html.erb_spec.rb']).
                            to('app/views/things/index.html.erb')
  end
  
end

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
describe Spec::Adapters::RubyEngine do
  it "should default to MRI" do
    Spec::Adapters::RubyEngine.adapter.should be_an_instance_of(Spec::Adapters::RubyEngine::MRI)
  end
  
  it "should provide Rubinius for rbx" do
    Spec::Adapters::RubyEngine.stub!(:engine).and_return('rbx')
    Spec::Adapters::RubyEngine.adapter.should be_an_instance_of(Spec::Adapters::RubyEngine::Rubinius)
  end
  
  it "should try to find whatever is defined by the RUBY_ENGINE const" do
    Object.stub!(:const_defined?).with('RUBY_ENGINE').and_return(true)
    Object.stub!(:const_get).with('RUBY_ENGINE').and_return("xyz")
    Spec::Adapters::RubyEngine.engine.should == "xyz"
  end
end
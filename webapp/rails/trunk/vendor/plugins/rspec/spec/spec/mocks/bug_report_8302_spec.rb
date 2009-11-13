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
require File.dirname(__FILE__) + '/../../spec_helper.rb'

module Bug8302
  class Foo
    def Foo.class_method(arg)
    end
  
    def instance_bar(arg)
    end
  end

  describe "Bug report 8302:" do
    it "class method is not restored correctly when proxied" do
      Foo.should_not_receive(:class_method).with(Array.new)
      Foo.rspec_verify
      Foo.class_method(Array.new)
    end

    it "instance method is not restored correctly when proxied" do
      foo = Foo.new
      foo.should_not_receive(:instance_bar).with(Array.new)
      foo.rspec_verify
      foo.instance_bar(Array.new)
    end
  end
end

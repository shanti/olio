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

module Spec
  module Runner
    describe ClassAndArgumentsParser, ".parse" do
      
      it "should use a single : to separate class names from arguments" do
        ClassAndArgumentsParser.parse('Foo').should == ['Foo', nil]
        ClassAndArgumentsParser.parse('Foo:arg').should == ['Foo', 'arg']
        ClassAndArgumentsParser.parse('Foo::Bar::Zap:arg').should == ['Foo::Bar::Zap', 'arg']
        ClassAndArgumentsParser.parse('Foo:arg1,arg2').should == ['Foo', 'arg1,arg2']
        ClassAndArgumentsParser.parse('Foo::Bar::Zap:arg1,arg2').should == ['Foo::Bar::Zap', 'arg1,arg2']
        ClassAndArgumentsParser.parse('Foo::Bar::Zap:drb://foo,drb://bar').should == ['Foo::Bar::Zap', 'drb://foo,drb://bar']
      end

      it "should raise an error when passed an empty string" do
        lambda do
          ClassAndArgumentsParser.parse('')
        end.should raise_error("Couldn't parse \"\"")
      end      
    end
  end
end

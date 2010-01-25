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
module Spec
  module Mocks
    module ExampleMethods
      include Spec::Mocks::ArgumentConstraints

      # Shortcut for creating an instance of Spec::Mocks::Mock.
      #
      # +name+ is used for failure reporting, so you should use the
      # role that the mock is playing in the example.
      #
      # +stubs_and_options+ lets you assign options and stub values
      # at the same time. The only option available is :null_object.
      # Anything else is treated as a stub value.
      #
      # == Examples
      #
      #   stub_thing = mock("thing", :a => "A")
      #   stub_thing.a == "A" => true
      #
      #   stub_person = stub("thing", :name => "Joe", :email => "joe@domain.com")
      #   stub_person.name => "Joe"
      #   stub_person.email => "joe@domain.com"
      def mock(name, stubs_and_options={})
        Spec::Mocks::Mock.new(name, stubs_and_options)
      end
      
      alias :stub :mock

      # Shortcut for creating a mock object that will return itself in response
      # to any message it receives that it hasn't been explicitly instructed
      # to respond to.
      def stub_everything(name = 'stub')
        mock(name, :null_object => true)
      end
      
      # Disables warning messages about expectations being set on nil.
      #
      # By default warning messages are issued when expectations are set on nil.  This is to 
      # prevent false-positives and to catch potential bugs early on.
      def allow_message_expectations_on_nil
        Proxy.allow_message_expectations_on_nil
      end

    end
  end
end

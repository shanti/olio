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
  module Example
    # Base class for customized example groups. Use this if you
    # want to make a custom example group.
    class ExampleGroup
      extend Spec::Example::ExampleGroupMethods
      include Spec::Example::ExampleMethods

      def initialize(defined_description, options={}, &implementation)
        @_options = options
        @_defined_description = defined_description
        @_implementation = implementation || pending_implementation
        @_backtrace = caller
      end
      
    private
      
      def pending_implementation
        error = NotYetImplementedError.new(caller)
        lambda { raise(error) }
      end
    end
  end
end

Spec::ExampleGroup = Spec::Example::ExampleGroup

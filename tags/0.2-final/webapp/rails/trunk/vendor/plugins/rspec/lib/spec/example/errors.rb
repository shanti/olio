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
    class ExamplePendingError < StandardError
      attr_reader :pending_caller

      def initialize(message=nil)
        super
        @pending_caller = caller[2]
      end
    end
    
    class NotYetImplementedError < ExamplePendingError
      MESSAGE = "Not Yet Implemented"
      RSPEC_ROOT_LIB = File.expand_path(File.dirname(__FILE__) + "/../..")
      
      def initialize(backtrace)
        super(MESSAGE)
        @pending_caller = pending_caller_from(backtrace)
      end
      
    private
      
      def pending_caller_from(backtrace)
        backtrace.detect {|line| !line.include?(RSPEC_ROOT_LIB) }
      end
    end

    class PendingExampleFixedError < StandardError; end
  end
end

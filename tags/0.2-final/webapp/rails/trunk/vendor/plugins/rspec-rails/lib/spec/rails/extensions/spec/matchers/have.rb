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
require 'spec/matchers/have'

module Spec #:nodoc:
  module Matchers #:nodoc:
    class Have #:nodoc:
      alias_method :__original_failure_message, :failure_message
      def failure_message
        return "expected #{relativities[@relativity]}#{@expected} errors on :#{@args[0]}, got #{@given}" if @collection_name == :errors_on
        return "expected #{relativities[@relativity]}#{@expected} error on :#{@args[0]}, got #{@given}" if @collection_name == :error_on
        return __original_failure_message
      end
      
      alias_method :__original_description, :description
      def description
        return "should have #{relativities[@relativity]}#{@expected} errors on :#{@args[0]}" if @collection_name == :errors_on
        return "should have #{relativities[@relativity]}#{@expected} error on :#{@args[0]}" if @collection_name == :error_on
        return __original_description
      end
    end
  end
end

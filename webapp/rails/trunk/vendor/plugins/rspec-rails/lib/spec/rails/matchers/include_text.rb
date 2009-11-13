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
  module Rails
    module Matchers

      class IncludeText  #:nodoc:

        def initialize(expected)
          @expected = expected
        end

        def matches?(response_or_text)
          @actual = response_or_text.respond_to?(:body) ? response_or_text.body : response_or_text
          return actual.include?(expected)
        end

        def failure_message
          "expected to find #{expected.inspect} in #{actual.inspect}"
        end

        def negative_failure_message
          "expected not to include text #{expected.inspect}"
        end

        def to_s
          "include text #{expected.inspect}"
        end

        private
          attr_reader :expected
          attr_reader :actual

      end


      # :call-seq:
      #   response.should include_text(expected)
      #   response.should_not include_text(expected)
      #
      # Accepts a String, matching using include?
      #
      # Use this instead of <tt>response.should have_text()</tt>
      # when you either don't know or don't care where on the page
      # this text appears.
      #
      # == Examples
      #
      #   response.should include_text("This text will be in the actual string")
      def include_text(text)
        IncludeText.new(text)
      end

    end
  end
end
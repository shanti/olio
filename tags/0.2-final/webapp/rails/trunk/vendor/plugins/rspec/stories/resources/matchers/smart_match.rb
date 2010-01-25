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
  module Matchers
    class SmartMatch
      def initialize(expected)
        @expected = expected
      end

      def matches?(actual)
        @actual = actual
        # Satisfy expectation here. Return false or raise an error if it's not met.

        if @expected =~ /^\/.*\/?$/ || @expected =~ /^".*"$/
          regex_or_string = eval(@expected)
          if Regexp === regex_or_string
            (@actual =~ regex_or_string) ? true : false
          else
            @actual.index(regex_or_string) != nil
          end
        else
          false
        end
      end

      def failure_message
        "expected #{@actual.inspect} to smart_match #{@expected.inspect}, but it didn't"
      end

      def negative_failure_message
        "expected #{@actual.inspect} not to smart_match #{@expected.inspect}, but it did"
      end
    end

    def smart_match(expected)
      SmartMatch.new(expected)
    end
  end
end
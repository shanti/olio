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
  module Expectations
    class InvalidMatcherError < ArgumentError; end        
    
    class ExpectationMatcherHandler        
      def self.handle_matcher(actual, matcher, &block)
        ::Spec::Matchers.last_should = "should"
        return Spec::Matchers::PositiveOperatorMatcher.new(actual) if matcher.nil?

        unless matcher.respond_to?(:matches?)
          raise InvalidMatcherError, "Expected a matcher, got #{matcher.inspect}."
        end
        
        match = matcher.matches?(actual, &block)
        ::Spec::Matchers.last_matcher = matcher
        Spec::Expectations.fail_with(matcher.failure_message) unless match
        match
      end
    end

    class NegativeExpectationMatcherHandler
      def self.handle_matcher(actual, matcher, &block)
        ::Spec::Matchers.last_should = "should not"
        return Spec::Matchers::NegativeOperatorMatcher.new(actual) if matcher.nil?
        
        unless matcher.respond_to?(:matches?)
          raise InvalidMatcherError, "Expected a matcher, got #{matcher.inspect}."
        end

        unless matcher.respond_to?(:negative_failure_message)
          Spec::Expectations.fail_with(
<<-EOF
Matcher does not support should_not.
See Spec::Matchers for more information
about matchers.
EOF
)
        end
        match = matcher.matches?(actual, &block)
        ::Spec::Matchers.last_matcher = matcher
        Spec::Expectations.fail_with(matcher.negative_failure_message) if match
        match
      end
    end
  end
end


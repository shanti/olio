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

    class OperatorMatcher
      @operator_registry = {}

      def self.register(klass, operator, matcher)
        @operator_registry[klass] ||= {}
        @operator_registry[klass][operator] = matcher
      end

      def self.get(klass, operator)
        return @operator_registry[klass][operator] if @operator_registry[klass]
        nil
      end

      def initialize(actual)
        @actual = actual
      end

      def self.use_custom_matcher_or_delegate(operator)
        define_method(operator) do |expected|
          if matcher = OperatorMatcher.get(@actual.class, operator)
            return @actual.send(matcher_method, matcher.new(expected))
          else
            ::Spec::Matchers.last_matcher = self
            @operator, @expected = operator, expected
            __delegate_operator(@actual, operator, expected)
          end
        end
      end

      ['==', '===', '=~', '>', '>=', '<', '<='].each do |operator|
        use_custom_matcher_or_delegate operator
      end

      def fail_with_message(message)
        Spec::Expectations.fail_with(message, @expected, @actual)
      end

      def description
        "#{@operator} #{@expected.inspect}"
      end

    end

    class PositiveOperatorMatcher < OperatorMatcher #:nodoc:
      def matcher_method
        :should
      end

      def __delegate_operator(actual, operator, expected)
        return true if actual.__send__(operator, expected)
        if ['==','===', '=~'].include?(operator)
          fail_with_message("expected: #{expected.inspect},\n     got: #{actual.inspect} (using #{operator})") 
        else
          fail_with_message("expected: #{operator} #{expected.inspect},\n     got: #{operator.gsub(/./, ' ')} #{actual.inspect}")
        end
      end

    end

    class NegativeOperatorMatcher < OperatorMatcher #:nodoc:
      def matcher_method
        :should_not
      end

      def __delegate_operator(actual, operator, expected)
        return true unless actual.__send__(operator, expected)
        return fail_with_message("expected not: #{operator} #{expected.inspect},\n         got: #{operator.gsub(/./, ' ')} #{actual.inspect}")
      end

    end

  end
end

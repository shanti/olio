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
    # rspec adds #should and #should_not to every Object (and,
    # implicitly, every Class).
    module ObjectExpectations
      # :call-seq:
      #   should(matcher)
      #   should == expected
      #   should === expected
      #   should =~ expected
      #
      #   receiver.should(matcher)
      #     => Passes if matcher.matches?(receiver)
      #
      #   receiver.should == expected #any value
      #     => Passes if (receiver == expected)
      #
      #   receiver.should === expected #any value
      #     => Passes if (receiver === expected)
      #
      #   receiver.should =~ regexp
      #     => Passes if (receiver =~ regexp)
      #
      # See Spec::Matchers for more information about matchers
      #
      # == Warning
      #
      # NOTE that this does NOT support receiver.should != expected.
      # Instead, use receiver.should_not == expected
      def should(matcher=nil, &block)
        ExpectationMatcherHandler.handle_matcher(self, matcher, &block)
      end

      # :call-seq:
      #   should_not(matcher)
      #   should_not == expected
      #   should_not === expected
      #   should_not =~ expected
      #
      #   receiver.should_not(matcher)
      #     => Passes unless matcher.matches?(receiver)
      #
      #   receiver.should_not == expected
      #     => Passes unless (receiver == expected)
      #
      #   receiver.should_not === expected
      #     => Passes unless (receiver === expected)
      #
      #   receiver.should_not =~ regexp
      #     => Passes unless (receiver =~ regexp)
      #
      # See Spec::Matchers for more information about matchers
      def should_not(matcher=nil, &block)
        NegativeExpectationMatcherHandler.handle_matcher(self, matcher, &block)
      end

    end
  end
end

class Object
  include Spec::Expectations::ObjectExpectations
end

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
    
    class Satisfy #:nodoc:
      def initialize(&block)
        @block = block
      end
      
      def matches?(given, &block)
        @block = block if block
        @given = given
        @block.call(given)
      end
      
      def failure_message
        "expected #{@given} to satisfy block"
      end

      def negative_failure_message
        "expected #{@given} not to satisfy block"
      end
    end
    
    # :call-seq:
    #   should satisfy {}
    #   should_not satisfy {}
    #
    # Passes if the submitted block returns true. Yields target to the
    # block.
    #
    # Generally speaking, this should be thought of as a last resort when
    # you can't find any other way to specify the behaviour you wish to
    # specify.
    #
    # If you do find yourself in such a situation, you could always write
    # a custom matcher, which would likely make your specs more expressive.
    #
    # == Examples
    #
    #   5.should satisfy { |n|
    #     n > 3
    #   }
    def satisfy(&block)
      Matchers::Satisfy.new(&block)
    end
  end
end

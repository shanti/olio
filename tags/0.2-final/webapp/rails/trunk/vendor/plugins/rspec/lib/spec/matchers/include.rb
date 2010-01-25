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

    class Include #:nodoc:
      
      def initialize(*expecteds)
        @expecteds = expecteds
      end
      
      def matches?(actual)
        @actual = actual
        @expecteds.each do |expected|
          if actual.is_a?(Hash)
            if expected.is_a?(Hash)
              expected.each_pair do |k,v|
                return false unless actual[k] == v
              end
            else
              return false unless actual.has_key?(expected)
            end
          else
            return false unless actual.include?(expected)
          end
        end
        true
      end
      
      def failure_message
        _message
      end
      
      def negative_failure_message
        _message("not ")
      end
      
      def description
        "include #{_pretty_print(@expecteds)}"
      end
      
      private
        def _message(maybe_not="")
          "expected #{@actual.inspect} #{maybe_not}to include #{_pretty_print(@expecteds)}"
        end
        
        def _pretty_print(array)
          result = ""
          array.each_with_index do |item, index|
            if index < (array.length - 2)
              result << "#{item.inspect}, "
            elsif index < (array.length - 1)
              result << "#{item.inspect} and "
            else
              result << "#{item.inspect}"
            end
          end
          result
        end
    end

    # :call-seq:
    #   should include(expected)
    #   should_not include(expected)
    #
    # Passes if actual includes expected. This works for
    # collections and Strings. You can also pass in multiple args
    # and it will only pass if all args are found in collection.
    #
    # == Examples
    #
    #   [1,2,3].should include(3)
    #   [1,2,3].should include(2,3) #would pass
    #   [1,2,3].should include(2,3,4) #would fail
    #   [1,2,3].should_not include(4)
    #   "spread".should include("read")
    #   "spread".should_not include("red")
    def include(*expected)
      Matchers::Include.new(*expected)
    end
  end
end

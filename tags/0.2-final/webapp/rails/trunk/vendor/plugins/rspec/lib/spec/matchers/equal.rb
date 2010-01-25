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
  
    # :call-seq:
    #   should equal(expected)
    #   should_not equal(expected)
    #
    # Passes if given and expected are the same object (object identity).
    #
    # See http://www.ruby-doc.org/core/classes/Object.html#M001057 for more information about equality in Ruby.
    #
    # == Examples
    #
    #   5.should equal(5) #Fixnums are equal
    #   "5".should_not equal("5") #Strings that look the same are not the same object
    def equal(expected)
      simple_matcher do |actual, matcher|
        matcher.failure_message          = "expected #{expected.inspect}, got #{actual.inspect} (using .equal?)", expected, actual
        matcher.negative_failure_message = "expected #{actual.inspect} not to equal #{expected.inspect} (using .equal?)", expected, actual
        matcher.description              = "equal #{expected.inspect}"
        actual.equal?(expected)
      end
    end
  end
end

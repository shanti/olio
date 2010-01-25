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
    #   should match(regexp)
    #   should_not match(regexp)
    #
    # Given a Regexp, passes if actual =~ regexp
    #
    # == Examples
    #
    #   email.should match(/^([^\s]+)((?:[-a-z0-9]+\.)+[a-z]{2,})$/i)
    def match(regexp)
      simple_matcher do |actual, matcher|
        matcher.failure_message          = "expected #{actual.inspect} to match #{regexp.inspect}", regexp, actual
        matcher.negative_failure_message = "expected #{actual.inspect} not to match #{regexp.inspect}", regexp, actual
        matcher.description              = "match #{regexp.inspect}"
        actual =~ regexp
      end
    end
  end
end

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
    def self.last_matcher
      @last_matcher
    end

    def self.last_matcher=(last_matcher)
      @last_matcher = last_matcher
    end

    def self.last_should
      @last_should
    end

    def self.last_should=(last_should)
      @last_should = last_should
    end

    def self.clear_generated_description
      self.last_matcher = nil
      self.last_should = nil
    end

    def self.generated_description
      return nil if last_should.nil?
      "#{last_should} #{last_description}"
    end
    
    private
    
    def self.last_description
      last_matcher.respond_to?(:description) ? last_matcher.description : <<-MESSAGE
When you call a matcher in an example without a String, like this:

specify { object.should matcher }

or this:

it { should matcher }

the runner expects the matcher to have a #describe method. You should either
add a String to the example this matcher is being used in, or give it a
description method. Then you won't have to suffer this lengthy warning again.
MESSAGE
    end
  end
end
      
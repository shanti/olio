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
    def has(sym, *args) # :nodoc:
      simple_matcher do |actual, matcher|
        matcher.failure_message          = "expected ##{predicate(sym)}(#{args[0].inspect}) to return true, got false"
        matcher.negative_failure_message = "expected ##{predicate(sym)}(#{args[0].inspect}) to return false, got true"
        matcher.description              = "have key #{args[0].inspect}"
        actual.__send__(predicate(sym), *args)
      end
    end
    
  private
    def predicate(sym)
      "#{sym.to_s.sub("have_","has_")}?".to_sym
    end

  end
end

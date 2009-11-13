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
  module Rails
    module Example
      class AssignsHashProxy #:nodoc:
        def initialize(example_group, &block)
          @target = block.call
          @example_group = example_group
        end

        def [](key)
          return false if assigns[key] == false
          return false if assigns[key.to_s] == false
          assigns[key] || assigns[key.to_s] || @target.instance_variable_get("@#{key}")
        end

        def []=(key, val)
          @target.instance_variable_set("@#{key}", val)
        end

        def delete(key)
          assigns.delete(key.to_s)
          @target.instance_variable_set("@#{key}", nil)
        end

        def each(&block)
          assigns.each &block
        end

        def has_key?(key)
          assigns.key?(key.to_s)
        end

        protected
        def assigns
          @example_group.orig_assigns
        end
      end
    end
  end
end

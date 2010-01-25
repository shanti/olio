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
  module Mocks
    module ArgumentConstraintMatchers
      
      # Shortcut for creating an instance of Spec::Mocks::DuckTypeArgConstraint
      def duck_type(*args)
        DuckTypeArgConstraint.new(*args)
      end

      def any_args
        AnyArgsConstraint.new
      end
      
      def anything
        AnyArgConstraint.new(nil)
      end
      
      def boolean
        BooleanArgConstraint.new(nil)
      end
      
      def hash_including(expected={})
        HashIncludingConstraint.new(expected)
      end
      
      def no_args
        NoArgsConstraint.new
      end
    end
  end
end

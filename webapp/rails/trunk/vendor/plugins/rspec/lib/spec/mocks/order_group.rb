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
    class OrderGroup
      def initialize error_generator
        @error_generator = error_generator
        @ordering = Array.new
      end
      
      def register(expectation)
        @ordering << expectation
      end
      
      def ready_for?(expectation)
        return @ordering.first == expectation
      end
      
      def consume
        @ordering.shift
      end
      
      def handle_order_constraint expectation
        return unless @ordering.include? expectation
        return consume if ready_for?(expectation)
        @error_generator.raise_out_of_order_error expectation.sym
      end
      
    end
  end
end

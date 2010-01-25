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
    class Space
      def add(obj)
        mocks << obj unless mocks.detect {|m| m.equal? obj}
      end

      def verify_all
        mocks.each do |mock|
          mock.rspec_verify
        end
      end
      
      def reset_all
        mocks.each do |mock|
          mock.rspec_reset
        end
        mocks.clear
      end
      
    private
    
      def mocks
        @mocks ||= []
      end
    end
  end
end

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
    class AutotestMappingMatcher
      def initialize(specs)
        @specs = specs
      end
  
      def to(file)
        @file = file
        self
      end
  
      def matches?(autotest)
        @autotest = prepare autotest
        @actual = autotest.test_files_for(@file)
        @actual == @specs
      end
  
      def failure_message
        "expected #{@autotest.class} to map #{@specs.inspect} to #{@file.inspect}\ngot #{@actual.inspect}"
      end
  
      private
      def prepare autotest
        stub_found_files autotest
        stub_find_order autotest
        autotest
      end
  
      def stub_found_files autotest
        found_files = @specs.inject({}){|h,f| h[f] = Time.at(0)}
        autotest.stub!(:find_files).and_return(found_files)
      end

      def stub_find_order autotest
        find_order = @specs.dup << @file
        autotest.instance_eval { @find_order = find_order }
      end

    end
    
    def map_specs(specs)
      AutotestMappingMatcher.new(specs)
    end
    
  end
end
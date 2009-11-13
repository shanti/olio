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
module Test
  module Unit
    class TestSuiteAdapter < TestSuite
      attr_reader :example_group, :examples
      alias_method :tests, :examples
      def initialize(example_group)
        @example_group = example_group
        @examples = example_group.examples
      end

      def name
        example_group.description
      end

      def run(*args)
        return true unless args.empty?
        example_group.run
      end

      def size
        example_group.number_of_examples
      end

      def delete(example)
        examples.delete example
      end

      def empty?
        examples.empty?
      end
    end
  end
end


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
  module Runner
    class ExampleGroupRunner
      def initialize(options)
        @options = options
      end

      def load_files(files)
        # It's important that loading files (or choosing not to) stays the
        # responsibility of the ExampleGroupRunner. Some implementations (like)
        # the one using DRb may choose *not* to load files, but instead tell
        # someone else to do it over the wire.
        files.each do |file|
          load file
        end
      end

      def run
        prepare
        success = true
        example_groups.each do |example_group|
          success = success & example_group.run
        end
        return success
      ensure
        finish
      end

      protected
      def prepare
        reporter.start(number_of_examples)
        example_groups.reverse! if reverse
      end

      def finish
        reporter.end
        reporter.dump
      end

      def reporter
        @options.reporter
      end

      def reverse
        @options.reverse
      end

      def example_groups
        @options.example_groups
      end

      def number_of_examples
        @options.number_of_examples
      end
    end
    # TODO: BT - Deprecate BehaviourRunner?
    BehaviourRunner = ExampleGroupRunner
  end
end
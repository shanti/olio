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
require 'spec/runner/formatter/base_text_formatter'

module Spec
  module Runner
    module Formatter
      class NestedTextFormatter < BaseTextFormatter
        attr_reader :previous_nested_example_groups
        def initialize(options, where)
          super
          @previous_nested_example_groups = []
        end

        def add_example_group(example_group)
          super

          current_nested_example_groups = described_example_group_chain
          current_nested_example_groups.each_with_index do |nested_example_group, i|
            unless nested_example_group == previous_nested_example_groups[i]
              output.puts "#{'  ' * i}#{nested_example_group.description_args}"
            end
          end

          @previous_nested_example_groups = described_example_group_chain
        end

        def example_failed(example, counter, failure)
          message = if failure.expectation_not_met?
            "#{current_indentation}#{example.description} (FAILED - #{counter})"
          else
            "#{current_indentation}#{example.description} (ERROR - #{counter})"
          end

          output.puts(failure.expectation_not_met? ? red(message) : magenta(message))
          output.flush
        end

        def example_passed(example)
          message = "#{current_indentation}#{example.description}"
          output.puts green(message)
          output.flush
        end

        def example_pending(example, message, pending_caller)
          super
          output.puts yellow("#{current_indentation}#{example.description} (PENDING: #{message})")
          output.flush
        end

        def current_indentation
          '  ' * previous_nested_example_groups.length
        end

        def described_example_group_chain
          example_group_chain = []
          example_group.__send__(:each_ancestor_example_group_class) do |example_group_class|
            unless example_group_class.description_args.empty?
              example_group_chain << example_group_class
            end
          end
          example_group_chain
        end
      end
    end
  end
end

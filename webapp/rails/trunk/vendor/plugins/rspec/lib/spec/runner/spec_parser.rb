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
    # Parses a spec file and finds the nearest example for a given line number.
    class SpecParser
      attr_reader :best_match

      def initialize
        @best_match = {}
      end

      def spec_name_for(file, line_number)
        best_match.clear
        file = File.expand_path(file)
        Spec::Runner.options.example_groups.each do |example_group|
          consider_example_group_for_best_match example_group, file, line_number

          example_group.examples.each do |example|
            consider_example_for_best_match example, example_group, file, line_number
          end
        end
        if best_match[:example_group]
          if best_match[:example]
            "#{best_match[:example_group].description} #{best_match[:example].description}"
          else
            best_match[:example_group].description
          end
        else
          nil
        end
      end

    protected

      def consider_example_group_for_best_match(example_group, file, line_number)
        parsed_backtrace = parse_backtrace(example_group.backtrace)
        parsed_backtrace.each do |example_file, example_line|
          if is_best_match?(file, line_number, example_file, example_line)
            best_match.clear
            best_match[:example_group] = example_group
            best_match[:line] = example_line
          end
        end
      end

      def consider_example_for_best_match(example, example_group, file, line_number)
        parsed_backtrace = parse_backtrace(example.backtrace)
        parsed_backtrace.each do |example_file, example_line|
          if is_best_match?(file, line_number, example_file, example_line)
            best_match.clear
            best_match[:example_group] = example_group
            best_match[:example] = example
            best_match[:line] = example_line
          end
        end
      end

      def is_best_match?(file, line_number, example_file, example_line)
        file == File.expand_path(example_file) &&
        example_line <= line_number &&
        example_line > best_match[:line].to_i
      end

      def parse_backtrace(backtrace)
        Array(backtrace).collect do |trace_line|
          trace_line =~ /(.*)\:(\d*)(\:|$)/
          file, number = $1, $2
          [file, Integer(number)]
        end
      end
    end
  end
end

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
      class FailingExampleGroupsFormatter < BaseTextFormatter
        def example_failed(example, counter, failure)
          if @example_group
            description_parts = @example_group.description_parts.collect do |description|
              description =~ /(.*) \(druby.*\)$/ ? $1 : description
            end
            @output.puts ::Spec::Example::ExampleGroupMethods.description_text(*description_parts)

            @output.flush
            @example_group = nil
          end
        end

        def dump_failure(counter, failure)
        end

        def dump_summary(duration, example_count, failure_count, pending_count)
        end
      end
    end
  end
end

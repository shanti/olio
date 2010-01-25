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
      class SpecdocFormatter < BaseTextFormatter
        def add_example_group(example_group)
          super
          output.puts
          output.puts example_group.description
          output.flush
        end
      
        def example_failed(example, counter, failure)
          message = if failure.expectation_not_met?
            "- #{example.description} (FAILED - #{counter})"
          else
            "- #{example.description} (ERROR - #{counter})"
          end
          
          output.puts(failure.expectation_not_met? ? red(message) : magenta(message))
          output.flush
        end
        
        def example_passed(example)
          message = "- #{example.description}"
          output.puts green(message)
          output.flush
        end
        
        def example_pending(example, message, pending_caller)
          super
          output.puts yellow("- #{example.description} (PENDING: #{message})")
          output.flush
        end
      end
    end
  end
end

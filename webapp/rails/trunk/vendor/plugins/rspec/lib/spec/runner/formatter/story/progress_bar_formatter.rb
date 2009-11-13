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
require 'spec/runner/formatter/story/plain_text_formatter'

module Spec
  module Runner
    module Formatter
      module Story
        class ProgressBarFormatter < PlainTextFormatter

          def story_started(title, narrative) end
          def story_ended(title, narrative) end

          def run_started(count)
            @start_time = Time.now
            super
          end
          
          def run_ended
            @output.puts
            @output.puts
            @output.puts "Finished in %f seconds" % (Time.now - @start_time)
            @output.puts
            super
          end

          def scenario_ended
            if @scenario_failed
              @output.print red('F')
              @output.flush
            elsif @scenario_pending
              @output.print yellow('P')
              @output.flush
            else
              @output.print green('.')
              @output.flush
            end
          end

        end
      end
    end
  end
end

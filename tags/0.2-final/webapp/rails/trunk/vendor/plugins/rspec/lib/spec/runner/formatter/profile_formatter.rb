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
require 'spec/runner/formatter/progress_bar_formatter'

module Spec
  module Runner
    module Formatter
      class ProfileFormatter < ProgressBarFormatter
        
        def initialize(options, where)
          super
          @example_times = []
        end
        
        def start(count)
          @output.puts "Profiling enabled."
        end
        
        def example_started(example)
          @time = Time.now
        end
        
        def example_passed(example)
          super
          @example_times << [
            example_group.description,
            example.description,
            Time.now - @time
          ]
        end
        
        def start_dump
          super
          @output.puts "\n\nTop 10 slowest examples:\n"
          
          @example_times = @example_times.sort_by do |description, example, time|
            time
          end.reverse
          
          @example_times[0..9].each do |description, example, time|
            @output.print red(sprintf("%.7f", time))
            @output.puts " #{description} #{example}"
          end
          @output.flush
        end
        
        def method_missing(sym, *args)
          # ignore
        end
      end
    end
  end
end

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
    class BacktraceTweaker
      def clean_up_double_slashes(line)
        line.gsub!('//','/')
      end
    end

    class NoisyBacktraceTweaker < BacktraceTweaker
      def tweak_backtrace(error)
        return if error.backtrace.nil?
        error.backtrace.each do |line|
          clean_up_double_slashes(line)
        end
      end
    end

    # Tweaks raised Exceptions to mask noisy (unneeded) parts of the backtrace
    class QuietBacktraceTweaker < BacktraceTweaker
      unless defined?(IGNORE_PATTERNS)
        root_dir = File.expand_path(File.join(__FILE__, '..', '..', '..', '..'))
        spec_files = Dir["#{root_dir}/lib/*"].map do |path| 
          subpath = path[root_dir.length..-1]
          /#{subpath}/
        end
        IGNORE_PATTERNS = spec_files + [
          /\/lib\/ruby\//,
          /bin\/spec:/,
          /bin\/rcov:/,
          /lib\/rspec-rails/,
          /vendor\/rails/,
          # TextMate's Ruby and RSpec plugins
          /Ruby\.tmbundle\/Support\/tmruby.rb:/,
          /RSpec\.tmbundle\/Support\/lib/,
          /temp_textmate\./,
          /mock_frameworks\/rspec/,
          /spec_server/
        ]
      end
      
      def tweak_backtrace(error)
        return if error.backtrace.nil?
        error.backtrace.collect! do |message|
          clean_up_double_slashes(message)
          kept_lines = message.split("\n").select do |line|
            IGNORE_PATTERNS.each do |ignore|
              break if line =~ ignore
            end
          end
          kept_lines.empty?? nil : kept_lines.join("\n")
        end
        error.backtrace.compact!
      end
    end
  end
end

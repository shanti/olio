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
require File.dirname(__FILE__) + '/spec_helper'
require 'spec/runner/formatter/progress_bar_formatter'

# Example of a formatter with custom bactrace printing. Run me with:
# ruby bin/spec failing_examples -r examples/custom_formatter.rb -f CustomFormatter
class CustomFormatter < Spec::Runner::Formatter::ProgressBarFormatter
  def backtrace_line(line)
    line.gsub(/([^:]*\.rb):(\d*)/) do
      "<a href=\"file://#{File.expand_path($1)}\">#{$1}:#{$2}</a> "
    end
  end
end

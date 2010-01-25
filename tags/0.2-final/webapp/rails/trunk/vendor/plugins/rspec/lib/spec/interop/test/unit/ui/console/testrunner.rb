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
require 'test/unit/ui/console/testrunner'

module Test
  module Unit
    module UI
      module Console
        class TestRunner

          alias_method :started_without_rspec, :started
          def started_with_rspec(result)
            @result = result
            @need_to_output_started = true
          end
          alias_method :started, :started_with_rspec

          alias_method :test_started_without_rspec, :test_started
          def test_started_with_rspec(name)
            if @need_to_output_started
              if @rspec_io
                @rspec_io.rewind
                output(@rspec_io.read)
              end
              output("Started")
              @need_to_output_started = false
            end
            test_started_without_rspec(name)
          end
          alias_method :test_started, :test_started_with_rspec

          alias_method :test_finished_without_rspec, :test_finished
          def test_finished_with_rspec(name)
            test_finished_without_rspec(name)
            @ran_test = true
          end
          alias_method :test_finished, :test_finished_with_rspec

          alias_method :finished_without_rspec, :finished
          def finished_with_rspec(elapsed_time)
            @ran_test ||= false
            if @ran_test
              finished_without_rspec(elapsed_time)
            end
          end
          alias_method :finished, :finished_with_rspec
          
          alias_method :setup_mediator_without_rspec, :setup_mediator
          def setup_mediator_with_rspec
            orig_io = @io
            @io = StringIO.new
            setup_mediator_without_rspec
          ensure
            @rspec_io = @io
            @io = orig_io
          end
          alias_method :setup_mediator, :setup_mediator_with_rspec
          
        end
      end
    end
  end
end

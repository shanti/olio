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
class MultiThreadedExampleGroupRunner < Spec::Runner::ExampleGroupRunner
  def initialize(options, arg)
    super(options)
    # configure these
    @thread_count = 4
    @thread_wait = 0
  end

  def run
    @threads = []
    q = Queue.new
    example_groups.each { |b| q << b}
    success = true
    @thread_count.times do
      @threads << Thread.new(q) do |queue|
        while not queue.empty?
          example_group = queue.pop
          success &= example_group.suite.run(nil)
        end
      end
      sleep @thread_wait
    end
    @threads.each {|t| t.join}
    success
  end
end

MultiThreadedBehaviourRunner = MultiThreadedExampleGroupRunner
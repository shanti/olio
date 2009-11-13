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
module Test
  module Unit
    class TestCase
      # Edge rails (r8664) introduces class-wide setup & teardown callbacks for Test::Unit::TestCase.
      # Make sure these still get run when running TestCases under rspec:
      prepend_before(:each) do
        run_callbacks :setup if respond_to?(:run_callbacks)
      end
      append_after(:each) do
        run_callbacks :teardown if respond_to?(:run_callbacks)
      end
    end
  end
end
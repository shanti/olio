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
module FixtureReplacementController
  # This is here so that if someone (some how) assigns a proc
  # to an accessor on an ActiveRecord object, FixtureReplacement
  # won't get tripped up, and try to evaluate the proc.
  class DelayedEvaluationProc < Proc
    def evaluate(caller)
      default_obj, params = self.call
      return caller.__send__("create_#{default_obj.fixture_name}", params)
    end
  end
end
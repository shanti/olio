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
# stub frameworks like to gum up Object, so this is deliberately
# set NOT to run so that you don't accidentally run it when you
# run this dir.

# To run it, stand in this directory and say:
#
#   RUN_RR_EXAMPLE=true ruby ../bin/spec mocking_with_rr.rb

if ENV['RUN_RR_EXAMPLE']
  Spec::Runner.configure do |config|
    config.mock_with :rr
  end
  describe "RR framework" do
    it "should should be made available by saying config.mock_with :rr" do
      o = Object.new
      mock(o).msg("arg")
      o.msg
    end
    it "should should be made available by saying config.mock_with :rr" do
      o = Object.new
      mock(o) do |m|
        m.msg("arg")
      end
      o.msg
    end
  end
end

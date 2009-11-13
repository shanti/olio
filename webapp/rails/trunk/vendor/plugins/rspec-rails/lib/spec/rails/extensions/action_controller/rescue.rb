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
module ActionController
  module Rescue
    def use_rails_error_handling!
      @use_rails_error_handling = true
    end

    def use_rails_error_handling?
      @use_rails_error_handling ||= false
    end

    protected
    def rescue_action_with_fast_errors(exception)
      if use_rails_error_handling?
        rescue_action_without_fast_errors exception
      else
        raise exception
      end
    end
    alias_method_chain :rescue_action, :fast_errors
  end
end

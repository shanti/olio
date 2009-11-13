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
require 'action_controller/cookies'

module Spec
  module Rails
    module Example
      class CookiesProxy
        def initialize(example)
          @example = example
        end
      
        def[]=(name, value)
          @example.request.cookies[name.to_s] = CGI::Cookie.new(name.to_s, value)
        end
        
        def [](name)
          @example.response.cookies[name.to_s]
        end
      
        def delete(name)
          @example.response.cookies.delete(name.to_s)
        end
      end
    end
  end
end

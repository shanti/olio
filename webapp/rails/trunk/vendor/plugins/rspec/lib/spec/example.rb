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
  module Example
    def self.args_and_options(*args)
      with_options_from(args) do |options|
        return args, options
      end
    end

    def self.scope_from(*args)
      args[0] || :each
    end

    def self.scope_and_options(*args)
      args, options = args_and_options(*args)
      return scope_from(*args), options
    end

  private
    
    def self.with_options_from(args)
      yield Hash === args.last ? args.pop : {} if block_given?
    end
  end
end

require 'timeout'
require 'spec/example/before_and_after_hooks'
require 'spec/example/pending'
require 'spec/example/module_reopening_fix'
require 'spec/example/example_group_methods'
require 'spec/example/example_methods'
require 'spec/example/example_group'
require 'spec/example/shared_example_group'
require 'spec/example/example_group_factory'
require 'spec/example/errors'
require 'spec/example/configuration'
require 'spec/example/example_matcher'


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
# Require everything except the global extensions of class and object. This
# supports wrapping rspec's mocking functionality without invading every
# object in the system.

require 'spec/mocks/methods'
require 'spec/mocks/argument_constraints'
require 'spec/mocks/spec_methods'
require 'spec/mocks/proxy'
require 'spec/mocks/mock'
require 'spec/mocks/argument_expectation'
require 'spec/mocks/message_expectation'
require 'spec/mocks/order_group'
require 'spec/mocks/errors'
require 'spec/mocks/error_generator'
require 'spec/mocks/space'

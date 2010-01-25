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
#!/usr/bin/env ruby
#
#  Created by Jim Weirich on 2007-04-10.
#  Copyright (c) 2007. All rights reserved.

require 'flexmock/rspec'

module Spec
  module Plugins
    module MockFramework
      include FlexMock::MockContainer
      def setup_mocks_for_rspec
        # No setup required
      end
      def verify_mocks_for_rspec
        flexmock_verify
      end
      def teardown_mocks_for_rspec
        flexmock_close
      end
    end
  end
end

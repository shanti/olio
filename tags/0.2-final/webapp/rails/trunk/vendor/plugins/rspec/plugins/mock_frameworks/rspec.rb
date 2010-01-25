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
require File.expand_path(File.join(File.dirname(__FILE__), "..", "..", "lib", "spec", "mocks"))
require 'spec/mocks/framework'
require 'spec/mocks/extensions'

module Spec
  module Plugins
    module MockFramework
      include Spec::Mocks::ExampleMethods
      def setup_mocks_for_rspec
        $rspec_mocks ||= Spec::Mocks::Space.new
      end
      def verify_mocks_for_rspec
        $rspec_mocks.verify_all
      end
      def teardown_mocks_for_rspec
        $rspec_mocks.reset_all
      end
    end
  end
end

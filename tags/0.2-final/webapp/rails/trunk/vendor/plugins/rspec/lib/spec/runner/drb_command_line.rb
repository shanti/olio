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
require "drb/drb"

module Spec
  module Runner
    # Facade to run specs by connecting to a DRB server
    class DrbCommandLine
      # Runs specs on a DRB server. Note that this API is similar to that of
      # CommandLine - making it possible for clients to use both interchangeably.
      def self.run(options)
        begin
          DRb.start_service
          spec_server = DRbObject.new_with_uri("druby://127.0.0.1:8989")
          spec_server.run(options.argv, options.error_stream, options.output_stream)
        rescue DRb::DRbConnError => e
          options.error_stream.puts "No server is running"
        end
      end
    end
  end
end

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
require 'rbconfig'

module RubyForker
  # Forks a ruby interpreter with same type as ourself.
  # juby will fork jruby, ruby will fork ruby etc.
  def ruby(args, stderr=nil)
    config       = ::Config::CONFIG
    interpreter  = File::join(config['bindir'], config['ruby_install_name']) + config['EXEEXT']
    cmd = "#{interpreter} #{args}"
    cmd << " 2> #{stderr}" unless stderr.nil?
    `#{cmd}`
  end
end
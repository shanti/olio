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
namespace :mysql do
  task :setup do
    # Get database names from database.yml
    conf = YAML.load(File.read('config/database.yml'))
    prod = conf['production']['database']
    test = conf['test']['database']
    prod_user = conf['production']['username'] || 'root'
    test_user = conf['test']['username'] || 'root'

    run <<-CMD
      mysqladmin -u #{prod_user} create #{prod} &&
      mysqladmin -u #{test_user} create #{test}
    CMD
  end
end

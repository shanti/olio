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
namespace :mongrel do
  set :mongrel_clean, false
  set :mongrel_rails, 'mongrel_rails'
  set :mongrel_conf, nil
  
  def set_mongrel_conf
    machine = get_machine
    set :mongrel_conf, "/work/etc/mongrel/#{machine}/#{application}.yml" unless mongrel_conf
  end
  
  desc "Setup mongrel cluster configuration"
  task :setup, :roles => :app do
    set_mongrel_conf
    machine = get_machine
    
    run <<-CMD
      mongrel_rails cluster::configure -e production -p #{apache_proxy_port} -a 127.0.0.1 -c #{current_path} -N 2 -C #{mongrel_conf} --user #{user} --group ugrad -P #{shared_path}/pids/mongrel.pid
    CMD
  end

  desc <<-DESC
  Start Mongrel processes on the app server.  This uses the :use_sudo variable to determine whether to use sudo or not. By default, :use_sudo is
  set to true.
  DESC
  task :start, :roles => :app do
    set_mongrel_conf
    cmd = "#{mongrel_rails} cluster::start -C #{mongrel_conf}"
    cmd += " --clean" if mongrel_clean    
    send(run_method, cmd)
  end
  
  desc <<-DESC
  Restart the Mongrel processes on the app server by starting and stopping the cluster. This uses the :use_sudo
  variable to determine whether to use sudo or not. By default, :use_sudo is set to true.
  DESC
  task :restart, :roles => :app do
    set_mongrel_conf
    cmd = "#{mongrel_rails} cluster::restart -C #{mongrel_conf}"
    cmd += " --clean" if mongrel_clean    
    send(run_method, cmd)
  end
  
  desc <<-DESC
  Stop the Mongrel processes on the app server.  This uses the :use_sudo
  variable to determine whether to use sudo or not. By default, :use_sudo is
  set to true.
  DESC
  task :stop, :roles => :app do
    set_mongrel_conf
    cmd = "#{mongrel_rails} cluster::stop -C #{mongrel_conf}"
    cmd += " --clean" if mongrel_clean    
    send(run_method, cmd)
  end

  desc <<-DESC
  Check the status of the Mongrel processes on the app server.  This uses the :use_sudo
  variable to determine whether to use sudo or not. By default, :use_sudo is
  set to true.
  DESC
  task :status, :roles => :app do
    set_mongrel_conf
    send(run_method, "#{mongrel_rails} cluster::status -C #{mongrel_conf}")
  end
  
end

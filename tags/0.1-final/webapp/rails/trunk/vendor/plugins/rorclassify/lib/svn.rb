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
namespace :svn do
  desc "Setup repo"
  task :setup do
  end
  
  desc "Import code into svn repository"
  task :import do
    new_path = Dir.pwd + "_svn≈"
    tags = repository.sub("trunk", "tags")
    branches = repository.sub("trunk", "branches")
    puts "Adding branches and tags"
    system "svn mkdir -m 'Adding tags and branches directories' #{tags} #{branches}"
    puts "Importing application."
    system "svn import \"#{repository}\" -m \"Initial Import\""
    puts "Checking out to new directory."
    system "svn co \"#{repository}\" \"#{new_path}\""
    cwd = Dir.getwd
    Dir.chdir new_path
    puts "removing log directory contents from svn"
    system "svn remove log/*"
    puts "ignoring log directory"
    system "svn propset svn:ignore '*.log' log/"
    system "svn update log/"
    puts "removing tmp directory from svn"
    system "svn remove tmp/"
    puts "ignoring tmp directory"
    system "svn propset svn:ignore '*' tmp/"
    system "svn update tmp/"
    puts "committing changes"
    system "svn commit -m \"Removed and ignored log files and tmp\""
    Dir.chdir cwd
    puts "Your repository is: #{repository}" 
    puts "Please change to your new working directory: #{new_path}"
  end
end

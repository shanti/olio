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

# This generator bootstraps a Rails project for use with RSpec
class RspecGenerator < Rails::Generator::Base
  DEFAULT_SHEBANG = File.join(Config::CONFIG['bindir'],
                              Config::CONFIG['ruby_install_name'])

  def initialize(runtime_args, runtime_options = {})
    Dir.mkdir('lib/tasks') unless File.directory?('lib/tasks')
    super
  end

  def manifest
    record do |m|
      script_options     = { :chmod => 0755, :shebang => options[:shebang] == DEFAULT_SHEBANG ? nil : options[:shebang] }

      m.file      'rspec.rake',                    'lib/tasks/rspec.rake'

      m.file      'script/autospec',               'script/autospec',    script_options
      m.file      'script/spec',                   'script/spec',        script_options
      m.file      'script/spec_server',            'script/spec_server', script_options

      m.directory 'spec'
      m.file      'rcov.opts',                     'spec/rcov.opts'
      m.file      'spec.opts',                     'spec/spec.opts'
      m.template  'spec_helper.rb',                'spec/spec_helper.rb'

      m.directory 'stories'
      m.file      'all_stories.rb',                'stories/all.rb'
      m.file      'stories_helper.rb',             'stories/helper.rb'
    end
  end

protected

  def banner
    "Usage: #{$0} rspec"
  end

end

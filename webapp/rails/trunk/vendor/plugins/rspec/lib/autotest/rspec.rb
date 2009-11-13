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
require 'autotest'

Autotest.add_hook :initialize do |at|
  at.clear_mappings
  # watch out: Ruby bug (1.8.6):
  # %r(/) != /\//
  at.add_mapping(%r%^spec/.*_spec.rb$%) { |filename, _| 
    filename 
  }
  at.add_mapping(%r%^lib/(.*)\.rb$%) { |_, m| 
    ["spec/#{m[1]}_spec.rb"]
  }
  at.add_mapping(%r%^spec/(spec_helper|shared/.*)\.rb$%) { 
    at.files_matching %r%^spec/.*_spec\.rb$%
  }
end

class RspecCommandError < StandardError; end

class Autotest::Rspec < Autotest

  def initialize
    super
    self.failed_results_re = /^\d+\)\n(?:\e\[\d*m)?(?:.*?in )?'([^\n]*)'(?: FAILED)?(?:\e\[\d*m)?\n(.*?)\n\n/m
    self.completed_re = /\n(?:\e\[\d*m)?\d* examples?/m
  end
  
  def consolidate_failures(failed)
    filters = new_hash_of_arrays
    failed.each do |spec, trace|
      if trace =~ /\n(\.\/)?(.*spec\.rb):[\d]+:/
        filters[$2] << spec
      end
    end
    return filters
  end

  def make_test_cmd(files_to_test)
    return '' if files_to_test.empty?
    spec_program = File.expand_path(File.join(File.dirname(__FILE__), '..', '..', 'bin', 'spec'))
    return "#{ruby} #{spec_program} #{files_to_test.keys.flatten.join(' ')} #{add_options_if_present}"
  end
  
  def add_options_if_present # :nodoc:
    File.exist?("spec/spec.opts") ? "-O spec/spec.opts " : ""
  end
end

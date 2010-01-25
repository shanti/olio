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
steps_for :running_rspec do

  Given("the file $relative_path") do |relative_path|
    @path = File.expand_path(File.join(File.dirname(__FILE__), "..", "..", "resources", relative_path))
    unless File.exist?(@path)
      raise "could not find file at #{@path}"
    end
  end
  
  When("I run it with the $interpreter") do |interpreter|
    stderr_file = Tempfile.new('rspec')
    stderr_file.close
    @stdout = case(interpreter)
      when /^ruby interpreter/
        args = interpreter.gsub('ruby interpreter','')
        ruby("#{@path}#{args}", stderr_file.path)
      when /^spec script/
        args = interpreter.gsub('spec script','')
        spec("#{@path}#{args}", stderr_file.path)
      when 'CommandLine object' then cmdline(@path, stderr_file.path)
      else raise "Unknown interpreter: #{interpreter}"
    end
    @stderr = IO.read(stderr_file.path)
    @exit_code = $?.to_i
  end
  
  Then("the exit code should be $exit_code") do |exit_code|
    if @exit_code != exit_code.to_i
      raise "Did not exit with #{exit_code}, but with #{@exit_code}. Standard error:\n#{@stderr}"
    end
  end
  
  Then("the $stream should match $regex") do |stream, string_or_regex|
    written = case(stream)
      when 'stdout' then @stdout
      when 'stderr' then @stderr
      else raise "Unknown stream: #{stream}"
    end
    written.should smart_match(string_or_regex)
  end
  
  Then("the $stream should not match $regex") do |stream, string_or_regex|
    written = case(stream)
      when 'stdout' then @stdout
      when 'stderr' then @stderr
      else raise "Unknown stream: #{stream}"
    end
    written.should_not smart_match(string_or_regex)
  end
end

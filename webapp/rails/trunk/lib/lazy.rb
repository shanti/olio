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
# Don't change this file!
# Configure your app in config/environment.rb and config/environments/*.rb

class LazySunday
  %w{== === =~ class clone display dup eql? equal? extend freeze frozen? gem hash id inspect instance_eval instance_of? instance_variable_defined? instance_variable_get instance_variable_set instance_variables is_a? kind_of? method methods nil? object_id private_methods protected_methods public_methods require respond_to? send singleton_methods taint tainted? to_a to_s type untaint }.each do |m|
    module_eval <<-EOS
      def #{m}(*args, &block)
        self.resolve.__send__(:#{m}, *args, &block)
      end
    EOS
  end
  
  def initialize(&block)
    @block = block
  end
  
  def resolve
    # puts caller.join("\n") unless @obj
    @obj ||= @block.call
  end
    
  def method_missing(method, *args, &block)
    resolve.send(method, *args, &block)
  end
end

module Kernel
  def lazy(&block)
    LazySunday.new(&block)
  end
end

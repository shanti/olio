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
module Spec
  module Example
    # In the future, modules will no longer be automatically included
    # in the Example Group (based on the description name); when that 
    # time comes, this code should be removed.
    module ModuleInclusionWarnings
      # Thanks, Francis Hwang.
      class MethodDispatcher
        def initialize(mod, target=nil)
          @mod = mod
          @target = target
        end
        
        def respond_to?(sym)
          @mod && @mod.instance_methods.include?(sym.to_s)
        end
        
        def call(sym, *args, &blk)
          Kernel.warn("Modules will no longer be automatically included in RSpec version 1.1.4.  Called from #{caller[2]}")
          @target.extend @mod
          @target.send(sym, *args, &blk)
        end
      end
      
      def respond_to?(sym)
        MethodDispatcher.new(self.class.described_module).respond_to?(sym) ? true : super
      end
      
    private
      
      def method_missing(sym, *args, &blk)
        md = MethodDispatcher.new(self.class.described_module, self)
        self.respond_to?(sym) ? md.call(sym, *args, &blk) : super
      end
    end
  end
end

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
    module BeforeAndAfterHooks
      # Registers a block to be executed before each example.
      # This method prepends +block+ to existing before blocks.
      def prepend_before(*args, &block)
        before_parts(*args).unshift(block)
      end

      # Registers a block to be executed before each example.
      # This method appends +block+ to existing before blocks.
      def append_before(*args, &block)
        before_parts(*args) << block
      end
      alias_method :before, :append_before

      # Registers a block to be executed after each example.
      # This method prepends +block+ to existing after blocks.
      def prepend_after(*args, &block)
        after_parts(*args).unshift(block)
      end
      alias_method :after, :prepend_after

      # Registers a block to be executed after each example.
      # This method appends +block+ to existing after blocks.
      def append_after(*args, &block)
        after_parts(*args) << block
      end

      # TODO - deprecate this unless there is a reason why it exists
      def remove_after(scope, &block) # :nodoc:
        after_each_parts.delete(block)
      end

      # Deprecated. Use before(:each)
      def setup(&block)
        before(:each, &block)
      end

      # Deprecated. Use after(:each)
      def teardown(&block)
        after(:each, &block)
      end

      def before_all_parts # :nodoc:
        @before_all_parts ||= []
      end

      def after_all_parts # :nodoc:
        @after_all_parts ||= []
      end

      def before_each_parts # :nodoc:
        @before_each_parts ||= []
      end

      def after_each_parts # :nodoc:
        @after_each_parts ||= []
      end
      
    private  
      
      def before_parts(*args)
        case Spec::Example.scope_from(*args)
        when :each; before_each_parts
        when :all; before_all_parts
        when :suite; Spec::Runner.options.before_suite_parts
        end
      end

      def after_parts(*args)
        case Spec::Example.scope_from(*args)
        when :each; after_each_parts
        when :all; after_all_parts
        when :suite; Spec::Runner.options.after_suite_parts
        end
      end

    end
  end
end
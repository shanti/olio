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
  module Mocks
    
    class ArgumentExpectation
      attr_reader :args
      
      def initialize(args, &block)
        @args = args
        @constraints_block = block
        
        if ArgumentConstraints::AnyArgsConstraint === args.first
          @match_any_args = true
        elsif ArgumentConstraints::NoArgsConstraint === args.first
          @constraints = []
        else
          @constraints = args.collect {|arg| constraint_for(arg)}
        end
      end
      
      def constraint_for(arg)
        return ArgumentConstraints::MatcherConstraint.new(arg)   if is_matcher?(arg)
        return ArgumentConstraints::RegexpConstraint.new(arg) if arg.is_a?(Regexp)
        return ArgumentConstraints::EqualityProxy.new(arg)
      end
      
      def is_matcher?(obj)
        return obj.respond_to?(:matches?) && obj.respond_to?(:description)
      end
      
      def args_match?(given_args)
        match_any_args? || constraints_block_matches?(given_args) || constraints_match?(given_args)
      end
      
      def constraints_block_matches?(given_args)
        @constraints_block ? @constraints_block.call(*given_args) : nil
      end
      
      def constraints_match?(given_args)
        @constraints == given_args
      end
      
      def match_any_args?
        @match_any_args
      end
      
    end
    
  end
end

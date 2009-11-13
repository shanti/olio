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
    class Mock
      include Methods

      # Creates a new mock with a +name+ (that will be used in error messages
      # only) == Options:
      # * <tt>:null_object</tt> - if true, the mock object acts as a forgiving
      #   null object allowing any message to be sent to it.
      def initialize(name, stubs_and_options={})
        @name = name
        @options = parse_options(stubs_and_options)
        assign_stubs(stubs_and_options)
      end
      
      # This allows for comparing the mock to other objects that proxy such as
      # ActiveRecords belongs_to proxy objects By making the other object run
      # the comparison, we're sure the call gets delegated to the proxy target
      # This is an unfortunate side effect from ActiveRecord, but this should
      # be safe unless the RHS redefines == in a nonsensical manner
      def ==(other)
        other == __mock_proxy
      end

      def method_missing(sym, *args, &block)
        __mock_proxy.instance_eval {@messages_received << [sym, args, block]}
        begin
          return self if __mock_proxy.null_object?
          super(sym, *args, &block)
        rescue NameError
          __mock_proxy.raise_unexpected_message_error sym, *args
        end
      end
      
      def inspect
        "#<#{self.class}:#{sprintf '0x%x', self.object_id} @name=#{@name.inspect}>"
      end
      
      def to_s
        inspect.gsub('<','[').gsub('>',']')
      end
      
      private
      
        def parse_options(options)
          options.has_key?(:null_object) ? {:null_object => options.delete(:null_object)} : {}
        end
        
        def assign_stubs(stubs)
          stubs.each_pair do |message, response|
            stub!(message).and_return(response)
          end
        end
    end
  end
end

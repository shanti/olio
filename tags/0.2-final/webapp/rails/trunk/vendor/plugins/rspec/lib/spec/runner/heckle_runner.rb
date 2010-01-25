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
begin
  require 'rubygems'
  require 'heckle'
rescue LoadError ; raise "You must gem install heckle to use --heckle" ; end

module Spec
  module Runner
    # Creates a new Heckler configured to heckle all methods in the classes
    # whose name matches +filter+
    class HeckleRunner
      def initialize(filter, heckle_class=Heckler)
        @filter = filter
        @heckle_class = heckle_class
      end
      
      # Runs all the example groups held by +rspec_options+ once for each of the
      # methods in the matched classes.
      def heckle_with
        if @filter =~ /(.*)[#\.](.*)/
          heckle_method($1, $2)
        else
          heckle_class_or_module(@filter)
        end
      end
      
      def heckle_method(class_name, method_name)
        verify_constant(class_name)
        heckle = @heckle_class.new(class_name, method_name, Spec::Runner.options)
        heckle.validate
      end
      
      def heckle_class_or_module(class_or_module_name)
        verify_constant(class_or_module_name)
        pattern = /^#{class_or_module_name}/
        classes = []
        ObjectSpace.each_object(Class) do |klass|
          classes << klass if klass.name =~ pattern
        end
        
        classes.each do |klass|
          klass.instance_methods(false).each do |method_name|
            heckle = @heckle_class.new(klass.name, method_name, Spec::Runner.options)
            heckle.validate
          end
        end
      end
      
      def verify_constant(name)
        begin
          # This is defined in Heckle
          name.to_class
        rescue
          raise "Heckling failed - \"#{name}\" is not a known class or module"
        end
      end
    end
    
    #Supports Heckle 1.2 and prior (earlier versions used Heckle::Base)
    class Heckler < (Heckle.const_defined?(:Base) ? Heckle::Base : Heckle)
      def initialize(klass_name, method_name, rspec_options)
        super(klass_name, method_name)
        @rspec_options = rspec_options
      end

      def tests_pass?
        success = @rspec_options.run_examples
        success
      end

    end
  end
end

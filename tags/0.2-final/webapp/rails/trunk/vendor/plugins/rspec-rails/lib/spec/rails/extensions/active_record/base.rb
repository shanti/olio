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
if defined?(ActiveRecord::Base)
  module ActiveRecord #:nodoc:
    class Base

      (class << self; self; end).class_eval do
        # Extension for <tt>should have</tt> on AR Model classes
        #
        #   ModelClass.should have(:no).records
        #   ModelClass.should have(1).record
        #   ModelClass.should have(n).records
        def records
          find(:all)
        end
        alias :record :records
      end

      # Extension for <tt>should have</tt> on AR Model instances
      #
      #   model.should have(:no).errors_on(:attribute)
      #   model.should have(1).error_on(:attribute)
      #   model.should have(n).errors_on(:attribute)
      def errors_on(attribute)
        self.valid?
        [self.errors.on(attribute)].flatten.compact
      end
      alias :error_on :errors_on

    end
  end
end
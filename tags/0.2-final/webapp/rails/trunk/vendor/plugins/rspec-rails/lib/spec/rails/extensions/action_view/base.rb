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
module ActionView #:nodoc:
  class Base #:nodoc:
    include Spec::Rails::Example::RenderObserver
    cattr_accessor :base_view_path

    alias_method :orig_render_partial, :render_partial
    def render_partial(partial_path, local_assigns = nil, deprecated_local_assigns = nil) #:nodoc:
      if partial_path.is_a?(String)
        unless partial_path.include?("/")
          unless self.class.base_view_path.nil?
            partial_path = "#{self.class.base_view_path}/#{partial_path}"
          end
        end
      end
      begin
        orig_render_partial(partial_path, local_assigns, deprecated_local_assigns)
      rescue ArgumentError # edge rails > 2.1 changed render_partial to accept only one arg
        orig_render_partial(partial_path)
      end
    end

    alias_method :orig_render, :render
    def render(options = {}, old_local_assigns = {}, &block)
      if render_proxy.send(:__mock_proxy).send(:find_matching_expectation, :render, options)
        render_proxy.render(options)
      else
        unless render_proxy.send(:__mock_proxy).send(:find_matching_method_stub, :render, options)
          orig_render(options, old_local_assigns, &block)
        end
      end
    end
  end
end

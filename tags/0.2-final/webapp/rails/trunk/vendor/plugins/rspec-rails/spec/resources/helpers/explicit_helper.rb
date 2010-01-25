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
module ExplicitHelper
  def method_in_explicit_helper
    "<div>This is text from a method in the ExplicitHelper</div>"
  end
  
  # this is an example of a method spec'able with eval_erb in helper specs
  def prepend(arg, &block)
    begin # rails edge after 2.1.0 eliminated need for block.binding
      concat(arg) + block.call
    rescue
      concat(arg, block.binding) + block.call
    end
  end
  
  def named_url
    rspec_on_rails_specs_url
  end
  
  def named_path
    rspec_on_rails_specs_path
  end
  
  def params_foo
    params[:foo]
  end
  
  def session_foo
    session[:foo]
  end
  
  def request_thing
    request.thing
  end
  
  def flash_thing
    flash[:thing]
  end
end

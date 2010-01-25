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
class Class
  # Creates a new subclass of self, with a name "under" our own name.
  # Example:
  #
  #   x = Foo::Bar.subclass('Zap'){}
  #   x.name # => Foo::Bar::Zap_1
  #   x.superclass.name # => Foo::Bar
  def subclass(base_name, &body)
    klass = Class.new(self)
    class_name = "#{base_name}_#{class_count!}"
    instance_eval do
      const_set(class_name, klass)
    end
    klass.instance_eval(&body)
    klass
  end

  private
  def class_count!
    @class_count ||= 0
    @class_count += 1
    @class_count
  end
end
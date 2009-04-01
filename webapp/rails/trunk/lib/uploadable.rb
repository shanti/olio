#
#  Licensed to the Apache Software Foundation (ASF) under one
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
module Uploadable
  
  def self.included(base)
    base.extend ClassMethods
  end
  
  # from attachment_fu, necessary for custom upload directory
  def full_filename(thumbnail = nil)
    file_system_path = (thumbnail ? thumbnail_class : self).attachment_options[:path_prefix].to_s
    File.join(RAILS_ROOT, file_system_path, thumbnail_name_for(thumbnail))
  end
  
  def individualize_filename!(id)
    self.filename = id.to_s + rand(Time.now.to_i).to_s + self.filename
  end
  
  module ClassMethods
    def make_from_upload(data, individualize_string)
      file = self.new(:uploaded_data => data)
      file.individualize_filename! individualize_string
      file.save
      file
    end 
  end
  
end

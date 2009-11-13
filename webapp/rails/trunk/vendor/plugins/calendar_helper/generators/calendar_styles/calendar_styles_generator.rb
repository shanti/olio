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
class CalendarStylesGenerator < Rails::Generator::Base  

  def manifest
    record do |m|
      calendar_themes_dir = "public/stylesheets/calendar"
      m.directory calendar_themes_dir

      # Copy files
      %w(red blue grey).each do |dir|
        m.directory File.join(calendar_themes_dir, dir)
        m.file File.join("#{dir}/style.css"), File.join(calendar_themes_dir, "#{dir}/style.css")
      end

      # Dir.read("vendor/public/calendar_helper/generators/calendar_styles/templates").each do |dir|
#         m.file "orig", File.join(calendar_themes_dir, dir.name, "some_file.css")
#       end

    end
  end
end

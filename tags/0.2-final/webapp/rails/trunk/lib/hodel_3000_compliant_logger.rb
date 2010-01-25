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
require 'logger'
require 'English'
# Jan  2 03:38:05 topfunky postfix/postqueue[2947]: warning blah blah blah

##
# A logger for use with pl_analyze and other tools that expect syslog-style log output.

class Hodel3000CompliantLogger < Logger

  ##
  # Note: If you are using FastCGI you may need to hard-code the hostname here instead of using Socket.gethostname

  def format_message(severity, timestamp, progname, msg)
    "#{timestamp.strftime("%b %d %H:%M:%S")} #{hostname} rails[#{$PID}]: #{msg2str(msg).gsub(/\n/, '').lstrip}\n"
  end

  # original method, pre-patch for Exception handling:
  #
  # def format_message(severity, timestamp, msg, progname)
  #   "#{timestamp.strftime("%b %d %H:%M:%S")} #{Socket.gethostname.split('.').first} rails[#{$PID}]: #{progname.gsub(/\n/, '').lstrip}\n"
  # end

  private

  def hostname
    @parsed_hostname ||= Socket.gethostname.split('.').first
  end

  def msg2str(msg)
    case msg
    when ::String
      msg
    when ::Exception
      "#{ msg.message } (#{ msg.class }): " <<
      (msg.backtrace || []).join(" | ")
    else
      msg.inspect
    end
  end

end

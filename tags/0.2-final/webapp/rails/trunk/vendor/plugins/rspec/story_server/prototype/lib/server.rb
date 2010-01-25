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
require 'webrick'

class DispatchServlet < WEBrick::HTTPServlet::AbstractServlet
  def do_POST(request, response)
    File.open('story', 'w') do |io|
      io.write(request.body)
    end

    response.status = 200
    response['Content-Type'] = 'text/html'
    response.body = "body"
  end
end

params = { :Port        => 4000,
           :ServerType  => WEBrick::SimpleServer,
           :BindAddress => "0.0.0.0",
           :MimeTypes   => WEBrick::HTTPUtils::DefaultMimeTypes }
server = WEBrick::HTTPServer.new(params)
server.mount('/stories', DispatchServlet)
server.mount('/', WEBrick::HTTPServlet::FileHandler, File.dirname(__FILE__) + '/..', { :FancyIndexing => true })

trap("INT") { server.shutdown }
server.start
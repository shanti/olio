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
require 'net/http'
require 'cgi'

class Geolocation
  attr_reader :address, :city, :state, :zip, :country, :latitude, :longitude
  
  @@url = 'http://localhost:8080/geocoder/geocode?appid=gsd5f'
  
  def self.url=(url)
    @@url = url
  end
  
  def initialize(street, city, state, zip)
    args = %w{street city state zip}.zip([street, city, state, zip]).map do |n, v|
      "#{n}=#{CGI::escape(v)}"
    end.join('&')
    response = Net::HTTP.get(URI.parse("#{@@url}&#{args}"))
    if response
      eval(response.scan(%r{<([A-Za-z]+)>\s*([^<]+)\s*</\1>}m).map do |k, v|
        "@#{k.downcase} = '#{v.chomp}'"
      end.join(';'))
    end
  end
end

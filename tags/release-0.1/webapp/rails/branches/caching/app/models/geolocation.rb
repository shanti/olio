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

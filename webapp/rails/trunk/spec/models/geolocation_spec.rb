require File.dirname(__FILE__) + '/../spec_helper'

describe Geolocation do
  before(:each) do
    Geolocation.url = 'http://localhost:8080/Web20Emulator/geocode?appid=gsd5f'
    uri = "http://localhost:8080/Web20Emulator/geocode?appid=gsd5f&street=100+Main+St&city=Berkeley&state=CA&zip=94611"
    Net::HTTP.stub!(:get).with(URI.parse(uri)).and_return <<EOF
<?xml version="1.0" ?>
<ResultSet xmlns="urn:yahoo:maps" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:yahoo:maps http://api.local.yahoo.com/MapsService/V1/GeocodeResponse.xsd">
<Result precision="address">
<Latitude>
33.0000
</Latitude><Longitude>
-177.0000
</Longitude><Address>
100 Main St
</Address><City>
Berkeley
</City><State>
CA
</State><zip>
94611
</zip><Country>
USA
</Country>
</Result>
</ResultSet>
EOF
  end
  
  it "should find geolocation using web service" do
    geo = Geolocation.new('100 Main St', 'Berkeley', 'CA', '94611')
    geo.latitude.should == '33.0000'
    geo.longitude.should == '-177.0000'
    geo.address.should == '100 Main St'
    geo.city.should == 'Berkeley'
    geo.state.should == 'CA'
    geo.country.should == 'USA'
    geo.zip.should == '94611'
  end
end

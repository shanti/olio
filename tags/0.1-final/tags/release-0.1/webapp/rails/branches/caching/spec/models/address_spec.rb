require File.dirname(__FILE__) + '/../spec_helper'

describe Address do
  
  before(:each) do
    @address = Address.new
  end
  
  it "should be valid" do
    address = new_address
    address.should be_valid
  end
  
  it "should be valid with the minimal parameters" do
    @address.street1 = "Soda Hall"
    @address.city = "Berkeley"
    @address.country = "United States"
    @address.zip = 94720
    @address.should be_valid
  end
  
  it "should have a street address" do 
    @address.should have(1).errors_on(:street1)
  end
  
  it "should have a city" do
    @address.should have(1).errors_on(:city)
  end
  
  it "should have a zip code" do
    @address.should have(1).errors_on(:zip)
  end
  
  it "should have a country" do
    @address.should have(1).errors_on(:country)
  end
  
  it "should allow duplicate addresses" do
    address = new_address
    @address.street1 = address.street1
    @address.city = address.city
    @address.country = address.country
    @address.zip = address.zip
    address.should be_valid
    @address.should be_valid
  end
  
end

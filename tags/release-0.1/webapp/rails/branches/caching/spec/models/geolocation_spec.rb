require File.dirname(__FILE__) + '/../spec_helper'

describe Geolocation do
  before(:each) do
    @geolocation = Geolocation.new
  end

  it "should be valid" do
    #location = new_geolocation
    #location.should be_valid
  end

  it "should not be valid" do
    @geolocation.should_not be_valid
  end
  
  # Basic requirements
  it "should require a zip code" do
    @geolocation.should have(1).errors_on(:zip)
  end
  
  it "should require a city" do
    @geolocation.should have(1).errors_on(:city)
  end

  it "should have a numerical latitude" do
    location = Geolocation.new
    
    location.latitude = "ABC"
    location.should have(1).errors_on(:latitude)
    location.errors_on(:latitude).should == ["is not a number"]
    location.latitude.should_not be_instance_of(Numeric)
    
    location.latitude = "`@="
    location.should have(1).errors_on(:latitude)
    location.errors_on(:latitude).should == ["is not a number"]
    location.latitude.should_not be_instance_of(Numeric)
    
    location.latitude = -11.11111111111
    location.should have(0).errors_on(:latitude)
    location.latitude.should be_instance_of(Float)
  end
  
  it "should have a numerical longitude" do
    location = Geolocation.new
    
    location.longitude = "Zsa123"
    location.should have(1).errors_on(:longitude)
    location.errors_on(:longitude).should == ["is not a number"]
    location.longitude.should_not be_instance_of(Numeric)
    
    location.longitude = "114[]0"
    location.should have(1).errors_on(:longitude)
    location.errors_on(:longitude).should == ["is not a number"]
    location.longitude.should_not be_instance_of(Numeric)
    
    location.longitude = -110.0
    location.should have(0).errors_on(:longitude)
    location.longitude.should be_instance_of(Float)
  end
  
  it "should have a valid latitude value" do
    location = Geolocation.new
    
    location.latitude = -90.1
    location.should have(1).errors_on(:latitude)
    location.errors_on(:latitude).should == ["must be between -90 and 90 degrees"]
    
    location.latitude = 90.1
    location.should have(1).errors_on(:latitude)
    location.errors_on(:latitude).should == ["must be between -90 and 90 degrees"]
    
    location.latitude = 12
    location.should have(0).errors_on(:latitude)
  end
  
  it "should have a valid longitude value" do
    location = Geolocation.new
    
    location.longitude = 180.1
    location.should have(1).errors_on(:longitude)
    location.errors_on(:longitude).should == ["must be between -180 and 180 degrees"]
    
    location.longitude = -180.1
    location.should have(1).errors_on(:longitude)
    location.errors_on(:longitude).should == ["must be between -180 and 180 degrees"]
    
    location.longitude = -89.0
    location.should have(0).errors_on(:longitude)
  end
  
end

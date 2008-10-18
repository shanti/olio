require File.dirname(__FILE__) + '/../spec_helper'

describe 'Image model: Image' do
  before do
    @image = Image.new
  end
  
  it 'should be valid' do
    image = new_image
    image.should be_valid
  end
  
  # Basic requirements
  it 'should have a content type' do
    @image.should have(2).errors_on(:content_type)
  end
  
  it 'should have a filename' do
    @image.should have(1).errors_on(:filename)
  end
  
  it "should have a unique filename" do
    images = []
    5.times do
      images << Image.make_from_upload(fixture_file_upload('rails.png', 'image/png'), 1)
    end
    filenames = images.map(&:filename)
    unique_filenames = images.map(&:filename).uniq
    filenames.should == unique_filenames
  end
  
  it 'should have a size' do
    @image.should have(2).errors_on(:size)
  end
  
  it 'should have a valid content type' do
    @image.content_type = "application/pdf"
    @image.should have(1).errors_on(:content_type)
    
    @image.content_type = "text/plain"
    @image.should have(1).errors_on(:content_type)
  end
  
  # 1 Megabyte = 1,048,576 (2^10)
  it 'should allow files <= 1 megabyte in size' do
    image = new_image(:size => 1048576)
    image.should have(0).errors_on(:size)
  end
  
  it 'should not exceed 1 megabyte in size' do
    image = new_image(:size => 1048577)
    image.should have(1).errors_on(:size)
  end
  
  it 'should allow thumbnails smaller than 250 x 250' do
    image = new_image(:thumbnail => "thumb",
                      :height => 250,
                      :width => 250)
    image.should be_valid
    image.should have(0).errors_on(:thumbnail)
  end
  
  it 'should not allow thumbnails larger than 250 x 250' do
    image = create_image(:thumbnail => "thumb",
                      :height => 500,
                      :width => 500)
    #image.should have(0).errors_on(:height)
    image.should be_valid
  end
  
  it 'should have a unique filename' do
    image = create_image
    image2 = new_image(:filename => image.filename)
    image2.should have(1).errors_on(:filename)
    image2.errors_on(:filename).should == ['has already been taken']
  end
  
  it 'should allow for the image to be replaced' do
    image = create_image
    image.destroy
    
    image2 = new_image(:filename => image.filename)
    image2.should be_valid
  end
  
  it 'should have a unique filename even after replacement' do
    image = create_image
    image.destroy
    
    image2 = create_image(:filename => image.filename)
    image2.should be_valid
    
    image3 = new_image(:filename => image.filename)
    image3.should have(1).errors_on(:filename)
  end
end

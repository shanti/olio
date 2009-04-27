require File.dirname(__FILE__) + '/../spec_helper'

describe 'Document model: Document' do
  
  before(:each) do
    @document = Document.new
  end
  
  it 'should be valid' do
    document = new_document
    document.should be_valid
  end
  
  it 'should have a content type' do
    @document.should have(2).errors_on(:content_type)
  end
  
  it 'should have a filename' do
    @document.should have(1).errors_on(:filename)
  end
  
  it "should have a unique filename" do
    docs = []
    5.times do
      docs << Document.make_from_upload(fixture_file_upload('rails.png', 'image/png'), 1)
    end
    filenames = docs.map(&:filename)
    unique_filenames = docs.map(&:filename).uniq
    filenames.should == unique_filenames
  end
  
  it 'should have a size' do
    @document.should have(2).errors_on(:size)
  end
  
  it "should only have a valid content type" do
    @document.content_type = "virus/super-bad-virus"
    @document.should have(1).errors_on(:content_type)
  end
  
  it 'should have a unique filename' do
    document = create_document
    document2 = new_document(:filename => document.filename)
    document2.should have(1).errors_on(:filename)
  end
  
  it 'should allow for the document to be replaced' do
    document = create_document
    document.destroy
    
    document2 = new_document(:filename => document.filename)
    document2.should be_valid
  end
  
end
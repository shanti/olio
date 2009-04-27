require File.dirname(__FILE__) + '/../spec_helper'

describe 'Event model: Event' do
  
  before do
    @event = Event.new
  end
  
  it "should be valid" do
    event = new_event
    event.should be_valid
  end
  
  it "should have have a title" do
    @event.should have(2).errors_on(:title)
    @event.title = String.random(101)    
    @event.should have(1).errors_on(:title)
  end
  
  it "should have a description" do
    @event.should have(2).errors_on(:description)
    @event.title = String.random(501)    
    @event.should have(1).errors_on(:title)
  end
  
  it "should require a properly formatted phone number" do
    @event.should have(2).errors_on(:telephone)
    @event.telephone = '555'
    @event.should have(1).errors_on(:telephone)
    @event.telephone = '555a'
    @event.should have(2).errors_on(:telephone)
    @event.errors_on(:telephone).should == ["is too short (minimum is 7 characters)", "must contain only 1-9 and/or -"]
    @event.telephone = '555-555-5555'
    @event.should have(0).errors_on(:telephone)
    @event.telephone = '555-555-5555-5555'
    @event.should have(0).errors_on(:telephone)
    @event.telephone = '123456789012345678901'
    @event.should have(1).errors_on(:telephone)
  end

  it "should have a timestamp" do
    @event.should have(1).errors_on(:event_timestamp)
  end
  
  it "should have a date" do
    event = create_event
    event.event_timestamp.should_not be_nil
    event.set_date
    event.save
    event.reload
    event.event_date.to_s.should =~ /\d{4}\-\d{2}\-\d{2}/
  end
  
  it "should be able to have attendees" do
    event = create_event
    event.users.should have(1).record
    bob = create_bob
    event.new_attendee(bob)
    event.users.should have(2).records # one for the event creator
    
    event.remove_attendee(bob)
    event.users.should have(1).records
  end
  
  it "should not allow invalid users to attend" do
    event = create_event
    user = User.new
    user.should_not be_valid
    num_attendees = event.users.count
    event.new_attendee(user)
    event.should have(num_attendees).users(true)
    user.events(true).should_not include(event)
  end
  
  it "should allow attendees to no longer attend" do
    event = create_event
    user = event.users[0] # creator
    num_attendees = event.users.count
    event.remove_attendee(user)
    event.should have(num_attendees - 1).users(true)
  end
  
  it "should not allow non-attendees to no longer attend" do
    event = create_event
    bob = create_bob
    event.users.should_not include(bob)
    num_attendees = event.users.count
    event.remove_attendee(bob)
    event.should have(num_attendees).users(true)
  end
  
  it "should be taggable" do
    event = create_event
    event.tag_with('big "green duffle"')
    event.should have(2).tags
    event.tags.map { |t| t.name }.sort.should == ['big', 'green duffle']
    
    found = Event.find_tagged_with('green duffle')
    found.should_not be_nil
    found.should include(event)
    found = Event.paginate_tagged_with('green duffle', :page => 1)
    found.should_not be_nil
    found.should include(event)
  end
  
  it "should be able to search for tags" do
    event = create_event
    event.tag_with('big "green duffle"')
    event.should have(2).tags
    
    events = Event.tag_search('green%')
    events.should have(1).record
  end
  
  it 'should have an address' do
    event = create_event
    event.address(true).should_not be_nil
  end
  
  it 'should have a user' do
    event = create_event
    event.user(true).should_not be_nil
  end
  
  it 'should have an image' do
    event = create_event
    event.image(true).should_not be_nil
  end
  
  it 'should have a document' do
    event = create_event
    event.document(true).should_not be_nil
  end
  
  it "should clean up associated records" do
    event = create_event
    address = event.address
    image = event.image
    document = event.document
    event.tag_with('big "green duffle"')
    event.tags.should have(2).records
    event.taggings(true).should have(2).records
    
    bob = create_bob
    event.new_attendee(bob)
    event.users.should have(2).records # one for the event creator
    
    dead_event = Event.find(event.id)
    dead_event.destroy.should_not be_false
    
    proc { Address.find(address.id) }.should raise_error(ActiveRecord::RecordNotFound)
    proc { Image.find(image.id) }.should raise_error(ActiveRecord::RecordNotFound)
    proc { Document.find(document.id) }.should raise_error(ActiveRecord::RecordNotFound)
    event.users(true).should be_empty
    event.taggings(true).should be_empty
  end
  
  it "should allow association of a new image" do
    event = create_simple_event
    event.image.should be_nil
    image = new_image
    event.image = image
    event.save
    event.image(true).should == image
  end
  
  it "should allow association of a new document" do
    event = create_simple_event
    event.document.should be_nil
    document = new_document
    event.document = document
    event.save
    event.document(true).should == document    
  end
  
  it "should allow updating of an image" do
    event = create_simple_event
    event.image.should be_nil
    image = create_image
    event.image = image
    event.save
    event.image(true).should == image

    image2 = new_image(:filename => image.filename)
    event.image = image2
    event.save
    event.image(true).should == image2
    proc { Image.find(image.id) }.should raise_error(ActiveRecord::RecordNotFound)
  end
  
  it "should allow updating of a document" do
    event = create_simple_event
    event.document.should be_nil
    document = create_document
    event.document = document
    event.save
    event.document(true).should == document

    document2 = new_document
    event.document = document2
    event.save
    event.document(true).should == document2
    proc { Image.find(document.id) }.should raise_error(ActiveRecord::RecordNotFound)    
  end
  
  it "should allow an image to be removed" do
    event = create_simple_event
    event.image.should be_nil
    image = create_image
    event.image = image
    event.save
    event.image(true).should == image
    
    event.image = nil
    proc { Image.find(image.id) }.should raise_error(ActiveRecord::RecordNotFound)
  end
  
  it "should allow a document to be removed" do
    event = create_simple_event
    event.document.should be_nil
    document = create_document
    event.document = document
    event.save
    event.document(true).should == document    
    
    event.document = nil
    proc { Image.find(document.id) }.should raise_error(ActiveRecord::RecordNotFound)    
  end
  
  it "should add tags uniquely" do
    event = create_simple_event
    event.tag_with "a b"
    event.reload
    event.tag_list.should == "a b"
    event.add_tags "b c"
    event.reload
    event.tag_list.should == "a b c"    
  end
end

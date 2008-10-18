require File.dirname(__FILE__) + "/../spec_helper"


describe "User model: User" do
  before do
    User.delete_all
    @user = User.new
  end
  
  it "should be valid" do
    user = new_user
    user.should be_valid
  end
  
  # Basic error validation
  it "should require a username" do
    @user.should have(1).errors_on(:username)
  end
  
  it "should require a password" do
    @user.should have(1).errors_on(:password)
  end
  
  it "should require a firstname" do
    @user.should have(1).errors_on(:firstname)
  end

  it "should require a lastname" do
    @user.should have(1).errors_on(:lastname)
  end

  it "should require a timezone" do
    @user.should have(1).errors_on(:timezone)
  end

  it "should require an email address" do
    @user.should have(1).errors_on(:email)
  end
  
  it "should require an address (id)" do
    @user.should have(1).errors_on(:address_id)
  end

  it "should require a properly formatted phone number" do
    @user.should have(1).errors_on(:telephone)
    @user.telephone = "555"
    @user.should have(1).errors_on(:telephone)
    @user.telephone = "555a"
    @user.should have(2).errors_on(:telephone)
    @user.errors_on(:telephone).should == ["is too short (minimum is 7 characters)", "must contain only 1-9 and/or -"]
    @user.telephone = "555-555-5555"
    @user.should have(0).errors_on(:telephone)
    @user.telephone = "555-555-5555-5555"
    @user.should have(0).errors_on(:telephone)
    @user.telephone = "123456789012345678901"
    @user.should have(1).errors_on(:telephone)
  end
  
  it "should have a summary less than 2500 characters" do
    @user.should have(0).errors_on(:summary)
    @user.summary = String.random(1000)
    @user.should have(0).errors_on(:summary)
    @user.summary = String.random(2501)
    @user.should have(1).errors_on(:summary)
  end
  
  it "should confirm the password" do
    @user.password = "doggy"
    @user.password_confirmation = "kitty"
    @user.should have(1).errors_on(:password)
  end
  
  it "should have an address" do
    user = new_user
    user.address.should_not be_nil
  end
  
  it "should authenticate user" do
    user = create_bob
    User.authenticate("bob", "kitty").should == user
    User.authenticate("bob", "kAtty").should be_nil
    # Password is NOT case sensitive
    User.authenticate("bob", "KITTY").should == user
  end
  
  it "should not allow duplicate username" do
    user = create_bob
    user.should have(0).errors_on(:username)
    bob = new_bob
    bob.should have(1).errors_on(:username)
    bob.errors_on(:username).should == ["has already been taken"]
  end
  
  it "should allow but not require user image" do
    image = create_image
    user1 = create_user
    user1.set_image(image)
    user1.image.should_not be_nil
    
    user2 = new_user
    user2.image.should be_nil
  end
  
  it "should allow replacement of images" do
    image1 = create_image
    user = create_user
    user.set_image(image1)
    user.image.should == image1
    
    image2 = create_image
    user.image=(image2)
    user.image.should == image2
  end
  
  it "should allow queries" do
    user = create_user(:firstname => "Jimmy")
    
    User.search("Jimm", '1').should_not be_nil
    User.search("Jimm", '1').should include(user)
  end
  
  # friendship specs
  ########################################################################

  it "should allow users to see invitations" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user2.should have(1).invitations
  end
  
  it "should be able to see incoming friend request ids" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user2.incoming_invite_ids.should include(user1.id)
  end
  
  it "should be able to see outgoing friend request ids" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user1.outgoing_invite_ids.should include(user2.id)
  end
  
  it "should be able to invite a friend" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user1.should have(1).outgoing_friend_requests
    user1.should have(0).friends
    
    user2.should have(1).incoming_friend_requests
  end
  
  it "should be able to accept a friend as an invite" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true    
    user2.accept(user1).should be_true
    
    user1.should have(1).friends
    user1.should have(0).incoming_friend_requests
    user2.should have(1).friends
    user2.should have(0).incoming_friend_requests
  end

  it "should not allow a user to become their own friend" do
    user1 = create_user
    user1.invite(user1).should be_false
    user1.should have(0).friends
    user1.should have(0).incoming_friend_requests
  end
  
  it "should not allow a user to invite another user twice" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user1.should have(1).outgoing_friend_requests
    user2.should have(1).incoming_friend_requests
    user1.invite(user2).should be_false
    user1.should have(1).outgoing_friend_requests
    user2.should have(1).incoming_friend_requests
  end
  
  it "should not allow a user to accept an invitation from someone who is not invited" do
    user1 = create_user
    user2 = create_user
    
    user1.accept(user2).should be_false
    user1.should have(0).incoming_friend_requests
    user1.should have(0).friends
    user2.should have(0).friends
  end
  
  it "should not allow a user to accept an invitation that they invited" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
    user1.accept(user2).should be_false
    user1.should have(0).friends
    user2.should have(0).friends
  end
  
  it "should allow a user to revoke an invitation" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true 
    user1.should have(1).outgoing_friend_requests
    user2.should have(1).incoming_friend_requests
        
    user1.unfriend(user2).should_not be_false
    user1.reload
    user2.reload
    user1.should have(0).outgoing_friend_requests
    user2.should have(0).incoming_friend_requests
  end
  
  it "should not allow a user to accept an invitation that has been revoked" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true
        
    user1.unfriend(user2).should_not be_false
    user1.reload
    
    user2.accept(user1).should be_false
  end
  
  it "should allow a user to reject an invitation" do    
    user1 = create_user
    user2 = create_user

    user1.invite(user2).should be_true
    user1.should have(1).outgoing_friend_requests
    user2.should have(1).incoming_friend_requests
    
    user2.unfriend(user1).should_not be_nil
    user1.reload
    user2.reload
    user1.should have(0).outgoing_friend_requests
    user2.should have(0).incoming_friend_requests
  end
  
  it "should not allow a user to reject a non-existant invitation" do
    user1 = create_user
    user2 = create_user
    
    user2.unfriend(user1).should be_false
    user1.should have(0).friends
    user2.should have(0).friends
    user1.should have(0).outgoing_friend_requests
    user2.should have(0).incoming_friend_requests
  end
    
  it "should allow a user to destroy a friendship from the invitee" do
    user1 = create_user
    user2 = create_user
    
    user1.invite(user2).should be_true 
    user1.should have(1).outgoing_friend_requests
    user2.should have(1).incoming_friend_requests
    user2.accept(user1).should be_true

    user1.outgoing_friend_requests.should_not include(user2)
    user2.incoming_friend_requests.should_not include(user1)
    user1.friends.should include(user2)
    user2.friends.should include(user1)
    
    user2.unfriend(user1).should_not be_false
    user1.invites.should_not include(user2)
    user2.invites.should_not include(user1)
    
    user1.reload
    user2.reload
    
    user1.friends.should_not include(user2)
    user2.friends.should_not include(user1)
  end
  
  # user-event specs
  ########################################################################
  
  it "should allow users to see upcoming events they are attending" do
    user = new_user
    event = create_event(:event_timestamp => "3008-06-13 11:54:12")
    
    event.new_attendee(user).should_not be_nil
    event.should be_valid
    user.upcoming_events.should include(event)
  end
  
  it "should not allow users to see upcoming events that they are not attending" do
    user = new_user
    event = create_event(:event_timestamp => "3008-06-13 11:54:12")
    
    event.should be_valid
    user.upcoming_events.should_not include(event)
  end
  
  it "should not allow users to see events that have already passed" do
    user = new_user
    event = create_event(:event_timestamp => "2000-06-13 11:54:12")
    
    event.new_attendee(user).should_not be_nil
    event.should be_valid
    user.upcoming_events.should_not include(event)
  end
  
  it "should be able to see events that they posted" do
    event = create_event
    user = User.find_by_id(event.user_id)
    
    event.should be_valid
    user.should be_valid
    user.posted_events.should include(event)
  end

  it "should still see posted events that have already passed" do
    event = create_event(:event_timestamp => "2000-06-13 11:54:12")
    user = User.find_by_id(event.user_id)
    
    event.should be_valid
    user.should be_valid
    user.posted_events.should include(event)
  end
  
  it "should not be able to see events that they did not post" do
    user = create_user
    event = create_event
    
    event.should be_valid
    user.posted_events.should_not include(event)
  end
  
end

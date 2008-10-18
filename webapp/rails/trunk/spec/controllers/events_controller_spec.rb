require File.dirname(__FILE__) + '/../spec_helper'

describe EventsController do
  def login
    @user = mock_model(User)
    User.stub!(:find).and_return(@user)
    session[:user_id] = @user.id
  end
  
  describe "handling GET /events" do

    before(:each) do
      @event = mock_model(Event)
      Event.stub!(:find).and_return([@event])
      @address = mock_model(Address)
      @event.stub!(:address).and_return(@address)
      @zip = "23456"
      @address.stub!(:zip).and_return(@zip)
      @tag = "green"
      Event.stub!(:find_tagged_with).and_return([@event])
      Event.stub!(:tag_search).with(@tag).and_return([@event])
    end
  
    def do_get
      get :index
    end
    
    def do_get_for_date
      get :index, :month => "1", :day => "1", :year => "2008"
    end
    
    describe "requesting a list of events" do
      it "should be successful" do
        do_get
        response.should be_success
      end

      it "should be successful for a single date" do
        do_get_for_date
        response.should be_success
      end

      it "should be successful for a zipcode" do
        session[:zipcode] = @zip
        Geolocation.should_receive(:find_by_zip).with(@zip)
        do_get
      end

      it "should render index template" do
        do_get
        response.should render_template('index')
      end

      it "should find all events" do
        Event.should_receive(:paginate).and_return([@event])
        do_get
      end

      it "should assign the found events for the view" do
        do_get
        assigns[:events].should == [@event]
      end
    end
    
    describe "tagging actions" do
      
      it "should be able to get a list of events with a certain tag" do
        Event.should_receive(:find_tagged_with).and_return([@event])
        get :tagged, :tag => @tag, :page => "1"
        assigns(:events).should == [@event]
        response.should be_success
        response.should render_template('tagged')
      end
      
      it "should be able to search events by tag" do
        Event.should_receive(:tag_search).with(@tag).and_return([@event])
        get :tag_search, :tag => @tag
        assigns(:events).should == [@event]
        response.should be_success
        response.should render_template('tagged')
      end
      
    end
    
    it "should be able to fetch an RSS feed" do
      Event.should_receive(:find).with(:all)
      @request.env["HTTP_ACCEPT"] = "application/xml"
      get :rss
      response.should be_success
    end
    
  end

  describe "handling GET /events.xml" do

    before(:each) do
      @event = mock_model(Event, :to_xml => "XML")
      Event.stub!(:paginate).and_return(@event)
    end
  
    def do_get
      @request.env["HTTP_ACCEPT"] = "application/xml"
      get :index
    end
  
    it "should be successful" do
      do_get
      response.should be_success
    end

    it "should find all events" do
      Event.should_receive(:paginate).and_return([@event])
      do_get
    end
  
    it "should render the found events as xml" do
      @event.should_receive(:to_xml).and_return("XML")
      do_get
      response.body.should == "XML"
    end
  end

  describe "handling GET /events/1" do

    before(:each) do
      @address = mock_model(Address)
      @user = mock_model(User)
      @image = mock_model(Image)
      @document = mock_model(Document)
      @comment = mock_model(Comment)
      
      @event = mock_model(Event, :address => @address, :users => [@user], :image => @image, :document => @document,
                                 :comments => [@comments])
      Event.stub!(:find).and_return(@event)
    end
  
    def do_get
      get :show, :id => "1"
    end

    it "should be successful" do
      do_get
      response.should be_success
    end
  
    it "should render show template" do
      do_get
      response.should render_template('show')
    end
  
    it "should find the event requested" do
      Event.should_receive(:find).with("1", :include => [:users, :image, :document, {:comments => :user }, :address]).and_return(@event)
      do_get
    end
  
    it "should assign the found event for the view" do
      do_get
      assigns[:event].should equal(@event)
    end
  end

  describe "handling GET /events/1.xml" do

    before(:each) do
      @address = mock_model(Address)
      @user = mock_model(User)
      @image = mock_model(Image)
      @document = mock_model(Document)
      @comment = mock_model(Comment)
      
      @event = mock_model(Event, :address => @address, :users => [@user], :image => @image, :document => @document,
                                 :comments => [@comments])
      Event.stub!(:find).and_return(@event)
    end
  
    def do_get
      @request.env["HTTP_ACCEPT"] = "application/xml"
      get :show, :id => "1"
    end

    it "should be successful" do
      do_get
      response.should be_success
    end
  
    it "should find the event requested" do
      Event.should_receive(:find).with("1", :include => [:users, :image, :document, {:comments => :user }, :address]).and_return(@event)
      do_get
    end
  
    it "should render the found event as xml" do
      @event.should_receive(:to_xml).and_return("XML")
      do_get
      response.body.should == "XML"
    end
    
    it "should render events for a date" do
      # implement me!
    end
    
    it "should render all events if an invalid date is specified" do
      # implement me!
    end
  end

  describe "handling GET /events/new" do

    before(:each) do
      @event = mock_model(Event)
      @address = mock_model(Address)
      Event.stub!(:new).and_return(@event)
      Address.stub!(:new).and_return(@address)
      login
    end
  
    def do_get
      get :new
    end

    it "should be successful" do
      do_get
      response.should be_success
    end
  
    it "should render new template" do
      do_get
      response.should render_template('new')
    end
  
    it "should create an new event" do
      Event.should_receive(:new).and_return(@event)
      do_get
    end
  
    it "should not save the new event" do
      @event.should_not_receive(:save)
      do_get
    end
  
    it "should assign the new event for the view" do
      do_get
      assigns[:event].should equal(@event)
    end
  end

  describe "handling GET /events/1/edit" do

    before(:each) do
      login
      @address = mock_model(Address)
      @image = mock_model(Image)
      @document = mock_model(Document)
      @comment = mock_model(Comment)
      @tag = mock_model(Tag)
      
      @event = mock_model(Event, :address => @address, :users => [@user], :image => @image, :document => @document,
                                 :comments => [@comments], :user_id => @user.id, :tag_list => [@tag])
      Event.stub!(:find).and_return(@event)
    end
  
    def do_get
      get :edit, :id => "1"
    end
    
    it "should be successful" do
      do_get
      response.should be_success
    end
    
    it "should not be successful when not called by the event creator" do
      user = mock_model(User)
      session[:user_id] = user.id
      get :edit, :id => "1"
      flash[:error].should == "You can only edit events you created"
      response.should redirect_to(events_path)
    end
  
    it "should render edit template" do
      do_get
      response.should render_template('edit')
    end
  
    it "should find the event requested" do
      Event.should_receive(:find).and_return(@event)
      do_get
    end
  
    it "should assign the found Event for the view" do
      do_get
      assigns[:event].should equal(@event)
    end
  end

  describe "handling POST /events" do

    before(:each) do
      login
      @controller.stub!(:'verified_request?').and_return(:true)
      @event = mock_model(Event, :to_param => "1")
      Event.stub!(:new).and_return(@event)
      @event.stub!(:set_date)
      
      @address = mock_model(Address)
      Address.stub!(:new).and_return(@address)
      
      @image = mock_model(Image)
      Image.stub!(:new).and_return(@image)
      @document = mock_model(Document)
      Document.stub!(:new).and_return(@document)
    end
    
    describe "with successful save" do
  
      def do_post
        @event.should_receive(:'save!').and_return(true)
        @address.should_receive(:'save!').and_return(true)
        post :create, :event => {}, :event_image => '', :event_document => ''
      end
  
      it "should create a new event" do
        Event.should_receive(:new).with({}).and_return(@event)
        @event.should_receive(:'user_id=').with(@user.id).and_return(@user.id)
        @event.should_receive(:'address=')
        do_post
      end

      it "should redirect to the new event" do
        @event.should_receive(:'user_id=').with(@user.id).and_return(@user.id)
        @event.should_receive(:'address=')
        do_post
        response.should redirect_to(event_url("1"))
      end
      
    end
    
    describe "with failed save" do

      def do_post
        @address.should_receive(:'save!').and_return(true)
        @event.should_receive(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        post :create, :event => {}, :event_image => '', :event_document => ''
      end
  
      it "should re-render 'new'" do
        @event.should_receive(:'user_id=').with(@user.id).and_return(@user.id)
        @event.should_receive(:'address=')
        do_post
        response.should render_template('new')
      end
    end
    
    describe 'with attendance' do
      
      before(:each) do
        User.should_receive(:find_by_id).with(@user.id).and_return(@user)
        Event.should_receive(:find_by_id).with(@event.id.to_s).and_return(@event)
        @event.stub!(:users).and_return([])
        @event.stub!(:title).and_return('title')
        @user.stub!(:upcoming_events).and_return([])
      end
      
      def do_post
        post :attend, :event => {}, :id => @event.id
      end

      it "should have user attend" do
        @event.should_receive(:new_attendee).with(@user).and_return(nil)
        do_post
        flash[:notice].should == "You are attending #{@event.title}"
      end
            
      it "should not be able to attend twice" do
        @event.stub!(:users).and_return([@user])
        @event.stub!(:title).and_return('title')
        do_post
        flash[:error].should == "You are already attending #{@event.title}"
      end
    end
    
    describe "with unattend" do
      before(:each) do
        User.should_receive(:find_by_id).with(@user.id).and_return(@user)
        Event.should_receive(:find_by_id).with(@event.id.to_s).and_return(@event)
        @event.stub!(:users).and_return([])
        @event.stub!(:title).and_return('title')
        @user.stub!(:upcoming_events).and_return([])
      end
      
      def do_post
        post :unattend, :event => {}, :id => @event.id
      end      
      
      it "should remove attendence" do
        @event.stub!(:users).and_return([@user])
        @event.should_receive(:remove_attendee).with(@user).and_return(nil)
        do_post
        flash[:notice].should == "You are no longer attending #{@event.title}"
      end
      
      it "should not remove attendence if you're not attending" do
        @event.stub!(:title).and_return('title')
        @event.stub!(:users).and_return([])
        do_post
        flash[:error].should == "You are not attending #{@event.title}"
      end
      
    end
  end

  describe "handling PUT /events/1" do

    before(:each) do
      login
      @address = mock_model(Address)
      @controller.stub!(:'verified_request?').and_return(:true)
      @event = mock_model(Event, :to_param => "1", :address => @address)
      Event.stub!(:find).and_return(@event)
      Event.stub!(:find_by_id).with("1").and_return(@event)
      @event.stub!(:set_date)
          
      @image = mock_model(Image)
      Image.stub!(:new).and_return(@image)
      @document = mock_model(Document)
      Document.stub!(:new).and_return(@document)
    end
    
    describe "with successful update" do

      def do_put
        @event.should_receive(:'save!').and_return(true)
        @event.should_receive(:'attributes=')
        @address.should_receive(:'save!').and_return(true)
        @address.should_receive(:'attributes=')
        put :update, :event => {}, :address => {}, :event_image => '', :event_document => '', :id => "1"
      end

      it "should find the event requested" do
        Event.should_receive(:find).with("1").and_return(@event)
        @event.should_receive(:address).and_return(@address)
        do_put
      end

      it "should update the found event" do
        do_put
        assigns(:event).should equal(@event)
      end

      it "should assign the found event for the view" do
        do_put
        assigns(:event).should equal(@event)
      end

      it "should redirect to the event" do
        do_put
        response.should redirect_to(event_url("1"))
      end

    end
    
    describe "with failed update" do

      def do_put
        @event.should_receive(:'save!').and_raise(ActiveRecord::RecordNotSaved)
        @event.should_receive(:'attributes=')
        @address.should_receive(:'save!').and_return(true)
        @address.should_receive(:'attributes=')
        put :update, :event => {}, :address => {}, :event_image => '', :event_document => '', :id => "1"
      end

      it "should re-render 'edit'" do
        do_put
        response.should render_template('edit')
      end

    end
    
    describe "with tag" do
      def do_put
        @event.should_receive(:add_tags).with('dog')
        @event.stub!(:reload)
        put :tag, :id => "1", :tag => 'dog', :format => 'js'
      end
      
      it "should add tag using ajax" do
        do_put
        response.should render_template('tag')
      end
    end
  end

  describe "handling DELETE /events/1" do

    before(:each) do
      login
      @controller.stub!(:'verified_request?').and_return(:true)
      @event = mock_model(Event, :to_param => "1", :address => @address, :user_id => @user.id, :destroy => true,
                                 :image => @image, :document => @document, :users => [])
      Event.stub!(:find).and_return(@event)          
    end
    
    def do_delete
      delete :destroy, :id => "1"
    end

    it "should find the event requested" do
      Event.should_receive(:find).with("1").and_return(@event)
      do_delete
    end
  
    it "should call destroy on the found event" do
      @event.should_receive(:destroy)
      do_delete
    end
  
    it "should redirect to the events list" do
      do_delete
      response.should redirect_to(events_url)
    end
  end

  describe "handling GET /events/update_calendar" do
    
    it "should be successful" do
      get :update_calendar
      response.should redirect_to(root_path)
    end
  
    it "should render show template" do
      xhr :get, :update_calendar
      response.should render_template('update_calendar')
    end
    
  end

end
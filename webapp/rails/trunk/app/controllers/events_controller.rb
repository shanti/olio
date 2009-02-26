class EventsController < ApplicationController

  layout 'site', :except => [ :rss ]
  before_filter :authorize, :only => [
    :new, :edit, :create, :update, :destroy, 
    :attend, :unattend, :tag
  ]
  protect_from_forgery :only => [:update, :destroy, :delete, :create]

  MAX_ATTENDEES = 20
  
  # caches_page :index
    
  ### CRUD Actions ######################################################## 
  
  # GET /events
  # GET /events.xml
  def index
    unless params[:month].nil? and params[:day].nil? and params[:year].nil?
      date = Date.parse("#{params[:month]}/#{params[:day]}/#{params[:year]}")
    end
    @zipcode = params[:zipcode] ? params[:zipcode] : session[:zipcode] # Update zipcode filter if changed by the user
    session[:zipcode] = @zipcode # Store the new zipcode filter in the user's session
    
    @date = Date.parse(date.to_s) unless date.nil?  
    session[:date] = @date
    
    conditions = @date ? "event_date = '#{@date}'" : "event_date >= '#{Date.today}'"

    session[:order] = params[:order] || session[:order] || 'event_date'
      
    @events = Event.paginate :page => params[:page], :conditions => conditions, :order => session[:order], :per_page => 10
    if @zipcode and !@zipcode.empty?
      @events.delete_if { |e| e.address.zip != @zipcode }
    end
      
    @tags = Event.top_n_tags(50)

    respond_to do |format|
      format.html # index.html.erb
      format.js # index.js.rjs
      format.xml  { render :xml => @events }
    end
  end
  
  # GET /events/1
  # GET /events/1.xml  
  def show
    @event = Event.find(params[:id], :include => [:image, :document, {:comments => :user }, :address])
    @address = @event.address
    @attendees = attendee_list(@event, MAX_ATTENDEES)
    @image = @event.image
    @document = @event.document
    @comments = @event.comments
    @comment = Comment.new
    @comment.rating = 0
    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @event }
    end
  end
  
  # GET /events/new
  # GET /events/new.xml
  def new
    @event = Event.new
    @address = Address.new
    
    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @event }
    end
  end
  
  # POST /events
  # POST /events.xml  
  def create
    @event = Event.new(params[:event])
    @event.user_id = session[:user_id]
    @event.set_date
    @address = Address.new(params[:address])
    @geolocation = Geolocation.new(@address.street1, @address.city, @address.state, @address.zip)
    @address.longitude = @geolocation.longitude
    @address.latitude = @geolocation.latitude
    begin
      Event.transaction do
        @address.save!
        @event.address = @address
        
        @event.image = Image.make_from_upload(params[:event_image], @event.id) if new_image?
        @event.document = Document.make_from_upload(params[:event_document], @event.id) if new_document?
        
        @event.save! # must come after all other updates
        set_tags(@event)
        respond_to do |format|
          flash[:notice] = 'Event was successfully created.'
          format.html { redirect_to(@event) }
          format.xml  { render :xml => @event, :status => :created, :location => @event }
        end
      end
    rescue
      logger.warn $!.to_s
      respond_to do |format|
        flash[:error] = 'Could not create event'
        format.html { render :action => "new" }
        format.xml  { render :xml => @event.errors, :status => :unprocessable_entity }
      end
    end
  end

  # GET /events/1/edit
  def edit
    @event = Event.find(params[:id])
    allowed = false
    if check_creator(@event.user_id, 'edit')
      @address = @event.address
      params[:tag_list] = @event.tag_list
      allowed = true
    end
    
    respond_to do |format| 
      if allowed
        format.html # edit.html.erb
      else
        format.html { redirect_to(events_path) }
      end
    end
  end
  
  # PUT /events/1
  # PUT /events/1.xml
  def update
    @event = Event.find(params[:id])
    @address = @event.address
    begin
      Event.transaction do
        @event.attributes = params[:event]
        @event.set_date
        @address.attributes = params[:address]
        @geolocation = Geolocation.new(@address.street1, @address.city, @address.state, @address.zip)
        @address.longitude = @geolocation.longitude
        @address.latitude = @geolocation.latitude
        @address.save!
        
        @event.image = Image.make_from_upload(params[:event_image], @event.id) if new_image?
        @event.document = Document.make_from_upload(params[:event_document], @event.id) if new_document?
        
        @event.save! # must come after all other updates
        
        set_tags(@event)
        respond_to do |format|
          flash[:notice] = 'Event was successfully updated.'
          format.html { redirect_to(@event) }
          format.xml  { head :ok }
        end
      end
    rescue
      logger.warn $!.to_s
      respond_to do |format|
        format.html { render :action => "edit" }
        format.xml  { render :xml => @event.errors, :status => :unprocessable_entity }
      end
    end
    
  end
  
  # DELETE /events/1
  # DELETE /events/1.xml  
  def destroy
    @event = Event.find(params[:id])
    if check_creator(@event.user_id, 'delete')
      @event.destroy
      @event = nil
    end

    respond_to do |format|
      format.html { redirect_to(events_url) }
      format.xml  { head :ok }
    end
  end

  
  
  ### Tagging Actions ######################################################## 
  
  # GET /events/tagged/tag
  # GET /events/tagged/tag.xml
  def tagged
    @tag = params[:tag]
    @events = Event.paginate_tagged_with(params[:tag], :page => params[:page], :per_page => 10)
    
    respond_to do |format|
      format.html # tagged.html.erb
      format.xml { render :xml => @events }
    end
  end
  
  # GET /events/tag_search
  # GET /events/tag_search.xml
  def tag_search
    @tag = params[:tag]
    @events = Event.tag_search(params[:tag])
    # could use pagination but relies on same view as tagged, which doesn't paginate
    
    respond_to do |format|
      format.html { render :action => 'tagged' }
      format.xml { render :xml => @events }
    end
  end
  
  
  
  ### Feed Actions ########################################################  
  
  # GET /events/rss
  def rss
    @events = Event.find(:all)
    @today = Date.today
    
    respond_to do |format|
      format.xml # rss.xml.builder
    end
  end
  
  
  ### Attendance Actions ######################################################## 
  
  # POST /events/1/attend
  def attend
    if @event = Event.find_by_id(params[:id])
      if user = User.find_by_id(session[:user_id])
        if @event.users.include?(user)
          flash[:error] = "You are already attending #{@event.title}"
        else
          @event.new_attendee(user)
          flash[:notice] = "You are attending #{@event.title}"
        end
        session[:upcoming] = user.upcoming_events.map { |e| e.id }
      end
    end
    
    @attendees = attendee_list(@event, MAX_ATTENDEES)
    
    respond_to do |format|
      format.html { redirect_to(event_path(@event)) }
      format.js { render :layout => false }
    end 
  end
  
  # POST /events/1/unattend
  def unattend
    if @event = Event.find_by_id(params[:id])
      if user = User.find_by_id(session[:user_id])
        if not @event.users.include?(user)
          flash[:error] = "You are not attending #{@event.title}"
        else
          @event.remove_attendee(user) 
          flash[:notice] = "You are no longer attending #{@event.title}"
        end
        session[:upcoming] = user.upcoming_events.map { |e| e.id }
      end
    end
    
    @attendees = attendee_list(@event, MAX_ATTENDEES)
    
    respond_to do |format|
      format.html { redirect_to(event_path(@event)) }
      format.js { render :layout => false }
    end 
  end
  
  # PUT /events/1/tag
  def tag
    if @event = Event.find_by_id(params[:id])
      @event.add_tags(params[:tag])
      @event.reload
    end
    
    respond_to do |format|
      format.html { redirect_to(event_path(@event)) }
      format.js { render :layout => false }
    end 
  end    
  
  ### Calendar Actions ######################################################## 
  # (for the calendar rendered from the calendar_helper plugin)
  
  # GET /events/update_calendar (AJAX)
  def update_calendar
    respond_to do |format|
      format.html { redirect_to(root_path) } 
      format.js
    end
  end
  
  
  private #################################################################################### 
  
  def check_creator(event_user_id, action)
    if event_user_id != session[:user_id]
      flash[:error] = "You can only #{action} events you created"
      return false
    else
      return true
    end
  end
  
  def set_tags(event)
    event.tag_with(params[:tag_list]) if params[:tag_list]
  end
  
  def new_image?
    return (params[:event_image] == '') ? false : true
  end
  
  def new_document?
    return (params[:event_document] == '') ? false : true
  end

  def attendee_list(event, max)
    users = event.users.find(:all, :limit => max)
    if session[:user_id]
      included = users.find { |u| u.id == session[:user_id] }
      p included
      p event.users.count(:conditions => ["users.id = ?",  session[:user_id]])
      if !included and event.users.count(:conditions => ["users.id = ?",  session[:user_id]]) > 0
        users<< (@user || User.find(session[:user_id]))
      end
    end
    users
  end
  
end

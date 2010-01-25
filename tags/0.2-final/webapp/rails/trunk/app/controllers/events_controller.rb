#
#  Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
class EventsController < ApplicationController

  layout 'site', :except => [ :rss ]
  before_filter :authorize, :only => [
    :new, :edit, :create, :update, :destroy, 
    :attend, :unattend, :tag
  ]
  protect_from_forgery :only => [:update, :destroy, :delete, :create]

  MAX_ATTENDEES = 20
  
  if CACHED
    after_filter :expire_home, :only => :index
    after_filter :expire_calendar, :only => :update_calendar
    after_filter :expire_tag, :only => :tag

    # caches_page :index, {:expire => 2.minutes.to_i}
    cache_sweeper :event_sweeper, :only => [:create, :destroy, :update]
  end
    
  ### CRUD Actions ######################################################## 
  
  # GET /events
  # GET /events.xml
  def home
    if CACHED and !session[:user_id].nil?
      do_index_page
      respond_to do |format|
        format.html { render :template => "events/index.html.erb" } # index.html.erb
        format.js { render :template => "events/index.js.rjs", :layout => false } # index.js.rjs
        format.xml  { render :xml => @events }
      end
    else
      redirect_to(root_path)
    end
  end
  
  # "home" = root page for those not logged in
  def index
    if !CACHED or session[:user_id].nil?
      do_index_page
      respond_to do |format|
        format.html # index.html.erb
        format.js  # index.js.rjs
        format.xml { render :xml => @events }
      end
    else
      redirect_to(home_path)
    end
  end

  # GET /events/1
  # GET /events/1.xml  
  def show
    @event = lazy { Event.find(params[:id], :include => [:image, :document, {:comments => :user }, :address]) }
    @attendees = lazy { attendee_list(@event, MAX_ATTENDEES) }
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
        @address.save!
        
        @event.image = Image.make_from_upload(params[:event_image], @event.id) if new_image?
        @event.document = Document.make_from_upload(params[:event_document], @event.id) if new_document?
        @geolocation = Geolocation.new(@address.street1, @address.city, @address.state, @address.zip)
        @address.longitude = @geolocation.longitude
        @address.latitude = @geolocation.latitude
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
          expire_attendees
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
          expire_attendees
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
    expire_fragment(:controller => "events", :part => "default_calendar")
    respond_to do |format|
      format.html { redirect_to(root_path) } 
      format.js
    end
  end
  
  
  private #################################################################################### 
  
  def do_index_page
    unless params[:month].nil? and params[:day].nil? and params[:year].nil?
      date = Date.parse("#{params[:month]}/#{params[:day]}/#{params[:year]}")
    end

    @zipcode = params[:zipcode] ? params[:zipcode] : session[:zipcode] # Update zipcode filter if changed by the user
    session[:zipcode] = @zipcode # Store the new zipcode filter in the user's session
  
    @date = Date.parse(date.to_s) unless date.nil?
    session[:date] = @date
  
    conditions = @date ? "event_date = '#{@date}'" : "event_date >= '#{Date.today}'"

    session[:order] = params[:order] || session[:order] || 'event_date'

    @events = lazy { Event.paginate :page => params[:page], :conditions => conditions, :order => session[:order], :per_page => 10,  :include => [:address, :image] }
    if @zipcode and !@zipcode.empty?
      @events.delete_if { |e| e.address.zip != @zipcode }
    end

    @tags = lazy { Event.top_n_tags(50) }
  end

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
##    return (params[:event_image] == '') ? false : true
# Fix for rails 2.3.2
	return !(params[:event_image].blank?)
  end
  
  def new_document?
#    return (params[:event_document] == '') ? false : true
	return !(params[:event_document].blank?)
  end

  def attendee_list(event, max)
    users = event.users.find(:all, :limit => max)
    if session[:user_id]
      included = users.find { |u| u.id == session[:user_id] }
      if !included and event.users.count(:conditions => ["users.id = ?",  session[:user_id]]) > 0
        users<< (@user || User.find(session[:user_id]))
      end
    end
    users
  end
  
  # CACHING - methods to expire fragments

  def expire_home
    expire_page(root_path)
  end

  def expire_calendar
    expire_fragment(:controller => "events", :part => "default_calendar")
  end

  def expire_tag
    unless session.nil?
      expire_fragment(:controller => 'events', :action => 'index', :part => 'tag_cloud')
    end
    expire_page(root_path)
  end

  def expire_attendees
    expire_fragment(:controller => "events", :action => "show", :id => @event.id, :part => "event_attendees")
    expire_fragment(:controller => "events", :action => "show", :id => @event.id, :part => "event_attendees", :login => true)
  end

end

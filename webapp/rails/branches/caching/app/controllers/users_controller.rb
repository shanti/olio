class UsersController < ApplicationController
  
  before_filter :authorize, :except => [ :new, :create, :login, :check_name, :show ]
  layout "site"
    
  # GET /users
  # GET /users.xml
  def index
    @users = User.paginate :page => params[:page], :order => 'lastname, firstname', :per_page => 20

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @users }
    end
  end
  
  # GET /users/search
  # GET /users/search.xml
  def search
    @query = params[:query]
    @users = User.search(@query, params[:page]) # FIX: needs pagination
    invites_for_friend_links @user
    
    respond_to do |format|
      format.html # search.html.erb
      format.js { render :layout => false }
      format.xml { render :xml => @users }
    end
  end
  
  # GET /users/1
  # GET /users/1.xml
  def show
    @user = User.find(params[:id])
    @you = User.find(session[:user_id])
    @address = Address.find(@user.address_id)
    @image = @user.image
    @posted = @user.posted_events[0..2]
    @friends = @user.friends
    @incoming = @user.incoming_friend_requests
    @outgoing = @user.outgoing_friend_requests
    invites_for_friend_links @you
    
    generate_friend_cloud @friends
    
    # TODO: Replace the following logic with a helper
    if session[:user_id].nil?
      @logged_in = false
    else
      @logged_in = true
      @current_user = User.find(session[:user_id])
    end
    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @user }
    end
  end
  
  # GET /users/new
  # GET /users/new.xml
  def new
    @user = User.new
    @address = Address.new
    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @user }
    end
  end

  # POST /users
  # POST /users.xml
  def create
    @user = User.new(params[:user])
    @address = Address.new(params[:address])
    @geolocation = Geolocation.new(@address.street1, @address.city, @address.state, @address.zip)
    @address.longitude = @geolocation.longitude
    @address.latitude = @geolocation.latitude
    begin
      User.transaction do
        @user.image = Image.make_from_upload params[:user_image], @user.id if new_image?
        @address.save!
        @user.address_id = @address.id
        @user.save!
        respond_to do |format|
            flash[:notice] = "Succeeded in creating user."
            format.html { redirect_to(root_path) } # used to be login_users_path
            format.xml  { render :xml => @user, :status => :created, :location => @user }
        end
      end
    rescue
      respond_to do |format|
        flash[:error] = "Failed to create user."
        format.html { render :action => "new" }
        format.xml  { render :xml => @user.errors, :status => :unprocessable_entity }
      end
    end
  end
  
  # GET /users/1/edit
  def edit
    @user = User.find(params[:id]) 
    @address = Address.find(@user.address_id)
    if !logged_in_as(@user)
      flash[:error] = "Failed to edit user."
      redirect_to(events_path)
    end
  end
  
  # PUT /users/1
  # PUT /users/1.xml
  def update
    @user = User.find(params[:id])
    @address = @user.address 
    @image = nil
    
    begin
      if logged_in_as(@user)
        User.transaction do
          @user.attributes = params[:user]
          session[:user_name] = @user.username
          @address.attributes = params[:address]
          @address.save!
          
          @user.image = Image.make_from_upload(params[:user_image], @user.id) if new_image?
          
          @user.save!
          respond_to do |format|
            flash[:notice] = "Succeeded in updating user."
            format.html { redirect_to @user }
            format.xml  { head :ok }
          end
        end
      end
    rescue
      respond_to do |format|
        flash[:error] = "Failed to update user."
        format.html { render :action => "edit" }
        format.xml  { render :xml => @user.errors, :status => :unprocessable_entity }
      end
    end
  end
  
  # GET /users/login - renders login form
  # POST /users/login - processes the login
  def login
    session[:user_id] = nil
    if request.post?
      user = User.find_by_username_and_password(params[:users][:username], params[:users][:password])
      if user
        session[:user_id] = user.id
        session[:user_name] = user.username
        session[:friend_requests] = user.incoming_friend_requests.length
        session[:upcoming] = user.upcoming_events[0,3].map { |e| e.id } # top 3 upcoming events
        uri = session[:original_uri]
        session[:original_uri] = nil
                
        flash[:notice] = "Successfully logged in!"
        if CACHED
          redirect_to(uri || home_path)
        else
          redirect_to(uri || events_path)
        end
      else
        user = nil
        params[:email] = nil
        params[:password] = nil
        flash[:notice] = "Invalid user/password combination."
        redirect_to(root_path)
      end
    end
  end
  
  # GET /users/logout
  def logout
    session[:user_id] = nil
    session[:user_name] = nil
    redirect_to(root_path)
  end
  
  # POST /users/check_if_name_is_valid
  def check_name
    if User.find_by_username(params[:name])
      render :text => "Name taken"
    else
      render :text => "Valid name"
    end
  end
  
  def upcoming_events
    @upcoming = User.find_by_id(params[:id]).upcoming_events.paginate :page => params[:page], :per_page => 10
    respond_to do |format|
      format.html
      format.xml  { head :ok }
    end
  end
  
  def posted_events
    @posted = User.find_by_id(params[:id]).posted_events.paginate :page => params[:page], :per_page => 10
    respond_to do |format|
      format.html
      format.xml { head :ok }
    end
  end
  
  
  private ###################################################
  
  def new_image?
    return (params[:user_image] == '') ? false : true
  end
  
  def invites_for_friend_links(user)
    if session[:user_id]
      @outgoing_invite_ids = user.outgoing_invite_ids
      @incoming_invite_ids = user.incoming_invite_ids
    else
      @outgoing_invite_ids, @incoming_invite_ids = [], []
    end
  end
    
end

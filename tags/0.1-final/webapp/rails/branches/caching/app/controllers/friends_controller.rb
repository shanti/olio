class FriendsController < ApplicationController
  
  before_filter :authorize, :except => [:index]
  layout "site"
  
  # NOTE: new, edit, and show have no meaning for this nested resource
  
  # GET /users/1/friends
  # GET /users/1/friends.xml
  def index
    @user = User.find(params[:user_id])
    @friends = @user.friends.paginate :page => params[:page], :per_page => 10
    
    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @friends }
    end
  end
  
  # POST /users/1/friends
  def create
    begin
      @user = User.find(session[:user_id])
      @target = User.find(params[:user_id])
      unless @user.invite(@target)
        flash[:error] = "Could not request friendship."
      end
    rescue ActiveRecord::RecordNotFound
      flash[:error] = "Invalid source or target user."
    end
    
    respond_to do |format|
      if flash[:error].blank?
        flash[:notice] = "Friendship requested"
        expire_fragment ("events/friend_requests/#{@target.id}")
        format.html { redirect_to(search_users_path) }
        format.js { render :layout => false }
      else
        format.html { redirect_to(search_users_path) }
        format.js do
          render :update do |page|
            page.refresh_messages
          end
        end
      end
    end
  end
  
  # PUT /users/1/friends/2
  # PUT /users/1/friends/2.xml
  def update
    begin
      @user = User.find(params[:user_id])
      @target = User.find(params[:id])
      if @user.accept(@target)
        decrement_friendship_requests
        generate_friend_cloud @user.friends
        flash[:notice] = 'Friendship approved.'
        expire_fragment ("events/friend_requests/#{@target.id}")
      else
        flash[:error] = 'Friendship could not be approved.'
      end
    rescue ActiveRecord::RecordNotFound
      flash[:error] = "Invalid source or target user."
    end
    
    respond_to do |format|
      if flash[:error].blank?        
        format.html { redirect_to(@user) }
        format.js { render :layout => false }
        format.xml  { head :ok }
      else        
        format.html { redirect_to(@user) }
        format.js do
          render :update do |page|
            page.refresh_messages
          end
        end
        format.xml  { render :xml => @user.errors, :status => :unprocessable_entity }
      end
    end
  end
  
  # DELETE /users/1/friends/2
  # DELETE /users/1/friends/2.xml
  def destroy
    @friend_action = params[:friend_action]
    begin
      @user = User.find(params[:user_id])
      @target = User.find(params[:id])
      
      case @friend_action
      when /^revoke/i
        confirmation_msg = "You've revoked your friendship request to #{@target.username}."
        error_msg = "You have not requested to be #{@target.username}'s friend."
      when /^reject/i
        confirmation_msg = "You rejected #{@target.username}'s friendship request."
        error_msg = "#{@target.username} did not request to be your friend."        
      else
        confirmation_msg = "You are no longer friends with #{@target.username}."
        error_msg = "You aren't friends with #{@target.username}."          
      end
      
      if @user.unfriend(@target)
        flash[:notice] = confirmation_msg
        expire_fragment ("events/friend_requests/#{@target.id}")
        decrement_friendship_requests if @friend_action =~ /^reject/i
        generate_friend_cloud @user.friends if @friend_action =~ /^remove/i
      else
        flash[:error] = error_msg
      end
    rescue ActiveRecord::RecordNotFound
      flash[:error] = "Invalid source or target user."
    end
    
    respond_to do |format|
      format.html { redirect_to(@user) }
      format.js { render :layout => false }
      format.xml { head :ok }
    end
  end
  
  
  
  private ####################################################################################
  
  def decrement_friendship_requests
    session[:friend_requests] = (session[:friend_requests].to_i - 1).to_s
  end
  
end

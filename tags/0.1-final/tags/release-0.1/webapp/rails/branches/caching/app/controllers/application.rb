# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  # Pick a unique cookie name to distinguish our session data from others'
  session :session_key => '_perf_session_id'
  
  def authorize
    begin
      @user = User.find(session[:user_id])
    rescue
      @user = nil
    end
    
    unless @user
      session[:original_uri] = request.request_uri
      flash[:error] = "You must log in before accessing that page."      
      redirect_to(root_path)
    end
  end
  
  def logged_in_as(user = nil)
    logged_in_as_user_id = false
    if !user.nil?
      if user.id == session[:user_id]
        logged_in_as_user_id = true
      end
    end
    user = nil
    logged_in_as_user_id
  end
  
  def generate_friend_cloud(all_friends)
    friends = all_friends.clone
    @friendcloud = []
    6.times do
      random_friend = rand(friends.size)
      @friendcloud << friends[random_friend] unless friends.empty?
      friends.delete_at(random_friend)
    end    
  end
    
  def validate_event
    begin
      @event = Event.find(params[:event_id])
    rescue ActiveRecord::RecordNotFound
      respond_to do |format|
        flash[:error] = "Event does not exist."
        format.html { redirect_to root_path }
      end
    end
  end
  
end

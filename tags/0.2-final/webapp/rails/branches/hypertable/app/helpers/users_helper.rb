module UsersHelper
  
  def display_name(user)
    (you? user) ? "Your" : "#{user.username}'s"
  end

  def search_results_header
    "Search Results for \"#{h @query}\""
  end
  
  def friendship_action(source, target)
    unless source == target
      if source.friends.include?(target)
        render :partial => 'friends/remove_link', :locals => { :user => source, :target => target }
      elsif @incoming_invite_ids.include?(target.id)
        out = render :partial => 'friends/approve_link', :locals => { :user => source, :target => target }
        out += render :partial => 'friends/reject_link', :locals => { :user => source, :target => target }
      elsif @outgoing_invite_ids.include?(target.id)
        render :partial => 'friends/revoke_link', :locals => { :user => source, :target => target }
      else
        render :partial => 'friends/add_link', :locals => { :user => target }
      end
    end
  end
  
end

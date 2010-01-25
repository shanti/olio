module FriendsHelper    

  def refresh_invites(which)
    page << "if($('#{which}_requests').getElementsBySelector('ol li').length == 0) {"
      page["#{which}_requests"].visual_effect :blind_up
    page << "}"
  end

end

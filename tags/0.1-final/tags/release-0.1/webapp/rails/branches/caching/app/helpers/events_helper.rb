module EventsHelper
  
  def attendance_links(event)
    links = ""
    if logged_in?
      attending = @attendees.find { |u| u.id == session[:user_id] }
      if attending
        links += form_remote_tag :url => unattend_event_path(event.id), :method => :post, :html => {:method => :post}
        links += submit_tag "Unattend"
        links += "</form>"
      else
        links += form_remote_tag :url => attend_event_path(event.id), :method => :post, :html => {:method => :post}
        links += submit_tag "Attend"
        links += "</form>"
      end
      
    end
    links += "\n"
  end
  
  def edit_delete_links(event)
    links = ""
    if logged_in? and event.user_id == session[:user_id]
      links += button_to 'Edit', edit_event_path(event), :method => :get
      links += " "
      links += button_to 'Delete', event, :confirm => 'Are you sure?', :method => :delete
    end
    links += "\n"
  end
  
  def created_at_radio_button
    if session[:order] == 'created_at'
      radio_button_tag 'order', 'created_at', true
    else
      radio_button_tag 'order', 'created_at', false
    end
  end
  
  def event_date_radio_button
    if session[:order] == 'event_date'
      radio_button_tag 'order', 'event_date', true
    else
      radio_button_tag 'order', 'event_date', false
    end
  end
  
  def zipcode_filter(zip)
    text_field_tag 'zipcode', "#{zip}"
  end
  
  def my_event?(event_id)
    event = Event.find_by_id(event_id)
    if !session[:user_id].nil? && event.user_id == session[:user_id]
      return true;
    else
      return false;
    end
  end
  
end
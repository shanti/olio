class EventSweeper < ActionController::Caching::Sweeper

  observe Event, Comment
  
  def after_create(record)
    expire_record(record)
  end

  def after_save(record)
    expire_record(record)
  end

  def after_destroy(record)
    expire_record(record)
  end
  
  private ############################################

  def expire_record(record)
    unless session.nil?
      expire_fragment(:controller => 'events', :action => 'index', :part => 'tag_cloud')
    end
    
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "event_description")
    
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "main_event_details")
    expire_fragment(:controller => "events", :action => "show", :id => record.id, :part => "main_event_details", :creator => true)
    
    expire_page(root_path)
  end

end


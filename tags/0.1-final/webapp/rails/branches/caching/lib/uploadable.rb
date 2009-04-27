module Uploadable
  
  def self.included(base)
    base.extend ClassMethods
  end
  
  # from attachment_fu, necessary for custom upload directory
  def full_filename(thumbnail = nil)
    file_system_path = (thumbnail ? thumbnail_class : self).attachment_options[:path_prefix].to_s
    File.join(RAILS_ROOT, file_system_path, thumbnail_name_for(thumbnail))
  end
  
  def individualize_filename!(id)
    self.filename = id.to_s + rand(Time.now.to_i).to_s + self.filename
  end
  
  module ClassMethods
    def make_from_upload(data, individualize_string)
      file = self.new(:uploaded_data => data)
      file.individualize_filename! individualize_string
      file.save
      file
    end 
  end
  
end
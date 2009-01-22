# == Schema Information
# Schema version: 17
#
# Table name: documents
#
#  id           :integer(11)     not null, primary key
#  size         :integer(11)     
#  content_type :string(255)     
#  filename     :string(255)     
#

class Document < ActiveRecord::Base
  
  has_attachment :content_type => ['application/pdf', 'application/msword', 'text/plain'],
                 :storage => :file_system, 
                 :path_prefix => DOCUMENT_STORE_PATH
  
  validates_uniqueness_of :filename
  validates_as_attachment
  include Uploadable
  
end

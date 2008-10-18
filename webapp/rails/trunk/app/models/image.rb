# == Schema Information
# Schema version: 17
#
# Table name: images
#
#  id           :integer(11)     not null, primary key
#  size         :integer(11)     
#  content_type :string(255)     
#  filename     :string(255)     
#  height       :integer(11)     
#  width        :integer(11)     
#  parent_id    :integer(11)     
#  thumbnail    :string(255)     
#

class Image < ActiveRecord::Base
  has_attachment :content_type => :image, 
                 :storage => :file_system, 
                 :max_size => 1.megabytes, 
                 :thumbnails => { :thumb => '250x250>' }, 
                 :path_prefix => IMAGE_STORE_PATH

  validates_uniqueness_of :filename
  validates_as_attachment
  
  include Uploadable
  
end

# == Schema Information
# Schema version: 17
#
# Table name: addresses
#
#  id        :integer(11)     not null, primary key
#  street1   :string(55)      
#  street2   :string(55)      
#  city      :string(55)      
#  state     :string(25)      
#  zip       :string(12)      
#  country   :string(55)      
#  latitude  :decimal(14, 10) 
#  longitude :decimal(14, 10) 
#

class Address < ActiveRecord::Base
  
  # Our schema doesn't have foreign keys that go this way,
  # so this is changed to has_one to fit Rails convention. -Hubert
  # belongs_to :user
  # belongs_to :event
  has_one :user
  has_one :event
  
  validates_presence_of :street1, :city, :zip, :country
  
end

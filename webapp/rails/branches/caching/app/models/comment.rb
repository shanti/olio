# == Schema Information
# Schema version: 17
#
# Table name: comments
#
#  id         :integer(11)     not null, primary key
#  user_id    :integer(11)     
#  event_id   :integer(11)     
#  rating     :integer(11)     
#  comment    :text            
#  created_at :datetime        
#  updated_at :datetime        
#

class Comment < ActiveRecord::Base
  
  belongs_to :user
  belongs_to :event
  
  validates_presence_of :user_id, :event_id, :comment, :rating
  validates_numericality_of :rating, :only_integer => true, :message => "must be an integer number"
  validates_inclusion_of :rating, :in => 0..5, :message => "must be between 0 and 5"
  
  def author
    self.user
  end
  
end

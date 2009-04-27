# == Schema Information
# Schema version: 17
#
# Table name: events
#
#  id              :integer(11)     not null, primary key
#  title           :string(100)     
#  description     :string(500)     
#  telephone       :string(20)      
#  user_id         :integer(11)     
#  address_id      :integer(11)     
#  image_id        :integer(11)     
#  document_id     :integer(11)     
#  timezone        :string(255)     
#  event_timestamp :datetime        
#  event_date      :date            
#  created_at      :datetime        
#  total_score     :integer(11)     
#  num_votes       :integer(11)     
#  disabled        :boolean(1)      
#

class Event < ActiveRecord::Base
  
  include Tagcount
  
  belongs_to :user
  belongs_to :address
  belongs_to :image 
  belongs_to :document 
  has_and_belongs_to_many :users
  has_many :comments, :dependent => :destroy
  acts_as_taggable
  
  validates_presence_of :title, :description, :telephone, :event_timestamp
  validates_length_of :telephone, :in => 7..20
  validates_length_of :title, :maximum => 100
  validates_length_of :description, :maximum => 500
  validates_format_of :telephone, 
                      :with => /^[\d\-]*$/,
                      :on => :save, 
                      :message => "must contain only 1-9 and/or -"
  
  after_save :add_creator_as_attendee
  
  def add_creator_as_attendee
    new_attendee(User.find(self.user_id))
  end
  
  def new_attendee(user)
    users << user if !users.include?(user)
  end
  
  def remove_attendee(user)
    users.delete(user) if users.include?(user)
  end
  
  def set_date
    self.event_date = self.event_timestamp.to_date.to_s #Date.parse(event_timestamp.to_s).to_s
  end
  
  def self.top_n_tags(n)
    tags_count(:limit => n, :order => 'count DESC').map{ |x| {:name => x.name, :count => x.count} }.sort { |x,y| x[:name] <=> y[:name] }
  end
  
  def self.tag_search(pattern)
    find_by_sql([
      "SELECT events.* FROM events, tags, taggings " +
      "WHERE events.id = taggings.taggable_id " +
      "AND taggings.taggable_type = 'Event' " +
      "AND taggings.tag_id = tags.id AND tags.name LIKE ?",
      pattern
    ])
  end
  
  def self.find_tagged_with(tags, options = {})
    options.update(:include => [:tags, :image], 
              :conditions => ['tags.name in (?) and taggable_type = ?', tags,
                              acts_as_taggable_options[:taggable_type]])
    self.find(:all, options)
  end
  
  def add_tags(tags)
    new_tags = Tag.parse(tags) - self.tags.map { |tag| tag.name }
    new_tags.each do |tag|
      Tag.find_or_create_by_name(tag).on(self)
    end
  end
    
  # Cleanup all associated records
  def destroy
    transaction do
      self.document.destroy if self.document
      self.image.destroy if self.image
      self.address.destroy
      self.users.clear
      super
    end
  end
  
  # Overload the image and document assignment methods to provide 
  # cascade destroy.
  alias set_image image=
  alias set_document document=
  
  # Associate an image with this event. Need to simulate dependent destroy
  def image=(image)
    self.image.destroy unless self.image.nil?
    set_image(image)
  end
  
  # Associate a document with this event
  def document=(document)
    self.document.destroy unless self.document.nil?
    self.set_document(document)
  end
  
end

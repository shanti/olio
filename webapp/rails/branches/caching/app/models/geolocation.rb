# +------------+--------------+------+-----+---------+----------------+
# | Field      | Type         | Null | Key | Default | Extra          |
# +------------+--------------+------+-----+---------+----------------+
# | id         | int(11)      | NO   | PRI | NULL    | auto_increment | 
# | zip        | int(11)      | YES  | MUL | NULL    |                | 
# | state_code | varchar(255) | YES  |     | NULL    |                | 
# | state      | varchar(255) | YES  |     | NULL    |                | 
# | city       | varchar(255) | YES  |     | NULL    |                | 
# | longitude  | float        | YES  |     | NULL    |                | 
# | latitude   | float        | YES  |     | NULL    |                | 
# | created_at | datetime     | YES  |     | NULL    |                | 
# | updated_at | datetime     | YES  |     | NULL    |                | 
class Geolocation < ActiveRecord::Base

  # For now, everything assumed is to be in the United States
  # Will more countries be added later?
  validates_presence_of :zip, :city
  validates_uniqueness_of :zip
  
  # As of now, zip code ranges from 1001 to 99950 in current database
  
  validates_numericality_of :latitude, :longitude
  validates_inclusion_of :latitude,
                         :in => -90..90,
                         :message => "must be between -90 and 90 degrees"
  validates_inclusion_of :longitude,
                         :in => -180..180,
                         :message => "must be between -180 and 180 degrees"

end

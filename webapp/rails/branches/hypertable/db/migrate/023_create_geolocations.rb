class CreateGeolocations < ActiveRecord::Migration
  def self.up
    create_table :geolocations do |t|
      t.integer :zip
      t.string :state_code
      t.string :state
      t.string :city
      t.float :longitude
      t.float :latitude

      t.timestamps
    end
    
    
    
  end

  def self.down
    drop_table :geolocations
  end
end

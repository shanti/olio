class CreateAddresses < ActiveRecord::Migration
  def self.up
    create_table :addresses do |t|
      t.column :street1, :string, :limit => 55
      t.column :street2, :string, :limit => 55
      t.column :city, :string, :limit => 55
      t.column :state, :string, :limit => 25
      t.column :zip, :string, :limit => 12
      t.column :country, :string, :limit => 55
      t.column :latitude, :decimal, :precision => 14, :scale => 10
      t.column :longitude, :decimal, :precision => 14, :scale => 10
    end
  end

  def self.down
    drop_table :addresses
  end
end

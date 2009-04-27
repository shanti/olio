class AddIndexes < ActiveRecord::Migration
  def self.up
    add_index :comments, :event_id
    add_index :geolocations, :zip
    add_index :invites, :user_id
    add_index :invites, :user_id_target
    add_index :users, :username
  end

  def self.down
    remove_index :users, :username
    remove_index :invites, :user_id_target
    remove_index :invites, :user_id
    remove_index :geolocations, :zip
    remove_index :comments, :event_id
  end
end

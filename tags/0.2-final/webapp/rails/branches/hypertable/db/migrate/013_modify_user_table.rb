class ModifyUserTable < ActiveRecord::Migration
  def self.up
    rename_column :users, :imageurl, :image_id
    change_column :users, :image_id, :integer
    
    remove_column :users, :imagethumburl
    remove_column :events, :thumbnail
  end

  def self.down
    rename_column :users, :image_id, :imageurl
    change_column :users, :imageurl, :string
    
    add_column :users, :imagethumburl, :string
    add_column :events, :thumbnail, :string
  end
end

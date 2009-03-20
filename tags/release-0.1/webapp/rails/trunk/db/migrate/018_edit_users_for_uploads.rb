class EditUsersForUploads < ActiveRecord::Migration
  def self.up
    add_column :users, :image_id, :integer
    add_column :users, :thumbnail, :integer
  end

  def self.down
    remove_column :users, :image_id
    remove_column :users, :thumbnail
  end
end

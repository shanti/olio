class RemoveOldUserImages < ActiveRecord::Migration
  def self.up
    remove_column :users, :image_id
  end

  def self.down
    add_column :users, :image_id, :integer
  end
end

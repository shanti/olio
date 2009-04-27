class AddEventThumbnails < ActiveRecord::Migration
  def self.up
    add_column :events, :thumbnail, :integer
  end

  def self.down
    remove_column :events, :thumbnail
  end
end

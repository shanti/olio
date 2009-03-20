class ShorterEventDescriptions < ActiveRecord::Migration
  def self.up
    change_column :events, :description, :string, :limit => 100
  end

  def self.down
    change_column :events, :description, :string, :limit => 500
  end
end

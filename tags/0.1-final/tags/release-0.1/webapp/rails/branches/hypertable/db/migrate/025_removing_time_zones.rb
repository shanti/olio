class RemovingTimeZones < ActiveRecord::Migration
  def self.up
    remove_column :events, :timezone
  end

  def self.down
    add_column :events, :timezone, :string
  end
end

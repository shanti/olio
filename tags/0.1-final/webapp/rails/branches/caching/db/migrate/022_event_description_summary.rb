class EventDescriptionSummary < ActiveRecord::Migration
  def self.up
    change_column :events, :description, :string, :limit => 500
    add_column :events, :summary, :string, :limit => 100
  end

  def self.down
    change_column :events, :description, :string, :limit => 100
    remove_column :events, :summary
  end
end

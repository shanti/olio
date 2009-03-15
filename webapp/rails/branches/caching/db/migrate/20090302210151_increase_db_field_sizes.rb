class IncreaseDbFieldSizes < ActiveRecord::Migration
  def self.up
    change_column :events, :description, :text
    change_column :events, :summary, :string, :limit => 1024
  end

  def self.down
    change_column :events, :description, :string, :limit =>  500   
    change_column :events, :summary, :string, :limit => 100
  end
end

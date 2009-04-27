class ChangeEventColumnTypes < ActiveRecord::Migration
  def self.up
    change_column :events, :image_id, :integer
    change_column :events, :thumbnail, :integer
    change_column :events, :document_id, :integer
  end

  def self.down
    change_column :events, :image_id, :string
    change_column :events, :thumbnail, :string
    change_column :events, :document_id, :string
  end
end

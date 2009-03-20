class CreateForeignKeys < ActiveRecord::Migration
  def self.up
    add_column :users, :address_id, :integer
  end

  def self.down
    remove_column :users, :address_id
  end
end

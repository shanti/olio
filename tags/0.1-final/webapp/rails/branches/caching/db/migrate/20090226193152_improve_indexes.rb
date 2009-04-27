class ImproveIndexes < ActiveRecord::Migration
  def self.up
    add_index :addresses, :zip
  end

  def self.down
    remove_index :addresses, :zip
  end
end

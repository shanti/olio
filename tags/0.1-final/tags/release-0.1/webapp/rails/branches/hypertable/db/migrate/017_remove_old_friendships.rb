class RemoveOldFriendships < ActiveRecord::Migration
  def self.up
    drop_table :friendships
  end

  def self.down
    create_table :friendships do |t|
      t.column :user_id, :integer
      t.column :friend_id, :integer
      t.column :authorized, :boolean, :default => false      
    end
  end
end

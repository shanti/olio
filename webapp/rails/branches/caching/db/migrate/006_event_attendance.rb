class EventAttendance < ActiveRecord::Migration
  def self.up
    create_table :events_users, :id => false do |t|
      t.column :event_id, :integer
      t.column :user_id, :integer
    end
    
    add_index :events_users, :event_id
    add_index :events_users, :user_id
  end

  def self.down
    drop_table :events_users
  end
end

class CreateComments < ActiveRecord::Migration
  def self.up
    drop_table :comments
    create_table :comments do |t|
      t.integer :user_id
      t.integer :event_id
      t.integer :rating
      t.text :comment

      t.timestamps
    end
  end

  def self.down
    drop_table :comments
    create_table :comments do |t|
      t.integer :user_id
      t.integer :event_id
      t.integer :rating
      t.text :comment
    end
  end
end

class CreateCommentsOld < ActiveRecord::Migration
  def self.up
    create_table :comments do |t|
      t.column :user_id, :integer
      t.column :event_id, :integer
      t.column :comment, :text
      t.column :rating, :integer
    end
  end

  def self.down
    drop_table :comments
  end
end

class CreateInvites < ActiveRecord::Migration
  def self.up
    create_table :invites do |t|
      t.column :user_id, :integer, :null => false           # source of the relationship
      t.column :user_id_target, :integer, :null => false    # target of the relationship
      t.column :is_accepted, :boolean                       # status of the friendship
    end
  end

  def self.down
    drop_table :invites
  end
end

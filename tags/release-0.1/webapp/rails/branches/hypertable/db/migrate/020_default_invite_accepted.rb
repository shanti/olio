class DefaultInviteAccepted < ActiveRecord::Migration
  def self.up
    change_column :invites, :is_accepted, :boolean, :default => false
  end

  def self.down
    change_column :invites, :is_accepted, :boolean, :default => nil  
  end
end

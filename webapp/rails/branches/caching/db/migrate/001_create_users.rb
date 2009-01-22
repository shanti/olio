class CreateUsers < ActiveRecord::Migration
  def self.up
    create_table :users do |t|
      t.column  :username, :string, :limit => 25
      t.column  :password, :string, :limit => 25
      t.column  :firstname, :string, :limit => 25
      t.column  :lastname, :string, :limit => 25
      t.column  :email, :string, :limit => 90
      t.column  :telephone, :string, :limit => 25
      t.column  :imageurl, :string, :limit => 100
      t.column  :imagethumburl, :string, :limit => 100
      t.column  :summary, :string, :limit => 2500
      t.column  :timezone, :string, :limit => 25
      t.column  :created_at, :timestamp
      t.column  :updated_at, :timestamp
    end
  end

  def self.down
    drop_table :users
  end
end

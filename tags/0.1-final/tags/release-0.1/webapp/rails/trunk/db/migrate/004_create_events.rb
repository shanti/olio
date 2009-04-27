class CreateEvents < ActiveRecord::Migration
  def self.up
    create_table :events, :force => true do |t|
      t.column :title, :string, :limit => 100
      t.column :description, :string, :limit => 500
      t.column :telephone, :string, :limit => 20

      # foreign keys
      t.column :user_id, :integer
      t.column :address_id, :integer

      # file-related
      t.column :image_url, :string
      t.column :image_thumb_url, :string
      t.column :literature_url, :string

      # time-related
      t.column :timezone, :string
      t.column :event_timestamp, :timestamp
      t.column :event_date, :date #redundant
      t.column :created_at, :timestamp 

      # other
      t.column :total_score, :integer
      t.column :num_votes, :integer      
      t.column :disabled, :boolean
    end
    
    add_index :events, :id #not present in PHP version
    add_index :events, :event_date
    add_index :events, :event_timestamp
    add_index :events, :created_at
    add_index :events, :user_id
  end

  def self.down
    remove_index :events, :user_id
    remove_index :events, :created_at
    remove_index :events, :event_timestamp
    remove_index :events, :event_date
    remove_index :events, :id
    
    drop_table :events
  end
end

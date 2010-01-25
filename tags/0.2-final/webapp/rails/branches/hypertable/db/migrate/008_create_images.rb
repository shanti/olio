class CreateImages < ActiveRecord::Migration
  def self.up
    create_table :images do |t|
      # general attachment_fu attributes
      t.column :size, :integer
      t.column :content_type, :string
      t.column :filename, :string    
      
      # image-specific attachment_fu attributes
      t.column :height, :integer
      t.column :width, :integer
      t.column :parent_id, :integer
      t.column :thumbnail, :string
    end
    
    add_index :images, :filename
    add_index :images, :thumbnail
  end

  def self.down
    drop_table :images
  end
end

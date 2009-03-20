class CreateDocuments < ActiveRecord::Migration
  def self.up
    create_table :documents do |t|
      # general attachment_fu attributes
      t.column :size, :integer
      t.column :content_type, :string
      t.column :filename, :string
    end
    
    add_index :documents, :filename
  end

  def self.down
    drop_table :documents
  end
end

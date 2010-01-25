class EditEventsForUploads < ActiveRecord::Migration
  def self.up
    rename_column :events, :image_url, :image_id
    rename_column :events, :literature_url, :document_id
    rename_column :events, :image_thumb_url, :thumbnail
  end

  def self.down
    rename_column :events, :image_id, :image_url
    rename_column :events, :document_id, :literature_url
    rename_column :events, :thumbnail, :image_thumb_url
  end
end

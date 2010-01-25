class PopulateGeolocations < ActiveRecord::Migration
  def self.up
    r = Rails::Configuration.new
    y = YAML::load_file(r.database_configuration_file)    
    if y[RAILS_ENV.to_s]["adapter"] == "mysql"
      system "mysql -u #{y[RAILS_ENV.to_s]["username"]} -D #{y[RAILS_ENV.to_s]["database"]} < ./db/migrate/geolocations.sql"
    #elsif y[RAILS_ENV.to_s]["adapter"] == "sqlite2"
      # Handle SQLite2
    #elsif y[RAILS_ENV.to_s]["adapter"] == "sqlite3"
      # Handle SQLite3
    #elsif y[RAILS_ENV.to_s]["adapter"] == "postgresql"
      # Handle PostgreSQL
    #elsif y[RAILS_ENV.to_s]["adapter"] == "oracle"
      # Handle Oracle
    else
      write "Connection adapter unsupported thus far. Geolocations have not been added to the database."
    end
  end

  def self.down
    execute "TRUNCATE geolocations"
  end
end
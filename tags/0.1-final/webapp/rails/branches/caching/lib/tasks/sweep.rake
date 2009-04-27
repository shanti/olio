desc "Delete all cached files"
task :sweep_cache => "tmp:cache:clear" do
  #file_pattern = File.join(RAILS_ROOT, "public", "events*")
  #FileUtils.rm_rf(Dir[file_pattern])
  FileUtils.rm_rf File.expand_path("public/index.html", RAILS_ROOT)
end

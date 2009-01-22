
namespace :svn do
  desc "Setup repo"
  task :setup do
  end
  
  desc "Import code into svn repository"
  task :import do
    new_path = Dir.pwd + "_svn≈"
    tags = repository.sub("trunk", "tags")
    branches = repository.sub("trunk", "branches")
    puts "Adding branches and tags"
    system "svn mkdir -m 'Adding tags and branches directories' #{tags} #{branches}"
    puts "Importing application."
    system "svn import \"#{repository}\" -m \"Initial Import\""
    puts "Checking out to new directory."
    system "svn co \"#{repository}\" \"#{new_path}\""
    cwd = Dir.getwd
    Dir.chdir new_path
    puts "removing log directory contents from svn"
    system "svn remove log/*"
    puts "ignoring log directory"
    system "svn propset svn:ignore '*.log' log/"
    system "svn update log/"
    puts "removing tmp directory from svn"
    system "svn remove tmp/"
    puts "ignoring tmp directory"
    system "svn propset svn:ignore '*' tmp/"
    system "svn update tmp/"
    puts "committing changes"
    system "svn commit -m \"Removed and ignored log files and tmp\""
    Dir.chdir cwd
    puts "Your repository is: #{repository}" 
    puts "Please change to your new working directory: #{new_path}"
  end
end

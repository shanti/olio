
namespace :apache do
  task :setup, :role => :web do
    machine = get_machine
    
    template = File.read(File.join('vendor', 'plugins', 'rorclassify', 'templates', "app.conf"))
    result = ERB.new(template).result(binding)
    
    put result, "/work/etc/apache2/#{machine}/#{application}.conf", :mode => 0644
  end

  task :restart, :role => :web do
    run "sudo apache2ctl restart"
  end
end


namespace :mysql do
  task :setup do
    # Get database names from database.yml
    conf = YAML.load(File.read('config/database.yml'))
    prod = conf['production']['database']
    test = conf['test']['database']
    prod_user = conf['production']['username'] || 'root'
    test_user = conf['test']['username'] || 'root'

    run <<-CMD
      mysqladmin -u #{prod_user} create #{prod} &&
      mysqladmin -u #{test_user} create #{test}
    CMD
  end
end

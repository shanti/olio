
class SeleniumController
  include FixtureReplacement
    
  def load_fixtures fixtures_param
    fixtures = fixtures_param.split(/\s*,\s*/).each do |f|
      self.send(f)
    end
  end
  
  def login_user
    create_bob
  end
  
  def users
    20.times do |i|
      create_user(:username => "User_#{i}")
    end
  end
  
  def events
    20.times do |i|
      address = create_address(:zip => 10000 + i)
      e = create_event(:title => "Event #{i}", 
                       :created_at => Time.now - i * 3600, 
                       :event_timestamp => Time.now - (20 - i) * 1.day, 
                       :address => address)
      e.tag_with("#{i},event_#{i/5}")
    end
  end
end

# Mirrors the actions specified in user-extensions.js from the selenium-core
module SeleniumOnRails::TestBuilderUserActions
 
 # Types the text twice into a text box.
 def type_tiny_mce locator, text
   wait_for_condition "selenium.browserbot.getCurrentWindow().tinyMCE.getInstanceById(\"#{locator}\").getBody().innerHTML = \"#{text}\"; " +
     'true', 10000
 end
 
 private
   
 # Generates the corresponding +_and_wait+ for each action.
 def self.generate_and_wait_actions
   public_instance_methods.each do |method|
     define_method method + '_and_wait' do |*args|
       make_command_waiting do
         send method, *args
       end
     end
   end
 end

 generate_and_wait_actions
 
end
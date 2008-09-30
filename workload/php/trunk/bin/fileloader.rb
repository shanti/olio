#!/opt/coolstack/bin/ruby
##############################################################
#  Copyright ?? 2008 Sun Microsystems, Inc. All rights reserved
#
#  Use is subject to license terms.
#
#  $Id: fileloader.rb,v 1.1.1.1 2008/09/29 22:33:07 sp208304 Exp $
##############################################################

 if ARGV.size != 1 then
  puts "Usage: fileloader.rb <scale> \nPlease setup the $FABAN_HOME environment variable before running the command.\n"
  exit 1
 end

e = 2.7182818
scale = ARGV[0].to_i

num_users = scale * 4
e_power = num_users/-10000.0
chl_prob = (1.0 - e**e_power) / (1.0 + e**e_power)
num_events = (15000 * chl_prob + 0.5).ceil
num_events = num_events.ceil

sets_to_load_into_mogile =
 {
 "Users" => num_users,
 "Events" => num_events
 }

sets_to_load_into_mogile.keys.each { |set|
 #puts "key is #{set} and value is #{sets_to_load_into_mogile[set]}"
if set == "Users"  then 
	puts "---1 Copying for Persons. . ."
	count = 1
	puts "range is #{count} to #{num_users}"  
	num_users.times do
		puts "/copying for person #{count}\n"
		exec "/usr/bin/cp $FABAN_HOME/benchmarks/Web20Driver/resources/person.jpg p#{count}.jpg;
			/usr/bin/cp $FABAN_HOME/benchmarks/Web20Driver/resources/person_thumb.jpg p#{count}t.jpg" if fork.nil?
		count = count + 1
	end
	
else 
	puts "---2 Copying for Events. . ."
	count = 1
	puts "range is #{count} to #{num_events}"  
	num_events.times do
		puts "/copying for event #{count}\n"
		exec "/usr/bin/cp $FABAN_HOME/benchmarks/Web20Driver/resources/event.jpg e#{count}.jpg;
			/usr/bin/cp $FABAN_HOME/benchmarks/Web20Driver/resources/event_thumb.jpg e#{count}t.jpg;
			/usr/bin/cp $FABAN_HOME/benchmarks/Web20Driver/resources/event.pdf e#{count}1.pdf" if fork.nil?
		count = count + 1
	end
end 
}

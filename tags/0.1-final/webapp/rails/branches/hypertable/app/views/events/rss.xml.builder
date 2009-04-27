xml.instruct! :xml, :version=>"1.0" 

xml.rss "version" => "2.0", "xmlns:dc" => "http://purl.org/dc/elements/1.1/" do
  xml.channel do
    xml.title("Sun Performance App RSS Feed") 
    xml.description("An RSS feed of upcoming events.") 
    xml.link("http://perf.rorclass.org")
    xml.updated @events.first.created_at.strftime "%Y-%m-%dT%H:%M:%SZ" if @events.any?
    xml.author "Sun Microsystems / RADLab"
    xml.language "en-us"
    
    for event in @events
      link = 
      xml.item do 
        xml.title(event.title) 
        xml.description(event.description)
        xml.author("#{event.user.firstname} #{event.user.lastname}")
        xml.pubDate(event.created_at.strftime("%a, %d %b %Y %H:%M:%S %z"))
        xml.link("http://" + request.host_with_port + url_for(:controller => '/events', :action => 'show', :id => event.id))
        xml.guid("http://" + request.host_with_port + url_for(:controller => '/events', :action => 'show', :id => event.id))        
      end 
    end 
  end
end



# 
# xml.instruct!
# 
# xml.feed "xmlns" => "http://www.w3.org/2005/Atom" do
# 
#   xml.title   "Feed Name"
#   xml.link    "rel" => "self", "href" => url_for(:only_path => false, :controller => 'feeds', :action => 'atom')
#   xml.link    "rel" => "alternate", "href" => url_for(:only_path => false, :controller => 'posts')
#   xml.id      url_for(:only_path => false, :controller => 'posts')
#   xml.updated @posts.first.updated_at.strftime "%Y-%m-%dT%H:%M:%SZ" if @posts.any?
#   xml.author  { xml.name "Author Name" }
# 
#   @posts.each do |post|
#     xml.entry do
#       xml.title   post.title
#       xml.link    "rel" => "alternate", "href" => url_for(:only_path => false, :controller => 'posts', :action => 'show', :id => post.id)
#       xml.id      url_for(:only_path => false, :controller => 'posts', :action => 'show', :id => post.id)
#       xml.updated post.updated_at.strftime "%Y-%m-%dT%H:%M:%SZ"
#       xml.author  { xml.name post.author.name }
#       xml.summary "Post summary"
#       xml.content "type" => "html" do
#         xml.text! render(:partial => "posts/post", :post => post)
#       end
#     end
#   end
# 
# end

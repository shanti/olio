ActionController::Routing::Routes.draw do |map|
  
  map.resources :events, :collection => { :rss => :get, 
                                          :tagged => :get,
                                          :update_calendar => :get,
                                          :tag_search => :get }
                                          
  map.resources :events, :member => { :attend => :post, 
                                      :unattend => :post,
                                      :tag => :put }
  map.resources :events do |event|
      event.resources :comments, :member => { :delete => :get }
  end
  map.resources :events
  
  map.resources(:users,:collection => {  :login => :any, 
                                         :logout => :get,
                                         :check_name => :post,
                                         :search => :get }) do |user|
    user.resources :friends
  end
  
  map.resources :users, :member => {  :upcoming_events => :get,
                                      :posted_events => :get }
  
  # The priority is based upon order of creation: first created -> highest priority.

  # Sample of regular route:
  #   map.connect 'products/:id', :controller => 'catalog', :action => 'view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   map.purchase 'products/:id/purchase', :controller => 'catalog', :action => 'purchase'
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   map.resources :products

  # Sample resource route with options:
  #   map.resources :products, :member => { :short => :get, :toggle => :post }, :collection => { :sold => :get }

  # Sample resource route with sub-resources:
  #   map.resources :products, :has_many => [ :comments, :sales ], :has_one => :seller

  # Sample resource route within a namespace:
  #   map.namespace :admin do |admin|
  #     # Directs /admin/products/* to Admin::ProductsController (app/controllers/admin/products_controller.rb)
  #     admin.resources :products
  #   end
  
  # You can have the root of your site routed with map.root -- just remember to delete public/index.html.
  map.root :controller => "events"
  
  # See how all your routes lay out with "rake routes"
  
  # Install the default route as the lowest priority.
  #map.connect ':controller/:action/:id.:format'
  #map.connect ':controller/:action/:id'
  
end

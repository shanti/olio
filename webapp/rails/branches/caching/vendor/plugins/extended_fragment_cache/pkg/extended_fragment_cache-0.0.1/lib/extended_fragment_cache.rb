# In-Process Memory Cache for Fragment Caching
#
# Fragment caching has a slight inefficiency that requires two lookups 
# within the fragment cache store to render a single cached fragment.  
# The two cache lookups are:
#
# 1. The read_fragment method invoked in a controller to determine if a 
#    fragment has already been cached. e.g., 
#      unless read_fragment("/x/y/z")
#       ...
#      end
# 2. The cache helper method invoked in a view that renders the fragment. e.g., 
#      <% cache("/x/y/z") do %>
#        ...
#      <% end %>
#
# This plugin adds an in-process cache that saves the value retrieved from
# the fragment cache store.  The in-process cache has two benefits:
#
# 1. It cuts in half the number of read requests sent to the fragment cache
#    store.  This can result in a considerable saving for sites that make
#    heavy use of memcached.
# 2. Retrieving the fragment from the in-process cache is faster than going
#    to fragment cache store.  On a typical dev box, the savings are
#    relatively small but would be noticeable in standard production 
#    environment using memcached (where the fragment cache could be remote)
#
# Peter Zaitsev has a great post comparing the latencies of different
# cache types on the MySQL Performance blog:
# http://www.mysqlperformanceblog.com/2006/08/09/cache-performance-comparison/
#
# The plugin automatically installs a before_filter on the 
# ApplicationController that flushes the in-process memory cache at the 
# start of every request.

module ActionController
  module Caching
    module ExtendedFragments
      # Add a local_fragment_cache object and accessor.
      def self.append_features(base) #:nodoc:
        super
        base.class_eval do
          @@local_fragment_cache = {}
          cattr_accessor :local_fragment_cache
        end

        # add a before filter to flush the local cache before every request
        base.before_filter({}) do |c|
          @@local_fragment_cache.clear
        end
      end
    end

    module Fragments
      # Override read_fragment so that it checks the local_fragment_cache
      # object before going to the fragment_cache_store backend.
      def read_fragment(name, options = nil)
        return unless perform_caching

        key = fragment_cache_key(name)
        self.class.benchmark "Fragment read: #{key}" do
          content = ApplicationController.local_fragment_cache[key]
          if content.nil?
            content = fragment_cache_store.read(key, options)
            ApplicationController.local_fragment_cache[key] = content
          end
          content
        end
      end

      def write_fragment(name, content, options = nil)
        return unless perform_caching

        key = fragment_cache_key(name)
        self.class.benchmark "Cached fragment: #{key}" do
          ApplicationController.local_fragment_cache[key] = content
          fragment_cache_store.write(key, content, options)
        end
        content
      end
    end
  end
end

# Content Interpolation for Fragment Caching
#
# Many modern websites mix a lot of static and dynamic content.  The more
# dynamic content you have in your site, the harder it becomes to implement
# caching.  In an effort to scale, you've implemented fragment caching
# all over the place.  Fragment caching can be difficult if your static content
# is interleaved with your dynamic content.  Your views become littered
# with cache calls which not only hurts performance (multiple calls to the
# cache backend), it also makes them harder to read.  Content 
# interpolation allows you substitude dynamic content into cached fragment.
#
# Take this example view:
# <% cache("/first_part") do %>
#   This content is very expensive to generate, so let's fragment cache it.<br/>
# <% end %>
# <%= Time.now %><br/>
# <% cache("/second_part") do %>
#   This content is also very expensive to generate.<br/>
# <% end %>
#
# We can replace it with:
# <% cache("/only_part", {}, {"__TIME_GOES_HERE__" => Time.now}) do %>
#   This content is very expensive to generate, so let's fragment cache it.<br/>
#   __TIME_GOES_HERE__<br/>
#   This content is also very expensive to generate.<br/>
# <% end %>
#
# The latter is easier to read and induces less load on the cache backend.
#
# We use content interpolation at Zvents to speed up our JSON methods.
# Converting objects to JSON representation is notoriously slow.  
# Unfortunately, in our application, each JSON request must return some unique
# data.  This makes caching tedious because 99% of the content returned is
# static for a given object, but there's a little bit of dynamic data that
# must be sent back in the response.  Using content interpolation, we cache
# the object in JSON format and substitue the dynamic values in the view.
# 
# This plugin integrates Yan Pritzker's extension that allows content to be 
# cached with an expiry time (from the memcache_fragments plugin) since they 
# both operate on the same method.  This allows you to do things like:
#
# <% cache("/only_part", {:expire => 15.minutes}) do %>
#   This content is very expensive to generate, so let's fragment cache it.
# <% end %>

module ActionView
  module Helpers
    # See ActionController::Caching::Fragments for usage instructions.
    module CacheHelper
      def cache(name = {}, options=nil, interpolation = {}, &block)
        begin
          content = @controller.cache_erb_fragment(block, name, options, interpolation) || ""
        rescue
          content = ""
        rescue MemCache::MemCacheError => err
          content = ""
        end

        interpolation.keys.each{|k| content.sub!(k.to_s, interpolation[k].to_s)}
        content
      end
    end
  end
end

module ActionController
  module Caching
    module Fragments
      # Called by CacheHelper#cache
      def cache_erb_fragment(block, name={}, options=nil, interpolation={})
        unless perform_caching then 
          content = block.call
          interpolation.keys.each{|k|content.sub!(k.to_s,interpolation[k].to_s)}
          content
          return 
        end

        buffer = eval("_erbout", block.binding)

        if cache = read_fragment(name, options)
          buffer.concat(cache)
        else
          pos = buffer.length
          block.call
          write_fragment(name, buffer[pos..-1], options)
          interpolation.keys.each{|k|
            buffer[pos..-1] = buffer[pos..-1].sub!(k.to_s,interpolation[k].to_s)
          }
          buffer[pos..-1]
        end
      end
    end
  end
end

class MemCache
  # The read and write methods are required to get fragment caching to 
  # work with the Robot Co-op memcache_client code.
  # http://rubyforge.org/projects/rctools/
  #
  # Lifted shamelessly from Yan Pritzker's memcache_fragments plugin.
  # This should really go back into the memcache_client core.
  # http://skwpspace.com/2006/08/19/rails-fragment-cache-with-memcached-client-and-time-based-expire-option/
  def read(key,options=nil)    
    begin
      get(key)
    rescue 
      ActiveRecord::Base.logger.error("MemCache Error: #{$!}")      
      return false
    rescue MemCache::MemCacheError => err
      ActiveRecord::Base.logger.error("MemCache Error: #{$!}")      
      return false
    end
  end
  
  def write(key,content,options=nil)
    expiry = options && options[:expire] || 0
    begin
      set(key,content,expiry)
    rescue 
      ActiveRecord::Base.logger.error("MemCache Error: #{$!}")      
    rescue MemCache::MemCacheError => err
      ActiveRecord::Base.logger.error("MemCache Error: #{$!}")      
    end
  end
end

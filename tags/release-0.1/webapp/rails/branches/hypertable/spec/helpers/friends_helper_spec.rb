require File.dirname(__FILE__) + '/../spec_helper'

# Taken from http://www.mintsource.org/2007/9/3/rspec-rjs-helper-specs -Hubert
module RJSSpecHelper
  class HelperRJSPageProxy
    def initialize(context)
      @context = context
    end
  
    def method_missing(method, *arguments)
      block = Proc.new { |page|  @lines = []; page.send(method, *arguments) }
      @context.response.body = ActionView::Helpers::PrototypeHelper::JavaScriptGenerator.new(@context, &block).to_s
      @context.response
    end
  end

  def rjs_for
    HelperRJSPageProxy.new(self)
  end
end

include RJSSpecHelper

describe FriendsHelper do
  include FriendsHelper
  
  it "should test refresh invites" do
    rjs_for.refresh_invites("something").body.should == "if($('something_requests').getElementsBySelector('ol li').length == 0) {\n$(\"something_requests\").visualEffect(\"blind_up\");\n}"
  end
  
end
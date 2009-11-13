#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
require File.dirname(__FILE__) + '/../../spec_helper'

class CookiesProxyExamplesController < ActionController::Base
  def index
    cookies[:key] = cookies[:key]
  end
end

module Spec
  module Rails
    module Example
      describe CookiesProxy, :type => :controller do
        controller_name :cookies_proxy_examples
      
        describe "with a String key" do
        
          it "should accept a String value" do
            cookies = CookiesProxy.new(self)
            cookies['key'] = 'value'
            get :index
            cookies['key'].should == ['value']
          end
          
          if Rails::VERSION::STRING >= "2.0.0"
            it "should accept a Hash value" do
              cookies = CookiesProxy.new(self)
              cookies['key'] = { :value => 'value', :expires => expiration = 1.hour.from_now, :path => path = '/path' }
              get :index
              cookies['key'].should == ['value']
              cookies['key'].value.should == ['value']
              cookies['key'].expires.should == expiration
              cookies['key'].path.should == path
            end
          end
            
        end
      
        describe "with a Symbol key" do
        
          it "should accept a String value" do
            example_cookies = CookiesProxy.new(self)
            example_cookies[:key] = 'value'
            get :index
            example_cookies[:key].should == ['value']
          end

          if Rails::VERSION::STRING >= "2.0.0"
            it "should accept a Hash value" do
              example_cookies = CookiesProxy.new(self)
              example_cookies[:key] = { :value => 'value', :expires => expiration = 1.hour.from_now, :path => path = '/path' }
              get :index
              example_cookies[:key].should == ['value']
              example_cookies[:key].value.should == ['value']
              example_cookies[:key].expires.should == expiration
              example_cookies[:key].path.should == path
            end
          end

        end
    
        describe "#delete" do
          it "should delete from the response cookies" do
            example_cookies = CookiesProxy.new(self)
            response_cookies = mock('cookies')
            response.should_receive(:cookies).and_return(response_cookies)
            response_cookies.should_receive(:delete).with('key')
            example_cookies.delete :key
          end
        end
      end
    
    end
  end
end

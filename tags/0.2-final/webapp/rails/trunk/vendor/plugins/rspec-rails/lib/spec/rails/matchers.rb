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
dir = File.dirname(__FILE__)
require 'spec/rails/matchers/assert_select'
require 'spec/rails/matchers/change'
require 'spec/rails/matchers/have_text'
require 'spec/rails/matchers/include_text'
require 'spec/rails/matchers/redirect_to'
require 'spec/rails/matchers/render_template'

module Spec
  module Rails
    # Spec::Rails::Expectations::Matchers provides several expectation matchers
    # intended to work with Rails components like models and responses. For example:
    #
    #   response.should redirect_to("some/url") #redirect_to(url) is the matcher.
    #
    # In addition to those you see below, the arbitrary predicate feature of RSpec
    # makes the following available as well:
    #
    #   response.should be_success #passes if response.success?
    #   response.should be_redirect #passes if response.redirect?
    #
    # Note that many of these matchers are part of a wrapper of <tt>assert_select</tt>, so
    # the documentation comes straight from that with some slight modifications.
    # <tt>assert_select</tt> is a Test::Unit extension originally contributed to the
    # Rails community as a plugin by Assaf Arkin and eventually shipped as part of Rails.
    #
    # For more info on <tt>assert_select</tt>, see the relevant Rails documentation.
    module Matchers
    end
  end
end

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
require File.dirname(__FILE__) + '/../../../spec_helper.rb'
require 'spec/runner/formatter/snippet_extractor'

module Spec
  module Runner
    module Formatter
      describe SnippetExtractor do
        it "should fall back on a default message when it doesn't understand a line" do
          SnippetExtractor.new.snippet_for("blech").should == ["# Couldn't get snippet for blech", 1]
        end

        it "should fall back on a default message when it doesn't find the file" do
          SnippetExtractor.new.lines_around("blech", 8).should == "# Couldn't get snippet for blech"
        end
      end
    end
  end
end
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
require File.dirname(__FILE__) + '/../../spec_helper.rb'

module Spec
  module Mocks
    module ArgumentConstraints
      describe HashIncludingConstraint do
        
        it "should describe itself properly" do
          HashIncludingConstraint.new(:a => 1).description.should == "hash_including(:a=>1)"
        end      

        describe "passing" do
          it "should match the same hash" do
            hash_including(:a => 1).should == {:a => 1}
          end

          it "should match a hash with extra stuff" do
            hash_including(:a => 1).should == {:a => 1, :b => 2}
          end
          
          describe "when matching against other constraints" do
            it "should match an int against anything()" do
              hash_including(:a => anything, :b => 2).should == {:a => 1, :b => 2}
            end

            it "should match a string against anything()" do
              hash_including(:a => anything, :b => 2).should == {:a => "1", :b => 2}
            end
          end
        end
        
        describe "failing" do
          it "should not match a non-hash" do
            hash_including(:a => 1).should_not == 1
          end


          it "should not match a hash with a missing key" do
            hash_including(:a => 1).should_not == {:b => 2}
          end

          it "should not match a hash with an incorrect value" do
            hash_including(:a => 1, :b => 2).should_not == {:a => 1, :b => 3}
          end

          it "should not match when values are nil but keys are different" do
            hash_including(:a => nil).should_not == {:b => nil}
          end
        end
      end
    end
  end
end

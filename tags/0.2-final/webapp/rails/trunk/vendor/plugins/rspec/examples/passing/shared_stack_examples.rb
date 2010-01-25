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
shared_examples_for "non-empty Stack" do

  it { @stack.should_not be_empty }
  
  it "should return the top item when sent #peek" do
    @stack.peek.should == @last_item_added
  end
  
  it "should NOT remove the top item when sent #peek" do
    @stack.peek.should == @last_item_added
    @stack.peek.should == @last_item_added
  end
  
  it "should return the top item when sent #pop" do
    @stack.pop.should == @last_item_added
  end
  
  it "should remove the top item when sent #pop" do
    @stack.pop.should == @last_item_added
    unless @stack.empty?
      @stack.pop.should_not == @last_item_added
    end
  end
  
end

shared_examples_for "non-full Stack" do

  it { @stack.should_not be_full }

  it "should add to the top when sent #push" do
    @stack.push "newly added top item"
    @stack.peek.should == "newly added top item"
  end

end
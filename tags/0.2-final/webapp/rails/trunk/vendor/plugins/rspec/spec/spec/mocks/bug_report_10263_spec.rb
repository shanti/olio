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
describe "Mock" do
  before do
    @mock = mock("test mock")
  end
  
  specify "when one example has an expectation (non-mock) inside the block passed to the mock" do
    @mock.should_receive(:msg) do |b|
      b.should be_true #this call exposes the problem
    end
    @mock.msg(false) rescue nil
  end
  
  specify "then the next example should behave as expected instead of saying" do
    @mock.should_receive(:foobar)
    @mock.foobar
    @mock.rspec_verify
    begin
      @mock.foobar
    rescue Exception => e
      e.message.should == "Mock 'test mock' received unexpected message :foobar with (no args)"
    end
  end 
end


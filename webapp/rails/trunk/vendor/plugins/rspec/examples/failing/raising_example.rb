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
describe "This example" do
  
  it "should show that a NoMethodError is raised but an Exception was expected" do
    proc { ''.nonexistent_method }.should raise_error
  end
  
  it "should pass" do
    proc { ''.nonexistent_method }.should raise_error(NoMethodError)
  end
  
  it "should show that a NoMethodError is raised but a SyntaxError was expected" do
    proc { ''.nonexistent_method }.should raise_error(SyntaxError)
  end
  
  it "should show that nothing is raised when SyntaxError was expected" do
    proc { }.should raise_error(SyntaxError)
  end

  it "should show that a NoMethodError is raised but a Exception was expected" do
    proc { ''.nonexistent_method }.should_not raise_error
  end
  
  it "should show that a NoMethodError is raised" do
    proc { ''.nonexistent_method }.should_not raise_error(NoMethodError)
  end
  
  it "should also pass" do
    proc { ''.nonexistent_method }.should_not raise_error(SyntaxError)
  end
  
  it "should show that a NoMethodError is raised when nothing expected" do
    proc { ''.nonexistent_method }.should_not raise_error(Exception)
  end
  
  it "should show that the wrong message was received" do
    proc { raise StandardError.new("what is an enterprise?") }.should raise_error(StandardError, "not this")
  end
  
  it "should show that the unexpected error/message was thrown" do
    proc { raise StandardError.new("abc") }.should_not raise_error(StandardError, "abc")
  end
  
  it "should pass too" do
    proc { raise StandardError.new("abc") }.should_not raise_error(StandardError, "xyz")
  end
  
end

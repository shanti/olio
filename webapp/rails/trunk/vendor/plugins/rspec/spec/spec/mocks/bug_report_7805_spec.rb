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

module Bug7805
  #This is really a duplicate of 8302

  describe "Stubs should correctly restore module methods" do
    it "1 - stub the open method" do
      File.stub!(:open).and_return("something")
      File.open.should == "something"
    end
    it "2 - use File.open to create example.txt" do
      filename = "#{File.dirname(__FILE__)}/example-#{Time.new.to_i}.txt"
      File.exist?(filename).should be_false
      file = File.open(filename,'w')
      file.close
      File.exist?(filename).should be_true
      File.delete(filename)
      File.exist?(filename).should be_false
    end
  end

end

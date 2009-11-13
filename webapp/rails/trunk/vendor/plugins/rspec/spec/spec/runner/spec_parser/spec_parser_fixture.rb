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

describe "c" do

  it "1" do
  end

  it "2" do
  end

end

describe "d" do

  it "3" do
  end

  it "4" do
  end

end

class SpecParserSubject
end

describe SpecParserSubject do

  it "5" do
  end

end

describe SpecParserSubject, "described" do

  it "6" do
  end

end

describe SpecParserSubject, "described", :something => :something_else do

   it "7" do
   end

end

describe "described", :something => :something_else do

  it "8" do
  end

end

describe "e" do

  it "9" do
  end

  it "10" do
  end

  describe "f" do
    it "11" do
    end

    it "12" do
    end
  end

end

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
describe "Running specs with --diff" do
  it "should print diff of different strings" do
    uk = <<-EOF
RSpec is a
behaviour driven development
framework for Ruby
EOF
    usa = <<-EOF
RSpec is a
behavior driven development
framework for Ruby
EOF
    usa.should == uk
  end

  class Animal
    def initialize(name,species)
      @name,@species = name,species
    end

    def inspect
      <<-EOA
<Animal
name=#{@name},
species=#{@species}
>
      EOA
    end
  end

  it "should print diff of different objects' pretty representation" do
    expected = Animal.new "bob", "giraffe"
    actual   = Animal.new "bob", "tortoise"
    expected.should eql(actual)
  end
end

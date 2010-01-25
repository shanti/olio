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
require File.dirname(__FILE__) + '/spec_helper'

describe "arrays" do
  def contain_same_elements_as(expected)
    simple_matcher "array with same elements in any order as #{expected.inspect}" do |actual|
      if actual.size == expected.size
        a, e = actual.dup, expected.dup
        until e.empty? do
          if i = a.index(e.pop) then a.delete_at(i) end
        end
        a.empty?
      else
        false
      end
    end
  end
  
  describe "can be matched by their contents disregarding order" do
    subject { [1,2,2,3] }
    it { should contain_same_elements_as([1,2,2,3]) }
    it { should contain_same_elements_as([2,3,2,1]) }
    it { should_not contain_same_elements_as([3,3,2,1]) }
  end
  
  describe "fail the match with different contents" do
    subject { [1,2,3] }
    it { should_not contain_same_elements_as([2,3,4])}
    it { should_not contain_same_elements_as([1,2,2,3])}
    it { should_not contain_same_elements_as([1,2])}
  end
end
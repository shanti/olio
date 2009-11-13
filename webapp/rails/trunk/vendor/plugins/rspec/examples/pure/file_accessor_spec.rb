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
require File.dirname(__FILE__) + '/file_accessor'
require 'stringio'

describe "A FileAccessor" do
  # This sequence diagram illustrates what this spec specifies.
  #
  #                  +--------------+     +----------+     +-------------+
  #                  | FileAccessor |     | Pathname |     | IoProcessor |
  #                  +--------------+     +----------+     +-------------+
  #                         |                  |                  |
  #   open_and_handle_with  |                  |                  |
  #   -------------------->| |           open  |                  |
  #                        | |--------------->| |                 |
  #                        | | io             | |                 |
  #                        | |<...............| |                 |
  #                        | |                 |     process(io)  |
  #                        | |---------------------------------->| |
  #                        | |                 |                 | |
  #                        | |<..................................| |
  #                         |                  |                  |
  #
  it "should open a file and pass it to the processor's process method" do
    # This is the primary actor
    accessor = FileAccessor.new

    # These are the primary actor's neighbours, which we mock.
    file = mock "Pathname"
    io_processor = mock "IoProcessor"
    
    io = StringIO.new "whatever"
    file.should_receive(:open).and_yield io
    io_processor.should_receive(:process).with(io)
    
    accessor.open_and_handle_with(file, io_processor)
  end

end

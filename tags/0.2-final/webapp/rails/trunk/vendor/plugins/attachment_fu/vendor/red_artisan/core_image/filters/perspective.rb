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
module RedArtisan
  module CoreImage
    module Filters
      module Perspective
        
        def perspective(top_left, top_right, bottom_left, bottom_right)
          create_core_image_context(@original.extent.size.width, @original.extent.size.height)
          
          @original.perspective_transform :inputTopLeft => top_left, :inputTopRight => top_right, :inputBottomLeft => bottom_left, :inputBottomRight => bottom_right do |transformed|
            @target = transformed
          end
        end

        def perspective_tiled(top_left, top_right, bottom_left, bottom_right)
          create_core_image_context(@original.extent.size.width, @original.extent.size.height)
          
          @original.perspective_tile :inputTopLeft => top_left, :inputTopRight => top_right, :inputBottomLeft => bottom_left, :inputBottomRight => bottom_right do |tiled|
            @target = tiled
          end
        end
        
      end
    end
  end
end
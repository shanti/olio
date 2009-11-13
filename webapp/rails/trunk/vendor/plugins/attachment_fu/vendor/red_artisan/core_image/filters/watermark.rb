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
      module Watermark
        
        def watermark(watermark_image, tile = false, strength = 0.1)
          create_core_image_context(@original.extent.size.width, @original.extent.size.height)
          
          if watermark_image.respond_to? :to_str
            watermark_image = OSX::CIImage.from(watermark_image.to_str)
          end
          
          if tile
            tile_transform = OSX::NSAffineTransform.transform
            tile_transform.scaleXBy_yBy 1.0, 1.0
            
            watermark_image.affine_tile :inputTransform => tile_transform do |tiled|
              tiled.crop :inputRectangle => vector(0, 0, @original.extent.size.width, @original.extent.size.height) do |tiled_watermark|
                watermark_image = tiled_watermark
              end
            end
          end
          
          @original.dissolve_transition :inputTargetImage => watermark_image, :inputTime => strength do |watermarked|
            @target = watermarked
          end
        end

      end
    end
  end
end
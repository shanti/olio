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
      module Scale
        
        def resize(width, height)
          create_core_image_context(width, height)

          scale_x, scale_y = scale(width, height)

          @original.affine_clamp :inputTransform => OSX::NSAffineTransform.transform do |clamped|
            clamped.lanczos_scale_transform :inputScale => scale_x > scale_y ? scale_x : scale_y, :inputAspectRatio => scale_x / scale_y do |scaled|
              scaled.crop :inputRectangle => vector(0, 0, width, height) do |cropped|
                @target = cropped
              end
            end
          end
        end

        def thumbnail(width, height)
          create_core_image_context(width, height)

          transform = OSX::NSAffineTransform.transform
          transform.scaleXBy_yBy *scale(width, height)

          @original.affine_transform :inputTransform => transform do |scaled|
            @target = scaled
          end
        end

        def fit(size)
          original_size = @original.extent.size
          scale = size.to_f / (original_size.width > original_size.height ? original_size.width : original_size.height)
          resize (original_size.width * scale).to_i, (original_size.height * scale).to_i
        end
        
        private
        
          def scale(width, height)
            original_size = @original.extent.size
            return width.to_f / original_size.width.to_f, height.to_f / original_size.height.to_f
          end
          
      end
    end
  end
end
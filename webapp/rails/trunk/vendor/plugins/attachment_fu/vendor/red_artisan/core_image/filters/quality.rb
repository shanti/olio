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
      module Quality
        
        def reduce_noise(level = 0.02, sharpness = 0.4)
          create_core_image_context(@original.extent.size.width, @original.extent.size.height)
          
          @original.noise_reduction :inputNoiseLevel => level, :inputSharpness => sharpness do |noise_reduced|
            @target = noise_reduced
          end
        end
        
        def adjust_exposure(input_ev = 0.5)
          create_core_image_context(@original.extent.size.width, @original.extent.size.height)
          
          @original.exposure_adjust :inputEV => input_ev do |adjusted|
            @target = adjusted
          end          
        end
        
      end
    end
  end
end
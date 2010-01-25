<?php
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
    
class ImageUtil
{

    static function createThumb($name,$thumbname,$new_w,$new_h){
        $system=explode('.',$name);
        $ext=$system[1];
        if (preg_match('/jpg|jpeg/',$ext)){
            $src_img=imagecreatefromjpeg($name);
            $img_type="jpg";
        } else if (preg_match('/png/',$ext)){
            $src_img=imagecreatefrompng($name);
            $img_type="png";
        }
        $old_x=imageSX($src_img);
        $old_y=imageSY($src_img);
        
        if ($old_x > $old_y) {
            $thumb_w=$new_w;
            $thumb_h=$old_y*($new_h/$old_x);
        } else if ($old_x < $old_y) {
            $thumb_w=$old_x*($new_w/$old_y);
            $thumb_h=$new_h;
        } else {
            $thumb_w=$new_w;
            $thumb_h=$new_h;
        }
        $dst_img=ImageCreateTrueColor($thumb_w,$thumb_h);
        fastimagecopyresampled($dst_img,$src_img,0,0,0,0,$thumb_w,$thumb_h,$old_x,$old_y,3.25); 
        if ($img_type == "png") {
            imagepng($dst_img, $thumbname); 
        } else {
            imagejpeg($dst_img, $thumbname); 
        }
        imagedestroy($dst_img); 
        imagedestroy($src_img); 
    }

}
    function fastimagecopyresampled ($dst_image, $src_image, $dst_x, $dst_y, $src_x, $src_y, $dst_w, $dst_h, $src_w, $src_h, $quality = 3) {
      // Plug-and-Play fastimagecopyresampled function replaces much slower imagecopyresampled.
      // Just include this function and change all "imagecopyresampled" references to "fastimagecopyresampled".
      // Typically from 30 to 60 times faster when reducing high resolution images down to thumbnail size using the default quality setting.
      // Author: Tim Eckel - Date: 09/07/07 - Version: 1.1 - Project: FreeRingers.net - Freely distributable - These comments must remain.
      //
      // Optional "quality" parameter (defaults is 3). Fractional values are allowed, for example
     // 1.5. Must be greater than zero.
      // Between 0 and 1 = Fast, but mosaic results, closer to 0 increases the mosaic effect.
      // 1 = Up to 350 times faster. Poor results, looks very similar to imagecopyresized.
      // 2 = Up to 95 times faster.  Images appear a little sharp, some prefer this over a quality of 3.
      // 3 = Up to 60 times faster.  Will give high quality smooth results very close to imagecopyresampled, just faster.
      // 4 = Up to 25 times faster.  Almost identical to imagecopyresampled for most images.
      // 5 = No speedup. Just uses imagecopyresampled, no advantage over imagecopyresampled.
    
      if (empty($src_image) || empty($dst_image) || $quality <= 0) { return false; }
      if ($quality < 5 && (($dst_w * $quality) < $src_w || ($dst_h * $quality) < $src_h)) {
        $temp = imagecreatetruecolor ($dst_w * $quality + 1, $dst_h * $quality + 1);
        imagecopyresized ($temp, $src_image, 0, 0, $src_x, $src_y, $dst_w * $quality + 1, $dst_h * $quality + 1, $src_w, $src_h);
        imagecopyresampled ($dst_image, $temp, $dst_x, $dst_y, 0, 0, $dst_w, $dst_h, $dst_w * $quality, $dst_h * $quality);
        imagedestroy ($temp);
      } else imagecopyresampled ($dst_image, $src_image, $dst_x, $dst_y, $src_x, $src_y, $dst_w, $dst_h, $src_w, $src_h);
      return true;
    }
?>

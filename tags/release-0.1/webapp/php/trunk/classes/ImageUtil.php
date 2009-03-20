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
        imagecopyresampled($dst_img,$src_img,0,0,0,0,$thumb_w,$thumb_h,$old_x,$old_y); 
        if ($img_type == "png") {
            imagepng($dst_img, $thumbname); 
        } else {
            imagejpeg($dst_img, $thumbname); 
        }
        imagedestroy($dst_img); 
        imagedestroy($src_img); 
    }
}
?>

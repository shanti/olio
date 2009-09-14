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
    
class Geocoder {

    public $latitude;
    public $longitude;

    function __construct($street, $city, $state, $zip) {
        $this->street = $street;
        $this->city = $city;
        $this->state = $state;
        $this->zip = $zip;
        $url = Olio::$config['geocoderURL'].'?appid=gsd5f&street='.
                $this->street.'&city='.$this->city.
                '&state='.$this->state.'&zip='.$this0->zip;
        $stream = $this->curl_string($url);
        $xmlBegin= strpos($stream, '<?xml');
        if(!$xmlBegin)
            throw new Exception("Did not find xml part in: $stream");
        $xmlString = substr($stream, $xmlBegin);
        $xml = simplexml_load_string($xmlString);
        $this->latitude = $xml->Result->Latitude;
        $this->longitude = $xml->Result->Longitude;
    }

    private function curl_string($url) {

        $ch = curl_init();
        $geoProxy = trim(Olio::$config['proxy']);
        if (strlen($geoProxy) > 0) {
            curl_setopt($ch, CURLOPT_PROXY, $geoProxy);
        } else {
            curl_setopt($ch, CURLOPT_HTTPPROXYTUNNEL, 0);
        }
        curl_setopt ($ch, CURLOPT_URL, $url);
        curl_setopt ($ch, CURLOPT_USERAGENT, 'Mozilla/5.0');
        //curl_setopt ($ch, CURLOPT_COOKIEJAR, "c:\cookie.txt");
        curl_setopt ($ch, CURLOPT_HEADER, 1);
        curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
        //curl_setopt ($ch, CURLOPT_FOLLOWLOCATION, 1);
        curl_setopt ($ch, CURLOPT_TIMEOUT, 120);
        $result = curl_exec ($ch);
        curl_close($ch);
        return $result;
    }
}
?>

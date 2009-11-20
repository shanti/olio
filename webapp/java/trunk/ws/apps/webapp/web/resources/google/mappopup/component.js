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
jmaki.namespace("jmaki.widgets.google.mappopup");

/*
 * Google Map Popup
 *
 * @author Greg Murray
 *
 * A component for showing locations in a popup
*/
jmaki.widgets.google.mappopup.Widget = function(wargs) {

    var _widget = this;

    var loaded = false;
    var mapH = 150;
    var mapW = 150;
    var padding = 10;
    var containerDiv = document.getElementById(wargs.uuid);
    var arrowDiv;
    var resizeDiv;
    var closeDiv;
    var dragStart;
    _widget.top = 0;
    var left = 0;
    var mapType = "SATELLITE_TYPE";
  
    _widget.model = {};
    _widget.model.center = {};
    _widget.model.zoom = 13; 
    _widget.model.location = "Campbell, CA";    

    if (typeof wargs.args != 'undefined') {
        if (typeof wargs.args.zoom == 'number') {
            _widget.model.zoom = wargs.args.zoom;
        }
        if (typeof wargs.args.padding == 'number') {
            padding = wargs.args.padding;
        }        
        
        if (typeof wargs.args.height == 'number') {
            mapH = wargs.args.height;
        }
        
        if (typeof wargs.args.width == 'number') {
            mapW = wargs.args.width;
        }
        if (typeof wargs.args.location == 'string') {
           location = wargs.args.location;
        }
        if (wargs.args.mapType) { 
            if (wargs.args.mapType == 'REGULAR') {
                mapType = "NORMAL_MAP";
            } else if (wargs.args.mapType == 'SATELLITE') {
                mapType = "SATELLITE_TYPE";
            } else if (wargs.args.mapType == 'HYBRID') {
                mapType = "HYBRID_MAP";
            }
        }
        if (wargs.args.zoom) {
        	_widget.model.zoom  = wargs.args.zoom;
        }
    }
    if (wargs.value) {
    	if (typeof wargs.value == 'string') {
    	    _widget.model.location = wargs.value;
    	} else if (wargs.value) {
         //   if (wargs.args.location) _widget.model.location  = wargs.value.location;
            if (wargs.args.zoom) _widget.model.zoom  = wargs.value.locaiton
    	}
    }
    
    this.showMap = function(location) {
        if (containerDiv && containerDiv.style.visibility == "visible") {
            setVisible("hidden");
        } else if (!loaded) {
        	//setVisible("visible");
        	postProcess();
        } else {
            setVisible("visible");
        }
    }
    
    function setVisible(visibility) {
        containerDiv.style.visibility = visibility;
        _widget.mapContainer.style.visibility = visibility;
        arrowDiv.style.visibility = visibility;
        closeDiv.style.visibility = visibility;
    }
	
    function postProcess() {

        if (!loaded) {
            loaded = true;
            containerDiv = document.getElementById(wargs.uuid);
            var button = document.getElementById(wargs.uuid + "_button");
            arrowDiv = document.getElementById(wargs.uuid + "_arrow");
            resizeDiv = document.getElementById(wargs.uuid + "_resize");
            _widget.mapContainer = document.getElementById(wargs.uuid + "_map");
            closeDiv = document.getElementById(wargs.uuid + "_close");
            closeDiv.onmousedown = function (e) {
                setVisible("hidden");
                return false;
            }
            var pos = getPosition(button);
            _widget.top = (pos.y - 25)
            containerDiv.style.top = _widget.top + "px";
            closeDiv.style.left = (mapW - closeDiv.clientWidth -1)  + "px";
            closeDiv.style.top =  1 + "px";
            // resizing div  location
            resizeDiv.style.left = (mapW - resizeDiv.clientWidth) + "px";
            resizeDiv.style.top = (mapH - resizeDiv.clientHeight) + "px";
            // attach listeners
            resizeDiv.onmousedown = function (e) {
                var pos = getMousePos(e);
                dragStart = pos;
                return false;
            }
            function mouseMove(e) {
                if (dragStart) {
                    var pos = getMousePos(e);
                    
                    mapW =  (pos.x - left);
                    mapH =  (pos.y - _widget.top);
                    
                    // move all the things to thier respective places 
                    containerDiv.style.width = mapW + "px";
                    containerDiv.style.height = mapH + "px";
                    _widget.mapContainer.style.width = mapW  - (padding * 2) + "px";
                    _widget.mapContainer.style.height = mapH - (padding * 2) + "px";
                    resizeDiv.style.left = (mapW - resizeDiv.clientWidth) + "px";
                    resizeDiv.style.top = (mapH - resizeDiv.clientHeight) + "px";
                    closeDiv.style.left = (mapW - closeDiv.clientWidth -1) + "px";
                }
                return true;
            }
            
            // drag done
            function onmouseup(e) {
                dragStart = null;
            }
            
            if (typeof document.attachEvent != 'undefined') {
                document.attachEvent("onmousemove",function(e){var event = e;mouseMove(event);});
            } else {
                document.addEventListener("mousemove",function(e){var event= e;mouseMove(e);}, true);
            }
            
            if (typeof document.attachEvent != 'undefined') {
                document.attachEvent("onmouseup",function(e){var event = e;onmouseup(event);});
            } else {
                document.addEventListener("mouseup",function(e){var event= e;onmouseup(e);}, true);
            }
            
            left = (pos.x + button.clientWidth +  arrowDiv.clientWidth - 1) ;
            
            containerDiv.style.left = left + "px";
            _widget.mapContainer.style.top = padding + "px";
            _widget.mapContainer.style.left = padding + "px";
            
            arrowDiv.style.top = pos.y  + "px";
            arrowDiv.style.left = (pos.x + button.clientWidth) + "px";                     
            containerDiv.style.width = mapW + "px";
            containerDiv.style.height = mapH + "px";
            
            _widget.mapContainer.style.width = mapW  - (padding * 2) + "px";
            _widget.mapContainer.style.height = mapH - (padding * 2) + "px";
            init();
            setVisible("visible");
        } else {
            alert("no map available for " + wargs.value);
        }
    }
    
    function getMousePos(e){
        var lx = 0;
        var ly = 0;
        if (!e) e = window.event;
        if (e.pageX || e.pageY) {
            lx = e.pageX;
            ly = e.pageY;
        } else if (e.clientX || e.clientY) {
            lx = e.clientX;
            ly = e.clientY;
        }
        return {x:lx,y:ly};
    }
        function getPosition(_e) {
            var pX = 0;
            var pY = 0;
            try {
             while (_e.offsetParent) {               
                pY += _e.offsetTop;               
                pX += _e.offsetLeft;
                _e = _e.offsetParent;
            }
            } catch(e){};
            return {x: pX, y: pY};
        }
        
        this.plotPoint = function(point) {
            _widget.model.center.latitude = point[0];
            _widget.model.center.longitude = point[1];
            _widget.wrapper.setCenter(point, 13);
            _widget.addMarkerAndCenter({ message : location, label : location}, point, true);
        }
        
        this.addMarkerAndCenter = function(marker, point, _center) {
            if (!mapReady())return;
            var zoom = 13;
            var center = true;
            if (typeof _center != "undefined") center = _center;
            if (marker.message) marker = marker.message;
            if (marker.value) marker = marker.value;
            var centerPoint = new google.maps.LatLng(marker.latitude, marker.longitude);
            if (marker.zoom) zoom = marker.zoom;
            if (center) {
                _widget.wrapper.setCenter(point, zoom);
            }
            var gmarker = new google.maps.Marker(point);
            gmarker.bindInfoWindowHtml(marker.label);
            _widget.wrapper.addOverlay(gmarker);       
            if(marker.open) gmarker.openInfoWindowHtml(marker.label);    	
    };

    // this is called after the script loader api is run
    this.mapCallback = function() {
        google.load("maps", "2",
        {"callback" : function() {       

        	_widget.geocoder = new GClientGeocoder();
                var props = {
                    mapTypes : [google.maps["SATELLITE_MAP"],
       	                google.maps["NORMAL_MAP"],
       	                google.maps["HYBRID_MAP"]
                    ]
                };
                _widget.wrapper = new google.maps.Map2(_widget.mapContainer, props);                
              //  _widget.wrapper.setCenter(new google.maps.LatLng(_widget.model.center.latitude, _widget.model.center.longitude), _widget.model.zoom);
                _widget.wrapper.setMapType(google.maps[mapType]);
                if (_widget.model.markers) {
                    for (var i=0; i < _widget.model.markers.length;i++) {
                        _widget.addMarker(_widget.model.markers[i]);
                    }
                }
                _widget.geocoder.getLatLng(
                	    _widget.model.location,
                        function(point) {
                          if (!point) {
                              jmaki.log("Google Map Popup: location "  + location + " not found");
                          } else {
                               _widget.plotPoint(point);                	  
                          }
                        }
                      );                
            }});                  	
    }; 
   
    // load the widget.json
    function loadWidgetJson(_widgetDir) {
        var obj = null;
        jmaki.doAjax({
            url : _widgetDir + "/widget.json",
            asynchronous : false,
            callback : function(req) {
                if (req.responseText != '') {
                    obj =jmaki.json.deserialize(req.responseText);
                }
            }
        });
        return obj;        
    }
    
    function getHost(url) {
        var host = {};
        host.scheme = "";
        // get the second 1/2             
        var _p = url.split("://");
        host.scheme = _p[0];
        if (_p[1]) {                 
            if (_p[1].indexOf("/") != -1) {
                host.name = _p[1].substring(0, _p[1].indexOf("/"));
            } else {               
                host.name = _p[1];
            }
        }
        host.url = host.scheme + "://" + host.name + "/";
        return host;
    }
   
    function init() {
        var wjson = loadWidgetJson(wargs.widgetDir);       
        var host = getHost(top.window.location.href);       
        var apiKey = "";
        if (host.name != 'localhost' && wjson &&
            wjson.config && 
            wjson.config.apikeys) {
            for (var i=0; i < wjson.config.apikeys[0].keys.length; i++) {
            	var key = wjson.config.apikeys[0].keys[i];
            	if (host.url == key.url) {
                    apiKey = key.key;
                    break;
                }
            }
        }       
        jmaki.addLibraries({
            libs : ["http://www.google.com/jsapi?key=" + apiKey],
            cleanup : false,
            callback : _widget.mapCallback,
            onerror : function(m) {
                jmaki.log("Error loading google.map : " + m);
            }
        });
            
    }

    // check if the map apis have been loaded
    function mapReady() {
        if (typeof google != "undefined" &&
            typeof google.maps != "undefined") {
            return true;
        } else {
            jmaki.log("google.maps error : Google maps not loaded");
            return false;
        }  
    }
};

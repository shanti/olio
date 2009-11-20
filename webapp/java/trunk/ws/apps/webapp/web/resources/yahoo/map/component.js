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
jmaki.namespace("jmaki.widgets.yahoo.map");

/*
 * Yahoo Map Wrapper
 * This wrapper is for version 3.4 of yahoo maps api as described at:
 *
 * @author Greg Murray  (original author)
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com> (Added YEvent jMaki publish events)
 * @constructor
 * @see http://developer.yahoo.com/maps/ajax/V3.4/reference.html
 */ 
jmaki.widgets.yahoo.map.Widget = function(wargs) {
    
    var publish = "/yahoo/map";
    var subscribe = "/yahoo/map";
  
    var uuid = wargs.uuid;
    
    this.zoom = 7;
    var autoSizeH = true;
    var autoSizeW = true;
    // default location to Yahoo
    var centerLat = 37.4041960114344;
    var centerLon = -122.008194923401;
    var address;
    var VIEWPORT_HEIGHT = 0;
    var VIEWPORT_WIDTH = 0;
    var oldResize;
    var oldWidth;
    // we need this for resize eventss
    var ie = /MSIE/i.test(navigator.userAgent);
    var safari = /Safari/i.test(navigator.userAgent);    
    
    _widget = this;
    
    _widget.container = document.getElementById(uuid);
    //this is needed for fix issue 224
    //https://ajax.dev.java.net/issues/show_bug.cgi?id=224
    YMAPPID = "jmaki-key";

    _widget.init = function() {

        var centerPoint;
        _widget.mapType = YAHOO_MAP_SAT;
    

        
        // pull in args
        if (typeof wargs.args != 'undefined') {
            
            //overide topic name if needed
            if (typeof wargs.args.topic != 'undefined') {
                topic = wargs.args.topic;
                jmaki.log("Yahoo map with id=" + wargs.uuid + ". Widget uses deprecated topic. Use publish/subscribe instead.");
            }  
            
            if (typeof wargs.args.zoom != 'undefined') {
                this.zoom = Number(wargs.args.zoom);
            }
            
            if (typeof wargs.args.centerLat != 'undefined') {
                centerLat = Number(wargs.args.centerLat);
            }
            
            if (typeof wargs.args.centerLon != 'undefined') {
                centerLon = Number(wargs.args.centerLon);
            }

	    if (typeof wargs.args.address != 'undefined') {
		address = wargs.args.address;
	    }
		
            
            if (typeof wargs.args.mapType != 'undefined') {
                
                if (wargs.args.mapType == 'REGULAR') {
                    _widget.mapType = YAHOO_MAP_REG;
                } else if (wargs.args.mapType == 'SATELLITE' ) {
                    _widget.mapType = YAHOO_MAP_SAT;
                } else if (wargs.args.mapType == 'HYBRID') {
                    _widget.mapType = YAHOO_MAP_HYB;
                }
            }       
            if (typeof wargs.args.height != 'undefined') {
                VIEWPORT_HEIGHT = Number(wargs.args.height);
                autoSizeH = false;
            }
            
            if (typeof wargs.args.width != 'undefined') {
                VIEWPORT_WIDTH = Number(wargs.args.width);
                autoSizeW = false;
            }
        }
        
        if (wargs.publish) publish = wargs.publish;
        if (wargs.subscribe) subscribe = wargs.subscribe;
        

        
        var mapSize = new YSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        _widget.map = new YMap(_widget.container, _widget.mapType);
	
        // Add this.map type control
        _widget.map.addTypeControl();
        
        // this.map zoomer
        _widget.map.addZoomLong();
        
        // Set this.map type to either of: YAHOO_MAP_SAT YAHOO_MAP_HYB YAHOO_MAP_REG
        
        _widget.map.setMapType(_widget.mapType);

        if (typeof address != 'undefined') {
	   // Set the centre location
	   _widget.map.drawZoomAndCenter (address, this.zoom);
	   
	}
	else {
           centerPoint = new YGeoPoint(centerLat,centerLon);
           _widget.map.drawZoomAndCenter(centerPoint, this.zoom);
	}
        
        
        YEvent.Capture(_widget.map, EventsList.onEndGeoCode, function(e) {
            var cll = _widget.map.getCenterLatLon();
            var marker = new YMarker(cll);
            marker.addLabel("A");
            _widget.map.addOverlay(marker);
        });

        //_widget.map.addMarker (_widget.map.getCenterLatLon());
        /**
         * Just a utility class to convert from yahoo's to ours
         */

        function evt2Value(e) {
            return { 
                lat:e.YGeoPoint.Lat, 
                lon:e.YGeoPoint.Lon, 
                prevZoom:e.zoomObj.previous, 
                currZoom:e.zoomObj.current 
            };
        }
        
        //on click map default handler
        YEvent.Capture(this.map,EventsList.MouseClick, function(e) {
            jmaki.publish(publish + "/onClick", {id:uuid, value:evt2Value(e)});
        });
        //on change zoom default handler
        YEvent.Capture(this.map,EventsList.changeZoom ,function(e) {
            jmaki.publish(publish + "/onChangeZoom", {id:uuid, value:evt2Value(e)});
        });
        resize(); 
    }

    function resize() {
        
        if (oldResize) {
            oldResize();
        }
        if (autoSizeH || autoSizeW){
            var pos = jmaki.getPosition(_widget.container);
            if (_widget.container.parentNode.nodeName == "BODY") {
                if (window.innerHeight){
                    if (autoSizeH) VIEWPORT_HEIGHT = window.innerHeight - pos.y -16;
                    if (autoSizeW) VIEWPORT_WIDTH = window.innerWidth - 15;
                } else {
                    var _tNode = _container.parentNode;
                    while(_tNode != null &&
                    (_tNode.clientHeight == 0 ||
                    typeof _tNode.clientWidth == 'undefined')) {
                        _tNode = _tNode.parentNode;
                    }
                    if (_tNode == null) {
                        VIEWPORT_WIDTH = 400;
                    } else {
                        if (autoSizeW) VIEWPORT_WIDTH = _tNode.clientWidth - 20;
                        if (autoSizeH) VIEWPORT_HEIGHT = _tNode.clientHeight - pos.y - 15;
                    }
                }
                if (VIEWPORT_HEIGHT < 0) {
                    VIEWPORT_HEIGHT = 300;
                }
                if (VIEWPORT_WIDTH < 0) {
                    VIEWPORT_WIDTH = 400;
                }
            } else {
                var _tNode = _widget.container.parentNode;
                while(_tNode != null &&
                (_tNode.clientHeight == 0 ||
                typeof _tNode.clientWidth == 'undefined')) {
                    _tNode = _tNode.parentNode;
                }
                if (_tNode == null) {
                    if (autoSizeW) VIEWPORT_WIDTH = 400;
                } else {
                    if (autoSizeW)  VIEWPORT_WIDTH = _tNode.clientWidth;
                    if (autoSizeH)  VIEWPORT_HEIGHT = _tNode.clientHeight;
                }
            }                  
        }
        
        _widget.container.style.width = VIEWPORT_WIDTH + "px";
        _widget.container.style.height = VIEWPORT_HEIGHT + "px";
        var mapSize = new YSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        _widget.map.resizeTo(mapSize);
        oldWidth = document.body.clientWidth;
        // Display the this.map centered on a latitude and longitude
        if (typeof self.map != 'undefined') {
            self.map.drawZoomAndCenter(_widget.centerPoint, self.zoom);
        }
    }

    var resizing = false;
    var lastSize = 0;

    function layout() {
        if (!ie) {
            resize();
            return;
        }
        // special handling for ie resizing.
        // we wait for no change for a full second before resizing.
        if (oldWidth != document.body.clientWidth && !resizing) {
            if (!resizing) {
                resizing = true;
                setTimeout(layout,1000);
            }
        } else if (resizing && document.body.clientWidth == lastSize) {
            resizing = false;
            resize();
        } else if (resizing) {
            lastSize = document.body.clientWidth;
            setTimeout(layout,1000);
        }
    }
    
    this.postLoad = function() {
    }

    if (typeof window.onresize != 'undefined') {
        oldResize = window.onresize;
    }
    window.onresize = layout;
    
    jmaki.addLibraries({ libs : ['http://us.js2.yimg.com/us.js.yimg.com/lib/map/js/api/ymapapi_3_8_0_7.js' ],
                        callback : _widget.init
    });
}

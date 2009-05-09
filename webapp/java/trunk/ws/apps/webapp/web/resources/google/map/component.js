/* Copyright 2007 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2007/04/15 19:39:59 gmurray71 Exp $
 */
jmaki.namespace("jmaki.widgets.google.map");

/**
 * A wrapper for the Google Map
 * 
 * For more on the Google Map API see:
 * http://code.google.com/apis/maps/documentation/reference.html
 *
 * For Production use you will need a Google API key from:
 * http://code.google.com/apis/maps/signup.html
 *
 * API keys are configured in the widget.json file.
 *
 */
jmaki.widgets.google.map.Widget = function(wargs) {
	
    var _widget = this;    
    _widget.container = document.getElementById(wargs.uuid);
    _widget.model = {};
    _widget.model.center = {};
    _widget.model.zoom = 13;
    var publish = "/google/map";
    var subscribe = ["/google/map"];  
    _widget.model.center.latitude = 37.4419;
    _widget.model.center.longitude = -122.1419;
    
    var mapType = "SATELLITE_MAP";
    var mapH;
    var mapW;   

    // this is called after the script loader api is run
    this.mapCallback = function() {
        google.load("maps", "2",
        {"callback" : function() {             
                var props = {
                    mapTypes : [google.maps["SATELLITE_MAP"],
       	                google.maps["NORMAL_MAP"],
       	                google.maps["HYBRID_MAP"]
                    ]
                };
                if (mapH || mapW) {
                    props.size = new google.maps.Size(mapW, mapH);
                }
                _widget.wrapper = new google.maps.Map2(_widget.container, props);                
                _widget.wrapper.setCenter(new google.maps.LatLng(_widget.model.center.latitude, _widget.model.center.longitude), _widget.model.zoom);
                _widget.wrapper.addControl(new google.maps.SmallMapControl());
                _widget.wrapper.addControl(new google.maps.MapTypeControl());
                _widget.wrapper.setMapType(google.maps[mapType]);
                if (_widget.model.markers) {
                    for (var i=0; i < _widget.model.markers.length;i++) {
                        _widget.addMarker(_widget.model.markers[i]);
                    }
                }
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
   
    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);     
    }
    
    function init() {
        var dim = jmaki.getDimensions(_widget.container);
        _widget.container.style.height = dim.h + "px";
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
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i] + "/addMarker", _widget.addMarkerAndCenter);
            doSubscribe(subscribe[_i] + "/setCenter", _widget.center);
            doSubscribe(subscribe[_i] + "/setZoom", _widget.zoom);
        }
        jmaki.subscribe(new RegExp("/jmaki/plotmap$"), geocoderListener);
        // add listerner mapping for backward compatibility
        jmaki.subscribe("*/geocoder*", geocoderListener);
            
    }
  
    // support the old geocoder listeners
    function geocoderListener (coordinates) {
        // keep out the /yahoo/geocoder second publish
        if (!(coordinates instanceof Array)) return;
        _widget.addMarker({latitude : coordinates[0].latitude,
            longitude : coordinates[0].longitude,
            label : coordinates[0].address + ' ' +
                coordinates[0].city + ' ' +
                coordinates[0].state,
            open : true
        });
    }
   
    this.postLoad = function() {

        if (wargs.publish) {
            publish = wargs.publish;
        }
        if (wargs.args) {    	
            if (wargs.args.zoom) {        	
                _widget.model.zoom = wargs.args.zoom;
            }
            if (wargs.args.latitude) {
                _widget.model.center.latitude = wargs.args.latitude;
            }        
            if (wargs.args.centerLat) {
                _widget.model.center.latitude = wargs.args.centerLat;
            }
            if (wargs.args.centerLon) {
                _widget.model.center.longitude = wargs.args.centerLon;
            }
            if (wargs.args.longitude) {
                _widget.model.center.longitude = wargs.args.longitude;
            }
            if (wargs.args.height) {
                mapH = wargs.args.height;
            }        
            if (wargs.args.width) {
                mapW = wargs.args.width;
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
        }     
        if (wargs.value) {
            _widget.model = wargs.value;
            init();
        } else if (wargs.service) {
            jmaki.doAjax({
                url : wargs.service,
                callback : function(req) {
                    if (req.responseText != '') {
                        try {
                        _widget.model = eval("(" + req.responseText +  ")");
                        init();
                        } catch(e){
                             _widget.container.innerHTML = "Error parsing data from service:  " + e;
                        }
                    } else {
                        _widget.container.innerHTML = "No Map data from service.";
                    }
                }
            }); 
        } else if (_widget.model.center.latitude &&
                   _widget.model.center.longitude) {
            init();
        } else {
            _widget.model.zoom = 1;
            init();  
        }
    };
    
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
    
    this.addMarkerAndCenter = function(marker, _center) {
        if (!mapReady())return;
        var center = true;
        if (typeof _center != "undefined") center = _center;
        if (marker.message) marker = marker.message;
        if (marker.value) marker = marker.value;
        var centerPoint = new google.maps.LatLng(marker.latitude, marker.longitude);
        if (marker.zoom) zoom = marker.zoom;
        if (center) {
            _widget.wrapper.setCenter(centerPoint, zoom);
        }
        var gmarker = new google.maps.Marker(centerPoint);
        gmarker.bindInfoWindowHtml(marker.label);
        _widget.wrapper.addOverlay(gmarker);       
        if(marker.open) gmarker.openInfoWindowHtml(marker.label);    	
    };

    this.addMarker= function(coordinates) {
        _widget.addMarkerAndCenter(coordinates, false);  	
    };

    this.zoom = function(zoom) {
        if (!mapReady())return;
        if (zoom.message) zoom = zoom.message;
        if (zoom.value) zoom = zoom.value;
        _widget.model.zoom = zoom.zoom;
        _widget.wrapper.setZoom(zoom);

    };
   
    this.center = function(coordinates) {
        if (!mapReady())return;
        if (coordinates.message) coordinates = coordinates.message;
        if (coordinates.value) coordinates = coordinates.value;
        if (coordinates instanceof Array) coordinates = coordinates[0];
        var centerPoint = new google.maps.LatLng( coordinates.latitude, coordinates.longitude);
        if (coordinates.zoom) zoom = coordinates.zoom;
        _widget.wrapper.setCenter(centerPoint, zoom);      	
    };
};

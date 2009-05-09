/* Copyright 2007 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2007/04/15 19:39:59 gmurray71 Exp $
 */

jmaki.namespace("jmaki.widgets.google.search");

/**
 * A wrapper for the Google Search
 * 
 * For more on the Google Search API see:
 * http://code.google.com/apis/ajaxsearch/documentation/
 *
 * For Production use you will need a Google API key from:
 * http://code.google.com/apis/maps/signup.html
 *
 * API keys are configured in the widget.json file.
 *
 */
jmaki.widgets.google.search.Widget = function(wargs) {
	
    var _widget = this;    
    _widget.container = document.getElementById(wargs.uuid);

    var publish = "/google/search";
    var subscribe = ["/google/search"];  

    
    var centerPoint =  'Santa Clara, CA';
    var defaultSearch;
    var showWebSearch = true;
    var showNewsSearch = true;
    var showLocalSearch = false;
    var showBlogSearch = false;
    var showVideoSearch = false;
    var showImageSearch = false;
    var showBookSearch = false;
    

    // this is called after the script loader api is run
    this.mapCallback = function() {
        google.load("search", "1", {
            "callback" : function() {
                
                _widget.wrapper = new google.search.SearchControl();
                var options = new google.search.SearcherOptions();
                options.setExpandMode(google.search.SearchControl.EXPAND_MODE_OPEN);
                if (showLocalSearch) {
                    var ls = new google.search.LocalSearch();
                    _widget.wrapper.addSearcher(ls, options);
                    ls.setCenterPoint(centerPoint);  
                }
                if (showWebSearch) { 
                    _widget.wrapper.addSearcher(new google.search.WebSearch(), options);
                }
                if (showNewsSearch) {
                    _widget.wrapper.addSearcher(new google.search.NewsSearch());
                }
                if (showVideoSearch) {
                    _widget.wrapper.addSearcher(new google.search.VideoSearch());
                }
                if (showImageSearch) {
                    _widget.wrapper.addSearcher(new google.search.ImageSearch());
                }
                if (showBookSearch) {
                    _widget.wrapper.addSearcher(new google.search.BookSearch());
                }
                if (showBlogSearch) {
                    _widget.wrapper.addSearcher(new google.search.BlogSearch());
                }
                var drawOptions = new google.search.DrawOptions();
                drawOptions.setDrawMode(google.search.SearchControl.DRAW_MODE_TABBED);
                _widget.wrapper.draw(_widget.container,drawOptions);
                // update the style
                var t = jmaki.getElementsByStyle("gsc-control", _widget.container);
                if (t) {
                    t[0].style.width = "99%";   
                }                 
                if (defaultSearch) _widget.doSearch(defaultSearch);
            }});                  	
    }; 
   
    _widget.doSearch = function(search) {
        if (!searchReady()) return;
        if (search.message) search = search.message;
        if (search.value) search = search.value;
        if (search.label)search = search.label;
        _widget.wrapper.execute(search);
        jmaki.publish(publish + "/onSearch", {value : search});        
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
        if (wjson &&
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
                jmaki.log("Error loading google.search : " + m);
            }
        });
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i] + "/doSearch", _widget.doSearch);
        }
            
    }
    
    this.postLoad = function() {
        
        if (wargs.publish) {
            publish = wargs.publish;
        }
        if (wargs.args) {
            if (wargs.args.defaultSearch) {
                defaultSearch = wargs.args.defaultSearch;
            }
            if (wargs.args.centerPoint) {
                centerPoint = wargs.args.centerPoint;
            }
            if (typeof wargs.args.showNewsSearch == "boolean") {
                showNewsSearch = wargs.args.showNewsSearch;              
            }            
            if (typeof wargs.args.showWebSearch == "boolean") {
                showWebSearch = wargs.args.showWebSearch;              
            }
            if (typeof wargs.args.showLocalSearch == "boolean") {
                showLocalSearch = wargs.args.showLocalSearch;
            }
            if (typeof wargs.args.showVideoSearch == "boolean") {
                showVideoSearch = wargs.args.showVideoSearch;              
            }
            if (typeof wargs.args.showBlogSearch == "boolean") {
                showBlogSearch = wargs.args.showBlogSearch;
            }
            if (typeof wargs.args.showImageSearch == "boolean") {
                showImageSearch = wargs.args.showImageSearch;
            }
            if (typeof wargs.args.showBookSearch == "boolean") {
                showBookSearch = wargs.args.showBookSearch;
            }              
        }    
        init();
    };
    
    // check if the map apis have been loaded
    function searchReady() {
        if (typeof google != "undefined" &&
            typeof google.search != "undefined") {
            return true;
        } else {
            jmaki.log("google.search error : Google search not loaded");
            return false;
        }  
    }
};
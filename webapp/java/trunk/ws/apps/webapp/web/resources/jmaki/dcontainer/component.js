/* Copyright 2007 Sun Microsystems, Inc. All rights reserved. You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at: http://developer.sun.com/berkeley_license.html
$Id: component.js,v 1.7 2007/07/22 22:49:14 gmurray71 Exp $
*/

// define the namespaces
jmaki.namespace("jmaki.widgets.jmaki.dcontainer");

/**
 * @constructor
 */
jmaki.widgets.jmaki.dcontainer.Widget = function(wargs) {
    
    var _widget = this;
    this.uuid = wargs.uuid;

    // the enclosing container    
    _widget.container = document.getElementById(wargs.uuid);

    // get the distance from the top of the container to calculate offset
    var offsetY =0
    if (_widget.container.parentNode) {
        offsetY = _widget.container.offsetTop - _widget.container.parentNode.offsetTop;
    }
    _widget.container.style.height= _widget.container.parentNode.clientHeight - offsetY + "px";
        
    var startHeight;
    var startWidth;
    var overflow;
    var useIframe = false;
    var autosize = true;
    var overflowY;
    var overflowX;
    var include;
    var content;
    var counter = 0;    
    
    var views = {};
    var selectedView;
    var items;
    
    // subscribe to this topic for url update request  
    var subscribe = ["/jmaki/dcontainer", "/tabbedview"];
    var publish = "/jmaki/dcontainer";
    
    function genId() {
        return wargs.uuid + "_nid_" + counter++;
    }
       
    this.clear = function(e, c) {
        var viewId;
        var content;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;
        if (e.value) content = e.value;
        else content = c;
  
        // use this for the case where there is only a single view
        if (typeof viewId != "string" && selectedView) viewId = selectedView; 
        if (views[viewId]) {
            var view = views[viewId];            
            if (content){
                view.dcontainer.clear();
                view.contentLoaded = true;
            }
        }
    }
 
     this.setInclude = function(e, c) {
        var viewId;
        var include;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;
        if (e.value) include = e.value;
        else include = c;
        // use this for the case where there is only a single view
        if (typeof viewId != "string" && selectedView) viewId = selectedView; 

        if (views[viewId]) {
            var view = views[viewId];
            if (include){            
                view.dcontainer.loadURL(include);
                view.contentLoaded = true;
            }
        }
    };
    
    this.setContent = function(e, c) {
        var viewId;
        var content;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;
        if (e.value) content = e.value;
        else content = c;
  
        // use this for the case where there is only a single view
        if (typeof viewId != "string" && selectedView) viewId = selectedView; 
        if (views[viewId]) {
            var view = views[viewId];            
            if (content){
                view.dcontainer.setContent(content);
                view.contentLoaded = true;
            }
        }
    }; 
    
   this.getSelectedView = function() {
       return selectedView;
   };
   
    function processActions(_t, _pid, _type, _value) {
        jmaki.processActions({ topic : publish, 
                             widgetId : wargs.uuid,
                             targetId : _pid,
                             type : _type,
                             value : _value
                           }
        
        );
    } 

    
   this.selectView = function(e) {
        var viewId;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;     
        if (views[viewId]) {
            // hide the previous view
            if (viewId != selectedView && views[selectedView]) {           
              views[selectedView].container.style.display = "none";  
            }
            var view = views[viewId];
            if (view.action) processActions(view, viewId, 'onSelect');           
            if (view.include && view.contentLoaded == false){               
                view.dcontainer.loadURL(view.include);
                view.contentLoaded = true;
            }
            view.container.style.display = "inline";
            selectedView = viewId;    
        }
    };
    
    /**
     * Add a view
     * @param view object following the tabbed view /accordion object model
     */
    this.addView = function(view) {
        if (!view.id) view.id = genId();
        // set overflow on if x or y overflow set
        if (view.overflow) {
            view.overflow = view.overflow;      
        } else  if (view.overflowX == 'hidden' && view.overflowY == 'hidden') {
            view.overflow = 'hidden';
        } 
        if (!view.autosize) view.autosize = true;
        view.contentLoaded = false;      
        view.container = document.createElement("div");
        view.container.id = wargs.uuid + "_" + view.id;
        // select the current view if specified or not done yet.
        if (view.selected == true ||  !selectedView) selectedView = view.id;        
        view.container.style.display = 'none';
        _widget.container.appendChild(view.container);

        view.dcontainer = new jmaki.DContainer(
            {target: view.container.id,
             useIframe : view.iframe,
             overflow : view.overflow,
             overflowY : view.overflowY,
             overflowX : view.overflowX,
             content : view.content,
             startHeight : view.startHeight,
             startWidth : view.startWidth,
             autosize : view.autosize}); 
        view.dcontainer.resize();
        views[view.id] = view;
    };
    
    /** 
     * Remove a view
     * @param view id that is to be removed
     */
    this.removeView = function(view) {
        var viewId;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;

        if (views[viewId]) {
            var view2 = views[viewId];
            // clean up jmaki widgets
            jmaki.clearWidgets(view2.container);
            delete views[viewId];
        }
    };
    
    /**
     * Returns the view at a given targetId
     * @param targetId view id
     * @return a View object at that index
     */
    this.getView = function(targetId) {
      return views[targetId];
    };
    
 
    this.getViews = function() {
        return views;
    };    

    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }
    
    function init() {
        // add items for the multiple case of add a single view otherwise
        if (items) {
            for(var _ii=0; _ii < items.length; _ii++) {
               _widget.addView(items[_ii]);
            }
        } else {
            _widget.addView({ id : 'default',
                iframe : useIframe,
                overflow : overflow,
                startHeight : startHeight,
                startWidth : startWidth,
                autosize : autosize,
                overflowX : overflowX,
                overflowY : overflowY,
                include : include,
                content : content
        
            });
        }     
        
        if (selectedView) _widget.selectView(selectedView);
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i] + "/setInclude", _widget.setInclude);
            doSubscribe(subscribe[_i] + "/clear", _widget.clear);            
            doSubscribe(subscribe[_i]  + "/select", _widget.selectView);
            doSubscribe(subscribe[_i] + "/setContent", _widget.setContent);
        }         
     }
    
    
    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
    };
    
    this.postLoad = function() {
        if (wargs.args){
            if (wargs.args.height){
                startHeight = wargs.args.height;
            }
            if (typeof wargs.args.width != 'undefined'){
                startWidth = wargs.args.width;
            }   
            if (wargs.args.overflow){
                overflow = wargs.args.overflow;
            }   
            if (wargs.args.iframe){
                useIframe = (wargs.args.iframe == true);
            }
            
            if (wargs.args.autosize) {
                autosize = wargs.args.autosize;
            }
            
            if (wargs.args.url) {
                include = wargs.args.url;
            }
            if (wargs.args.include) {
                include = wargs.args.include;
            }
            
            if (wargs.args.overflowX) {
                overflowX = wargs.args.overflowX;
                if (!overflow) overflow = 'auto';
            }
            if (wargs.args.overflowY) {
                overflowY = wargs.args.overflowY;
                if (!overflow) overflow = 'auto';
            }
            if (wargs.args.content) {
                content = wargs.args.content;
            }         
        }
        
        if (wargs.value && wargs.value.include) {
            _widget.url = wargs.value.include;
        }
        
        if (wargs.subscribe){
            if (typeof wargs.subscribe == "string") {
                subscribe = [];
                subscribe.push(wargs.subscribe);
            } else {
                subscribe = wargs.subscribe;
            }
        }
        
        if (wargs.publish) {
            publish = wargs.publish;
        }        
        if (wargs.value) {     
            if (wargs.value.views) {
                showModelDeprecation();
                items = wargs.value.views;
            } else if (wargs.value.items) {
                items = wargs.value.items;
            } else {
                showModelDeprecation();
                return;
            }
            init();
        } else if (wargs.service){
            var  _s = wargs.service;
            var callback = function(req) {
                if (req.responseText == '') {
                    _widget.container.innerHTML = "Error loading widget data. No data.";
                    return;
                }
                var obj = eval("(" + req.responseText + ")");
                
                if (obj.views) {
                    showModelDeprecation();
                    items = obj.views;
                } else if (obj.items){
                    items = obj.items;
                } else {
                    showModelDeprecation();
                    return;
                }
                init();  
            }
            
            jmaki.doAjax({url : _s,
                callback : callback,
                onerror : function() {
                    _widget.container.innerHTML = "Error loading widget data.";
                }
            });       
        } else {
            init();
        } 
    };
};

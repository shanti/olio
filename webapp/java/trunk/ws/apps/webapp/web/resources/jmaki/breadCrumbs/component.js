/* Copyright 2008 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2007/04/15 19:39:59 gmurray71 Exp $
*/
jmaki.namespace("jmaki.widgets.jmaki.breadCrumbs");

/**
 * @constructor
 */
jmaki.widgets.jmaki.breadCrumbs.Widget = function(wargs) {

    var _widget = this;
    var publish = "/jmaki/breadCrumbs";
    var subscribe =  ["/jmaki/breadCrumbs"];

    var container = document.getElementById(wargs.uuid + "_container");

    var items;
    var selectedTab;
    var mis = [];
    var tabs = {};
    var useHomeIcon = true;
    var homeIconSrc = wargs.widgetDir + "/images/home.png";
    
    if (wargs.value) {
        items = wargs.value.menu;
    }
    if (wargs.publish) {
        publish = wargs.publish;
    }
    
    if (wargs.subscribe){
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        } else {
            subscribe = wargs.subscribe;
        }
    }    
    
    function createMenu(items) {
        var _firstSelected;
        var menu = document.createElement("ul");
        container.appendChild(menu);
          
        for (var l= 0; l < items.length; l++) {
            
            var _id;
            if (items[l].id) _id = items[l].id;
            else _id = wargs.uuid + "_" + l;
            
            if (items[l].selected || !_firstSelected) _firstSelected = _id;
            var mi = document.createElement("li")
                        
            var mic =  document.createElement("div");            
            mic.id = _id + "_center";
            
            if (l == 0) {
                mic.className = "jm-bc-home";
            } else {
                mic.className = "jm-bc-center";
            }

            var mir =  document.createElement("div");
            mir.id = _id + "_right";         
            if (l != items.length -1) {
              mir.className = "jm-bc-right";
            } else {
                mir.className = "jm-bc-end";
            }
            
            var link = document.createElement("a");
            var target = items[l].id;
            link.id = target;

            link.className = "jm-bc-link";
            link.item =  items[l];
            link.item.uuid = _id;
       
            link.onclick = function(e) {
                if (!e) var e = window.event
                var t;
                if (e.target) t= e.target;
                else if (e.srcElement) t = e.srcElement;
                // home icon gets image as src
                if (!t.item) t= t.parentNode;           
                _widget.select(t.item.uuid);
               
                if (t.item.href) {
                    // if it's just a href nativate to it
                    if (t.item.href && !t.item.target) {
                        window.location.href = t.item.href;
                    } else if (t.item.target) {
                        t.target = t.item.target;                    
                        t.href= t.item.href;
                    }
                }
            }

            if (l == 0 && useHomeIcon) {
                   var homeIcon =  document.createElement("img");
                   homeIcon.src = homeIconSrc;
                   link.appendChild(homeIcon);
                  // link.setAttribute('label', items[l].label);
            } else {
                link.appendChild(document.createTextNode(items[l].label));
            }
            
            mic.appendChild(link);
            mi.appendChild(mic);
            mi.appendChild(mir);

            menu.appendChild(mi);
            mis.push(link);
            tabs[_id] = link;
       }     
       if (_firstSelected){
           _widget.select(_firstSelected);             
       }
       // set the size on the container which will cause it to center
       container.style.width = mi.parentNode.clientWidth + "px";
    }
    
    this.select = function(e) {
        var viewId;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;       
        if (tabs[viewId]) {
            var t = tabs[viewId];
            selectedTab = t.item.uuid;        
            processActions(t.item, t.id, 'onSelect');
            for (var i=0; i < mis.length; i++) {
                var tar = mis[i].item.uuid;
                var right = document.getElementById( tar + "_right");
                var center = document.getElementById( tar + "_center");
                
                if (selectedTab != tar) {           
                    if (i == 0) {
                        center.className = "jm-bc-home";
                    } else {
                        center.className = "jm-bc-center";
                    }
                    if (i != mis.length -1) right.className = "jm-bc-right";
                    else right.className = "jm-bc-end";
                } else {   

                    if (i == 0) {
                        center.className = "jm-bc-home-selected";
                    } else {
                        // get previous and set 
                        var prev = mis[i-1].item.uuid;
                        var left = document.getElementById( prev + "_right");
                        left.className = "jm-bc-left-selected";
                        center.className = "jm-bc-center-selected";
                    }
                    if (i != mis.length -1) right.className = "jm-bc-right-selected";
                    else right.className = "jm-bc-end-selected";   
              }     
            }
        }  
    }

    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }    
           
    this.init = function() {
         createMenu(items);
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
	    _widget.subs = [];         
            doSubscribe(subscribe[_i]  + "/select", _widget.select);
        }          
    }

    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
    }    

    function clone(t) {
       var obj = {};
       for (var i in t) {
            obj[i] = t[i];
       }
       return obj;
    }
    
    function processActions(_t, _pid, _type, _value) {
        if (_t) {
            var _topic = publish;
            var _m = {widgetId : wargs.uuid, type : _type, targetId : _pid};
            if (typeof _value != "undefined") _m.value = _value;
            var action = _t.action;
            if (!action) _topic = _topic + "/" + _type;
            if (action && action instanceof Array) {
              for (var _a=0; _a < action.length; _a++) {
                  var payload = clone(_m);
                  if (action[_a].topic) payload.topic = action[_a].topic;
                  else payload.topic = publish;
                  if (action[_a].message) payload.message = action[_a].message;
                  jmaki.publish(payload.topic,payload);
              }
            } else {
              if (action && action.topic) {
                  _topic = _m.topic = action.topic;
              }
              if (action && action.message) _m.message = action.message;               
              jmaki.publish(_topic,_m);
            }
        }
    }
     
    this.init();
}
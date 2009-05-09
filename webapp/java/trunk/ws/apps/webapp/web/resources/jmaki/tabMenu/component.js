/* Copyright 2007 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2007/04/15 19:39:59 gmurray71 Exp $
*/
// define the namespaces
jmaki.namespace("jmaki.widgets.jmaki.tabMenu");

/**
 * @constructor
 */
jmaki.widgets.jmaki.tabMenu.Widget = function(wargs) {

    var _widget = this;
    var publish = "/jmaki/tabMenu";
    var subscribe =  ["/jmaki/tabMenu"];

    var container = document.getElementById(wargs.uuid + "_container");

    var items;
    var selectedTab;
    var mis = [];
    var tabs = {};
    
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
            
            var mil = document.createElement("div");
            mil.className = "jm-tm-left";
            mil.id = _id + "_left";
            
            var mic =  document.createElement("div");
            mic.className = "jm-tm-center";
            mic.id = _id + "_center";
            
            var mir =  document.createElement("div");
            mir.className = "jm-tm-right";
            mir.id = _id + "_right";

            var link = document.createElement("a");
            var target = items[l].id;
            link.id = target;

            link.className = "jm-tm-link";
            link.item =  items[l];
            link.item.uuid = _id;
       
            link.onclick = function(e) {
                if (!e) var e = window.event
                var t;
                if (e.target) t= e.target;
                else if (e.srcElement) t = e.srcElement;
                       
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

            link.appendChild(document.createTextNode(items[l].label));

            mic.appendChild(link);

            mi.appendChild(mil);
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
                if (selectedTab != tar) {           
                    document.getElementById( tar + "_left").className = "jm-tm-left";
                    document.getElementById( tar + "_center").className = "jm-tm-center";
                    document.getElementById( tar + "_right").className = "jm-tm-right";
                } else {        
                    document.getElementById( tar + "_left").className = "jm-tm-left-selected";
                    document.getElementById( tar + "_center").className = "jm-tm-center-selected";
                    document.getElementById( tar + "_right").className = "jm-tm-right-selected";               
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
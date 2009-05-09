/* Copyright 2007 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2008/04/15 19:39:59 gmurray71 Exp $
*/
jmaki.namespace("jmaki.widgets.jmaki.blockList");

// this function filters a jmakiRSS formated object into an object that this
// widget can consume.
jmaki.widgets.jmaki.blockList.RssFilter = function(_in) {
    var maxLength = 100;
    var _rows = [];

    for (var _i=0; _i < _in.channel.items.length;_i++) {
      var _des = _in.channel.items[_i].description;
      if (_des.length > maxLength) {
          _des = _des.substring(0, maxLength) + "...";
      }
      var row = {      
         title : _in.channel.items[_i].title,
         link : _in.channel.items[_i].link,
         date : _in.channel.items[_i].date,
         description : _des,
         content : _in.channel.items[_i].content
      };
      _rows.push(row);
    }
    return _rows;  
}

jmaki.widgets.jmaki.blockList.Widget = function(wargs) {

    var _widget = this;   
    var container;
    var list;
    var items = [];
    var index = 0;
    var count = 3;
    var topic = "/jmaki/blocklist";
    var subscribe = ["/jmaki/blockList"];    
    var filter = "jmaki.widgets.jmaki.blockList.RssFilter";
    
    var next;
    var prev;
    var shownav = true;
    var nav  = document.getElementById(wargs.uuid + "_nav");

    var data;

    if (wargs.args) {
        if (wargs.args.count) {
            count = Number(wargs.args.count);
        }
        if (typeof wargs.args.shownavigation != 'undefined') {        
            shownav = wargs.args.shownavigation;  
            if (nav && shownav == false) nav.style.display = "none";
        }
        
       if (wargs.args.filter) {
           filter = wargs.args.filter;
       }
    }
    
    if (wargs.publish){
        topic = wargs.publish;
    }

    this.addItem = function(text) {
        var li = document.createElement("li");
        li.innerHTML = text;
        list.appendChild(li);
    };
    
    this.destroy = function() {
        delete list;
        delete next;
        delete prev;
        delete container;
    };

    this.render = function() {
        // remove the innerHTML
        list.innerHTML = "";
        for (var _i=index; _i < data.length && _i < (index + count); _i++) {
          _widget.addItem(items[_i]);
        }
        if ((index + count) >= data.length) {
            next.style.visibility = "hidden";
        } else {
            next.style.visibility= "visible";
        }
        if (index == 0) {
            prev.style.visibility = "hidden";
        } else {
            prev.style.visibility= "visible";
        }
    };
    
    this.applyTemplate = function(obj, _t) {
        for (var i in obj) {
            var token = "@{" + i + "}";
            while (_t.indexOf(token) != -1) {
                _t = _t.replace(token, obj[i]);
            }
        }
        return _t;
    };
    
    this.getNext = function() {
        if (index + count < data.length) index += count;
        _widget.clearList();        
        _widget.render();
    };
    
    this.getPrevious = function() {
        if (index > 0){
            index -= count;
            prev.enabled = "true";
        } else {
            prev.enabled = "false";
            index = 0;
        }
        _widget.clearList();        
        _widget.render();
        
    };
    
    this.clearList = function() {
        for (var i=list.childNodes.length - 1; i > 0 ; i--) {
           list.removeChild(list.childNodes[i]);
        }
    };

    if (wargs.value) {
        if (wargs.args && wargs.args.filter){
            data = jmaki.filter(wargs.value, wargs.args.filter);
        }else  data = wargs.value;
        init();
    } else if (wargs.service) {
        jmaki.doAjax({url: wargs.service, callback: function(req) {

                    var _in = eval('(' + req.responseText + ')');
                    if (_in.dataType) {
                        if (_in.dataType == "jmakiRSS") {
                          data = jmaki.filter(_in, filter);
                        }  else {
                          data = _in;
                        }
                    } else {
                        data = _in;
                    }
                    init();
        }});
    } else {
      data = [
    
        { label : 'jMaki Project Home', link : 'https://ajax.dev.java.net', description : 'Where to go for the latest jMaki.' },
        { label : 'jMaki Widgets Home', link : 'https://widgets.dev.java.net', description : 'The source for the latest jMaki widgets.' },
        { label : 'jMaki-Charting Home', link : 'https://jmaki-charting.dev.java.net', description : 'Enables complex charts rendered on the client in any modern browser.' }
    
    ];
        init();
    }

    function init() {
       // this is the template for all li
       var template = unescape(document.getElementById(wargs.uuid + "_template").innerHTML + "");
       for (var _i=index; _i < data.length; _i++) {
         items.push(_widget.applyTemplate(data[_i], template));
       }
       container = document.getElementById(wargs.uuid);
       var dim = jmaki.getDimensions(container, 52);

       container.style.height = (dim.h - 2) + "px";
       var content = document.getElementById(wargs.uuid + "_content");
      
       list = document.getElementById(wargs.uuid + "_list");      
       content.style.height = dim.h - 50 + "px";

       next = document.getElementById(wargs.uuid + "_next");
       prev = document.getElementById(wargs.uuid + "_previous");
       next.onclick = _widget.getNext;
       prev.onclick = _widget.getPrevious;
       _widget.render();
    }
    
    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
    };       
    
    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);     
    }
    
    this.clear = function() {
        items = [];
    
    };
    
    this.postLoad = function() {
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
        }
    };
};

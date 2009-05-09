jmaki.namespace("jmaki.widgets.jmaki.carousel");

jmaki.widgets.jmaki.carousel.Widget = function(wargs) {

    var _widget = this;    

    var items = [];
    var index = -1;
    var count = 1;
    var itemCount = 5;
    var timeout = 35;
    var topic = "/jmaki/carousel";
    var subscribe = ["/jmaki/carousel"];
    var filter = "jmaki.widgets.jmaki.carousel.RSSFilter";
    
    var tagService = "https://api.feedburner.com/format/1.0/JSONP?uri=";
    var feed ="https://api.feedburner.com/format/1.0/JSONP?uri=TheAquarium_en";    
    
    var counter = 0;
    
    var next;
    var prev;
    var showNav = true;

    var increment = 25;
    // this is relative to height
    var itemHeight = 100;
    var itemWidth = 100;
    var baseWidth = "100%";
    
    var fader = null;
    
   _widget.node = document.getElementById(wargs.uuid);
 
    var maxLength = 1024;
    var template;
    var scrollCount = 1;
    jmaki.MSIE6 = /MSIE ([6])/.test(navigator.userAgent);  
    var horizontalScroll = true;
    var scrollTimeout = 5000;
    var scrollCarousel = true;
    var scrolling = false;
    var fadeInterval = 150;
    var opacitysetting = 1;
    
    // set themes
    var themes = {
  	   ocean : 'ocean',
  	   kame : 'kame',
       orange : 'orange'
  	};
    var currentTheme = themes['ocean'];
    if (jmaki.config && jmaki.config.globalTheme) {
        if (themes[jmaki.config.globalTheme])
    	    currentTheme = jmaki.config.globalTheme;
    }

    
    if (wargs.args) {
        if (wargs.args.itemCount) {
            itemCount = Number(wargs.args.itemCount);
        }
        if (typeof wargs.args.showNavigation == "boolean") {
            showNav = wargs.args.showNavigation;
        }
        if (wargs.args.filter) {
            filter = wargs.args.filter;
        }
        if (wargs.args.scrollTimeout) {
            scrollTimeout = wargs.args.scrollTimeout;
        }
        if (wargs.args.increment) {
            increment = wargs.args.increment;
        }
        if (wargs.args.itemWidth) {
            baseWidth = wargs.args.itemWidth;
        }
        if (typeof wargs.args.scrollCarousel == "boolean") {
            scrollCarousel = wargs.args.scrollCarousel;
        }
        if (wargs.args.theme) {
            currentTheme = wargs.args.theme;
        }
    }
    
    
    if (/%/i.test(baseWidth)) {
        var _w = new Number(baseWidth.split('%')[0]);
        var _dim = jmaki.getDimensions(_widget.node);
        if (_dim != null) 
            itemWidth = (_dim.w - 12) / _w * 100;
    }
    else {
        itemWidth = baseWidth;
    }
    
    var scroll;
    // include some padding
    if (horizontalScroll) 
        scroll = itemWidth;
    else 
        scroll = itemHeight + 4;
    
    if (wargs.subscribe) {
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
        else {
            subscribe = wargs.subscribe;
        }
    }
    
    if (wargs.publish) {
        publish = wargs.publish;
    }
    
    if (!jmaki.getExtension("jmaki.wait")) {
        jmaki.loadExtension({
            name: "jmaki.wait",
            extensionDir: wargs.widgetDir
        });
    }
    
    // JSONP callback
    this.processJSONFeed = function(obj){
        _widget.model = jmaki.filter(obj, jmaki.widgets.jmaki.carousel.FeedburnerFilter);
        _widget.setItems(_widget.model);
        jmaki.publish("/hideWait", {
            targetId: wargs.uuid
        });
    };
    
    _widget.timerFunction = function(){
        if (!scrollCarousel) 
            return;
        if (items && (index < items.length - 1)) {
            _widget.getNext();
        }
        else {
            _widget.reset();
        }
        _widget.timer = setTimeout(_widget.timerFunction, scrollTimeout);
    };
	
    this.reset = function(){
        setTimeout(function(){
            fade('out', function(){
                _widget.container.style.display = "none";
                _widget.container.style.left = "0px";
                setTimeout(function(){
                    fade('in', function(){
                        _widget.select(0);                   
                    })
                }, fadeInterval);
            })
        }, fadeInterval);
    };
    
    function setOpacity(opacity, id){
        var target = document.getElementById(id);
        if (!target) 
            return;
        if (typeof target.style.filter != 'undefined') {
            target.style.filter = "alpha(opacity:" + (opacity * 100) + ")";
        }
        else {
            target.style.opacity = opacity;
        }
    }

    function fade(type, _callback){
    
        setOpacity(opacitysetting, wargs.uuid + "_content");
        var _done = false;
        if (type == "in") {
            opacitysetting += 0.1;
            if (opacitysetting > .2) _widget.container.style.display = "block";    
            if (opacitysetting >= 1) 
                _done = true;
        } else 
            if (type == "out") {
                opacitysetting -= 0.1;
                if (opacitysetting <= 0) 
                    _done = true;
            }
        if (_done) {
            if (typeof _callback == "function") {
                _callback();
            }
        } else {
            setTimeout(function(){
                fade(type, _callback)
            }, fadeInterval);
        }
    }  
    
    _widget.timer = setTimeout(_widget.timerFunction, scrollTimeout);
    
    function pauseScroller() {
       scrollCarousel = false;
       setTimeout(function() {
           scrollCarousel = true;
           _widget.timer = setTimeout(_widget.timerFunction, scrollTimeout);
       }, 15000); 
    }
    
    this.loadTag = function(obj) {
    	var _tag;
    	// for action processing
    	if (obj.message) obj = obj.message;
    	if (obj.value) {  		
    		_tag = obj.value;
    		if (obj.value.value) _tag = obj.value.value;
    		else _tag = obj.value;
    	} else if (obj.label){
    		 _tag = obj.label
    	} else _tag = obj;
    	if (_tag) {  		
    		_widget.clear();
    		items = [];
    		index = 0;
    		_widget.getJSONFeed(tagService + _tag);
    	}
    };
    
    this.getJSONFeed = function(_url) {
       jmaki.publish("/showWait", {targetId : wargs.uuid, message : "Loading..."});    
        var _jfeed = _url + "&callback=jmaki.getWidget('" + wargs.uuid + "').processJSONFeed"; 
        jmaki.addLibraries([_jfeed], undefined,undefined, true);
    };

    this.addItem = function(item) {
        if (!item.id) item.id = items.length;        
        var id = item.id;
        var text = _widget.applyTemplate(item, template);

        if (typeof item.id == 'undefined') item.id =  counter++; 
        var div = document.createElement("div");
         _widget.container.appendChild(div); 
        div.innerHTML = text;
        
        setTimeout(function() {
            var ems = jmaki.getElementsByStyle("jmk-carousel-title");
            for (var i=0; ems && i < ems.length; i++) {
                ems[i].className += " jmk-carousel-title-" + currentTheme;
            }
        },0); 
            
        div.style.zIndex = 1;
        div.style.width = itemWidth + "px";
        div.style.height = itemHeight + "px";                   
        div.style.display = 'inline';
        if (horizontalScroll) {
            // get the content node
            var content = document.getElementById(wargs.uuid+ "_item_" + id);
            content.style.height = itemHeight - 50 + "px"     
           if (jmaki.MSIE)div.style.styleFloat = "left";
           div.style.cssFloat = "left";
        }

        div.id = wargs.uuid + "_item_" + item.id;
        item.div = div;

        items.push(item);
        var _sel = document.createElement("div");
        _sel.index = id;
        _sel.onclick = function(e) {
          if (scrolling) return;
          pauseScroller();
          var _t;
          if (!e) _t = window.event.srcElement;
          else _t = e.target;
           _widget.select(_t.index);
        };
        _sel.appendChild(document.createTextNode((id + 1) ));
        _sel.className = "jmk-carousel-id jmk-carousel-id-" + currentTheme;
        _widget.mid.appendChild(_sel);
        item.menu = _sel;
        return id;
    };
    
    this.select = function(itemId){
        for (var _i=0; _i < items.length; _i++) {
           var item = items[_i];
           if (item.id == itemId) {         
               _widget.showIndex(_i);
               item.menu.className += "jmk-carousel-id jmk-carousel-id-" + currentTheme + " jmk-carousel-id-selected-" + currentTheme;
           } else {
               item.menu.className = "jmk-carousel-id jmk-carousel-id-" + currentTheme;
           }
        }
    };
    
    this.render = function() {    

    }
    
    this.applyTemplate = function(obj, _t) {
        for (var i in obj) {
            var token = "@{" + i + "}";
            while (_t.indexOf(token) != -1) {
                _t = _t.replace(token, obj[i]);
            }
        }
        return _t;
    }

    this.showIndex = function(targetIndex) {
        if (targetIndex < index ||
            targetIndex >= index + count){               
            if (targetIndex < index) {           
               var _cb = function() {
                 scrolling = false;
                 index = targetIndex;
               };
               var targetPos = (targetIndex * itemWidth) * -1;
               scrolling = true;         
               doScroll(targetPos, _cb, false);
            } else if (targetIndex >= index + count) {
                var callback = function() {               
                   index = targetIndex;
                   scrolling = false;
                };
                var tp = (targetIndex * itemWidth) * -1;
                scrolling = true;              
                doScroll(tp, callback, true);
            }
        }  
    };
    
    function doScroll(target, callback, forward) {
          var start;
          if (horizontalScroll) start = new Number(_widget.container.style.left.split('px')[0]); 
          else start = new Number(_widget.container.style.top.split('px')[0]);

          var _inc = increment;
 
          if (forward == false){
             if (start >= target){        
                if (horizontalScroll)_widget.container.style.left = target + "px";
                else _widget.container.style.top = target + "px";
                callback(); 
             } else {
                  // slow down near target             
                  if (target - start < scroll / 2) _inc = _inc / 1.25;
                  else if (target - start < scroll * 3) _inc = _inc / 1.5;
                  
                  if (horizontalScroll) _widget.container.style.left = (start  + _inc) + "px";
                  else _widget.container.style.top = (start  + _inc) + "px";
                  setTimeout(function(){ doScroll(target, callback,forward);}, timeout);
             }
          } else if (forward == true){
             if (start <= target){
                 if (horizontalScroll)_widget.container.style.left = target + "px";
                 else _widget.container.style.top = target + "px";
                callback(); 
             } else {
                  // slow down near target
                  if (start - target < scroll / 2) _inc = _inc / 1.25;
                  else if (start - target < scroll * 3) _inc = _inc / 1.5;           
                  if (horizontalScroll)_widget.container.style.left = (start  - _inc) + "px";
                  else _widget.container.style.top = (start  - _inc) + "px";
                  setTimeout(function(){ doScroll(target, callback,forward);}, timeout);
             }          
          }
    }
   
    this.getNext = function() {
         if (index < items.length - 1) {
            _widget.select(items[index +1].id);
        }
    }
    
    this.getPrevious = function() {    
       var callback = function() {
           scrolling = false;
       };
       index -= scrollCount;
       if (index < 0) index = 0;
       var targetPos = (index * scroll) * -1;
       scrolling = true; 
       doScroll(targetPos, callback, false);

    }    
       
    this.clear = function() {
        _widget.container.innerHTML = "";
        _widget.container.style.top = "0px";
        index = 0;
        items = [];
    }

    this.setItems = function(_in) {
       var data;
       if (_in.dataType=="jmakiRSS") {
            data = data = jmaki.filter(_in, filter);
       } else {
           data = _in;
       }
       if (horizontalScroll) {
            _widget.container.style.width = ((itemWidth + 10) * data.length) + "px";
       }       
       for (var _i=0; _i < data.length && _i < itemCount; _i++) {       
         _widget.addItem(data[_i]);
       }        
       _widget.mid.style.width = (itemCount * 30) + "px";
       if (data.length && data.length > 0) _widget.select(0); 
    };

    function setArrowNav() {
    }
    
    this.postLoad = function() {
        _widget.init();
        if (wargs.value) {
            var data;
            if (wargs.args && wargs.args.filter) {
                data = jmaki.filter(wargs.value, wargs.args.filter);
            } else {
                data = wargs.value;
            }   
            _widget.setItems(data);
        } else if (wargs.service) {     
            var _url = wargs.service;
            jmaki.doAjax({url: _url, callback: function(req) {

                        var _in = eval('(' + req.responseText + ')');
                        if (_in.dataType) {
                            if (_in.dataType == "jmakiRSS") {
                              var data = jmaki.filter(_in, filter);
                            }  else {
                              data = _in;
                            }
                        } else {
                            data = _in;
                        }
                        _widget.setItems(data);
            }});
        } else if (feed) {
            _widget.getJSONFeed(feed);
        }
    };
 
    this.init = function() {
        if (!_widget.node) return;
        _widget.scrollpane = document.getElementById(wargs.uuid + "_scrollpane");
        _widget.nav  = document.getElementById(wargs.uuid + "_nav");
        _widget.mid  = document.getElementById(wargs.uuid + "_mid");
        _widget.container = document.getElementById(wargs.uuid + "_content");
        _widget.node.className += " jmk-carousel-" + currentTheme;      
        _widget.resize(); 
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/clear", _widget.clear);
            doSubscribe(subscribe[_i]  + "/setItems", _widget.setItems);
        }        
       // this is the template for all li
       template = unescape(document.getElementById(wargs.uuid + "_template").innerHTML + "");
    };
    
    this.resize = function() {
        var _dim = jmaki.getDimensions(_widget.node.parentNode, 52);
        var _w = _dim.w - 12;
        if (jmaki.MSIE) _w -= 8;
        _widget.scrollpane.style.width =  _w + "px";
        if (!_dim) return;
          
        if (/%/i.test(baseWidth)) {
            var _w = new Number(baseWidth.split('%')[0]);
            itemWidth = (_dim.w - 12) / _w * 100; 
        } else {
            itemWidth = baseWidth;
        }  
        for (var i=0; items &&  i < items.length; i++) {
            items[i].div.style.width = itemWidth + "px";
            items[i].div.style.height = itemHeight + "px";
        }
        _widget.node.style.height = _dim.h -12 + "px";
        if (showNav) {         
            _widget.nav.style.display = "block";   
            itemHeight = _dim.h - 42;
            _widget.scrollpane.style.height = _dim.h - 40 + "px";
        } else {
            itemHeight = _dim.h - 4;
            _widget.scrollpane.style.height = _dim.h -12 + "px";      
        }
    }
    
    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
        delete list;
        delete next;
        delete prev;
        delete container;        
    };       
    
    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);     
    }
    
  jmaki.widgets.jmaki.carousel.FeedburnerFilter = function(_in) {
    var _rows = [];

    for (var _i=0; _i < _in.feed.items.length;_i++) {
      var _des = _in.feed.items[_i].body;
      
        var rex = new RegExp("\\\\n", "g");
        _des =  _des.replace(rex," ");

        rex = new RegExp("\\\\\"","g");
        _des =  _des.replace(rex,"\"");
        var rex2 = new RegExp("<p>.*?</p>");
//      var content = _des.match(rex2)[0];
	    var content = _des;

        if (content.length > maxLength) {
          content = content.substring(0, maxLength) + "... ";
        }
        var row = {      
          title : _in.feed.items[_i].title,
          link : _in.feed.items[_i].link,
          date : _in.feed.items[_i].date,
          body : _des,
          shortContent : content
       };
      _rows.push(row);
    }
    return _rows;  
}   
}

jmaki.namespace("jmaki.extensions.jmaki.wait");

jmaki.extensions.jmaki.wait.Extension = function(eargs) { 
 this.showWait = function(args) {     
   if (args.targetId) {   
       var target = document.getElementById(args.targetId);        
       if (target) {
          var loc = jmaki.getPosition(target);
          var dim = jmaki.getDimensions(target);        
          var cw = target.clientWidth;
          var ch = target.clientHeight;
          if (ch==0) {
               ch = dim.h;
               cw = dim.w;
          }
          var splashW = 150;
          var splashH = 150;
          var iconW = 128;
          var iconH = 128;
          // resize the loader to be poportioinal
          if (ch < iconH && ch) {
              splashH = ch -10;
              splashW = splashH;
              iconH = splashH - 15;
              iconW = iconH;
          }
          var splash = document.createElement("div");
          splash.id = args.targetId + "_splash";
          splash.style.position = "absolute";
          splash.style.zIndex = 9998;

          document.body.appendChild(splash);

          splash.style.opacity = .65;
          // TODO : Need this for IE
          var icon = document.createElement("img");
          icon.style.position = "relative";
          icon.src = eargs.extensionDir + "/images/loading.gif";
       
          splash.appendChild(icon);
          icon.style.width = iconW + "px";
          icon.style.height = iconH + "px";
          icon.style.top = (splash.clientHeight / 2) - (iconH / 2)  +  "px";
                    
          if (args.message) {       
              var message = document.createElement("div");
              message.innerHTML = args.message;
              message.style.color = "#000";             
              message.style.position = "absolute";
              message.style.width = "97%";
              message.style.textAlign = "center";
              message.style.fontSize = "12px"; 
              message.style.top = iconH + 10 + "px";
              message.style.left = '5px';
              splash.appendChild(message);

              splashH += 25;
              splashW += 25;      
          }                    
       }
       if (typeof args.modal !='undefined' && args.modal == true) {
             splash.style.width = cw + "px";
             splash.style.height = ch + "px";
             splash.style.top = loc.y + "px";
             splash.style.left = loc.x + "px";              
         } else {
             splash.style.width = splashW + "px";
             splash.style.height = splashH + "px";
             splash.style.left = loc.x + (cw / 2) - (splashW / 2) +  "px";
             splash.style.top = loc.y  + (ch / 2) - (splashH / 2) +  "px";                            
         }
         icon.style.left = (splash.clientWidth / 2) - (iconW / 2)  +  "px";
     }

   };
  jmaki.subscribe("/showWait", this.showWait);
 
   this.hideWait = function(args) {
     if (args.targetId) { 
         var target = document.getElementById(args.targetId + "_splash");
         if (target) {
             target.parentNode.removeChild(target);
         }
     }
   };
   jmaki.subscribe("/hideWait", this.hideWait); 
};

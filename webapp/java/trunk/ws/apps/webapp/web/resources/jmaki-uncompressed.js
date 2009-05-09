
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
var _globalScope = this;

function Jmaki() {
    
    var _jmaki = this;    
    this.version = '.9.7.1';
    this.debugGlue = false;
    this.verboseDebug = false;
    this.debug = false;
    var widgets = [];
    this.loaded = false;
    this.initialized = false;
    this.webRoot = "";
    this.resourcesRoot = "resources";
    this.extensions = [];
    this.inspectDepth = 2;    
    var _counter = 0;
    this.MSIE  = /MSIE/i.test(navigator.userAgent);
    
    /**
     *  Generate a unqiue id
     */
    this.genId = function() {
        return "jmk_" + _counter++;    
    }
    
    this.Map = function() {
        /**
         * This class provides a hash table like utility
         */
        var map = {};
        /**
         * Get a list of the keys to check
         */
        this.keys = function() {
            var o = {};
            var _keys = [];
            
            for (var _i in map){
                // make sure we don't return prototype properties.
                if (typeof o[_i] == 'undefined') _keys.push(_i);
            }
            return _keys;
        }
        /**
         * Put stores the value in the table
         * @param key the index in the table where the value will be stored
         * @param value the value to be stored 
         */
        this.put = function(key,value) {
            map[key] = value;
        }
        
        /**
         * Return the value stored in the table
         * @param key the index of the value to retrieve
         */
        this.get = function(key) {
            return map[key];
        }
        
        /**
         * Remove the value from the table
         * @param key the index of the value to be removed
         */
        this.remove =  function(key) {
            delete map[key];
        }
        /**
         *  Clear the table
         */
        this.clear = function() {
            delete map;
            map = {};
        }
    }
    
    this.attributes = new this.Map();	
    var topics = new this.Map();
    
    /**
     *  Unsubscribe a listener
     *  @param listener 
     */
    this.unsubscribe = function(_lis) {
        
        for (var _l=0; _jmaki.subs && _l < _jmaki.subs.length;_l++ ) {
            if (_jmaki.subs[_l].id  == _lis.id) {
                _jmaki.subs.splice(_l,1);
                break;
            }
        }
    }  
	
    /**
     *  Publish an event to a topic
     *  @param name of the topic to be published to
     *  @param args any object
     */
    this.publish = function(name, args, bubbleDown, bubbleUp) {
        if (typeof name == 'undefined' || typeof args == 'undefined') return;      
        if (_jmaki.debugGlue) {
            _jmaki.log("<span style='color:red'>Publish </span> : Topic:" + name + " message: " + _jmaki.inspect(args));
        }
        // check the glue for listeners
        if (_jmaki.subs){
            for (var _l=0; _l < _jmaki.subs.length;_l++ ) {
                var _listener = _jmaki.subs[_l];             
                    if ((_listener.topic instanceof RegExp && _listener.topic.test(name)) || ( _listener.topic == name)   ||
                    (typeof _listener.topic.charAt == 'function' && _listener.topic.charAt(_listener.topic.length-1)== '*'  &&
                       (name.indexOf(_listener.topic.substring(0,_listener.topic.length -1)) == 0)  &&
                       name.substring(0, _listener.topic.length-1) == _listener.topic.substring(0,_listener.topic.length-1) ) ||
                    (typeof _listener.topic.charAt == 'function' &&_listener.topic.charAt(0) == '*' &&
                     (name.indexOf(_listener.topic.substring(1,_listener.topic.length)) != -1) &&
                    name.substring(name.indexOf(_listener.topic.substring(1,_listener.topic.length)) ,name.length) == _listener.topic.substring(1,_listener.topic.length) ) ||

                    (typeof _listener.topic.charAt == 'function' && _listener.topic.charAt(0) == '*'  &&
                       _listener.topic.charAt(_listener.topic.length-1)== '*'  &&
                        name.indexOf(_listener.topic.substring(1,_listener.topic.length-1)) != -1)
                     ) {
                         
                    if (_jmaki.debugGlue) _jmaki.log("<span style='color:green'>Publish Match</span> : Topic:" + name + " listener: " + _listener);                    
                    if (_listener.action == 'call' && _listener.target) {
                        // get the top level object                   
                        var _obj;
                        if (_listener.target.functionName) {
                            _obj = _jmaki.findObject(_listener.target.object);
                            // create an instance of the object if needed.
                            if (typeof _obj == 'function') {
                                myo = new _obj;
                            } else if (_obj) {
                                myo = _obj;
                            } else {
                                if (_jmaki.debugGlue) _jmaki.log("Publish Error :  Object found: " + _listener.target.object);
                            }
                            if (typeof myo != 'undefined' &&
                                typeof myo[_listener.target.functionName] == 'function'){
                                myo[_listener.target.functionName].call(_globalScope,args);
                            } else {
                                if (_jmaki.debugGlue) _jmaki.log("Publish Error : function " + _listener.target.functionName + " not found on object " + _listener.target.object); 
                            }                        
                        } else if  (_listener.target.functionHandler) {
                            _listener.target.functionHandler.call(_globalScope,args);
                        } else {
                            if (_jmaki.debugGlue) _jmaki.log("Publish Error : no fuctionName or functionHandler specified on listener mapped to " + name); 
                        }

                    }
                } else if (_jmaki.subs[_l].action == 'forward') {
                    var _topics = _jmaki.subs[_l].topics;
                    // now multiplex the event
                    for (var ti = 0; ti < _topics.length; ti++){
                        // don't cause a recursive loop if the topic is this one
                        if (_topics[ti] != name) {
                            _jmaki.publish(_topics[ti], args);
                        }
                    }
                }
            }
        }
        // publish to subframes with a global context appended
        var bd = true;
        if (typeof bubbleDown != 'undefined') bd = bubbleDown;
      
        if ( bd && window.frames.length > 0){
            for (var i=0; i < window.frames.length; i++){
              if (window.frames[i].jmaki){
                  window.frames[i].jmaki.publish("/global" + name, args, true, false);
              }
            }
        }
        //  publish to parent frame if we are a sub-frame. This will prevent duplicate events
        if (window != window.top){
            var bu = true;
            if (typeof bubbleUp != 'undefined') bu = bubbleUp;
              if (bu && window.parent.jmaki){
                  window.parent.jmaki.publish("/global" + name, args, false, true);
            }
        }        
    }

    /**
     * Load a set of libraries in order
     */
    this.addLibraries = function(_libs, _cb, _inprocess) {
        if (_libs.length <= 0) {
            if (typeof _cb == 'function') {
                _cb();
                return;
            }
        }
        if (typeof _inprocess == 'undefined') {
            _inprocess = new _jmaki.Map();
        }
        var _uuid = new Date().getMilliseconds();
        var _lib = _libs[_libs.length-1];
        var _s_uuid = "c_script_" + _libs.length + "_" + _uuid;
        var head = document.getElementsByTagName("head")[0];
        var e = document.createElement("script");
        e.start = _uuid;
        e.id =  _s_uuid;
        e.type = 'text/javascript';
        head.appendChild(e);
        var se = document.getElementById(_s_uuid);
        _inprocess.put(_s_uuid,_lib);
        var loadHandler = function (_id) {
            var t = document.getElementById(_id);
            if (t && t.timeoutHandler) {
               clearInterval(t.timeoutHandler.interval);
               delete t.timeoutHandler;
            }
            var _s = document.getElementById(_id);
            // remove the script node
            if (_s) _s.parentNode.removeChild(_s);
            _inprocess.remove(_id);
            var _cbk = _cb;
            if (_libs.length-1 > 0) {
                _libs.pop();
                _jmaki.addLibraries(_libs, _cb,_inprocess);
             /**  rather than check length check for inprocess **/
            } 
            if (_inprocess.keys().length == 0) {
                if (typeof _cb != 'undefined'){
                    var timout = 0;
                    delete _inprocess;
                    setTimeout(function(){_cbk();}, 0);
                }
            }
        }
        if (_jmaki.MSIE) {
            se.onreadystatechange = function () {
                if (this.readyState == 'loaded') {
                    var _id = _s_uuid;
                    loadHandler(_id);
                }
            }; 
            document.getElementById(_s_uuid).src = _lib;
        } else {   
            if (se.addEventListener) {
               // force a load using a timer if all else fails
               var loader = function(_id) {
                 var _c = 0;
                 var self = this;
                 this.interval = setInterval(function() {
                 if (_c>2){
                   clearInterval(self.interval);
                   loadHandler(_id);
                 } else {
                   _c++;
                 }
                   }, 250);
                }
              se.timeoutHandler = new loader(_s_uuid);
              se.addEventListener("load", function(){var _id = _s_uuid;loadHandler(_id)}, true);
            }
            setTimeout(function(){document.getElementById(_s_uuid).src = _lib;}, 0);
        }
        se = null;
        head = null;
    }

    /**
     *  Get the XMLHttpRequest object
     *
     */
    this.getXHR = function () {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        }
    }
    
    function handleAjaxError(_m, _r, args){
           if (args.onerror) {
                 args.onerror(_m,_r);
               } else {
             _jmaki.log("jMaki.doAjax Error: " + _m);
           } 
    }
    
    /**
    * Generalized XMLHttpRequest which can be used from evaluated code. Evaluated code is not allowed to make calls.
    * @param args is an object literal containing configuration parameters including method[get| post, get is default], body[bodycontent for a post], asynchronous[true is default]
    */

   this.doAjax= function(args) {
       if (typeof args == 'undefined' || !args.url) {
           _jmaki.log("jmaki.doAjax: url required");
           return;
       }
      
       var _req =  this.getXHR();
      
       var method = "GET";
       var async = true;
       var callback;
       var _c = false;
       if (args.timeout) {
           setTimeout(function(){
             if (_c == false) {
               _c = true;
               if (_req.abort) _req.abort();
               handleAjaxError("Request timed out", _req, args);
               return;
              }
           }, args.timeout);
       }
       
       if  (typeof args.asynchronous != 'undefined') {
            async=args.asynchronous;
       }
       if (args.method) {
            method=args.method;
       }
       if (typeof args.callback == 'function') {
           callback = args.callback;
       }
       var body = null;
       if (args.body) {
           body = args.body;
       } else if (args.content) {
           body = "";
           for (var l in args.content) {
               body = body +  l + "=" + encodeURIComponent(args.content[l]) + "&";
           }
       }
       if (async == true && _c == false) {
       	   _req.onreadystatechange = function() {
           if (_req.readyState ==4 && _c == false) {         
               _c = true;
               if ((_req.status == 200 || _req.status ==0) && callback) {
                callback(_req);
               } else if (_req.status != 200){
                   handleAjaxError("jmaki.doAjax error communicating with " + args.url, _req, args);
               }
            return;
           }
       	 }
       }
       try {
          if (!_c)_req.open(method, args.url, async);
       } catch(e) {
         handleAjaxError("jmaki.doAjax error creating xmlhttprequest", _req, args);
         return;
       }
       // add headers
       if (args.headers && args.headers.length > 0) {
           for (var _h=0;_h < args.headers.length; _h++) {
               _req.setRequestHeader(args.headers[_h].name, args.headers[_h].value);
           }
       }
       // customize the method
       if (args.method) {
            method=args.method;
            if (method.toLowerCase() == 'post') {
               if (!args.contentType) _req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            }
       }
       if (args.contentType) {
           _req.setRequestHeader("Content-Type", args.contentType);
       }
       try {
         if (_c == false) _req.send(body);         
       } catch(e) {
         handleAjaxError("Error sendong body of xmlhttprequest", _req, args);
         return;
       }       
       if (_c == false && async == false) {
            _c = true;
           if (_req.status ==200) {
                if (callback) callback(_req);
           } else {
               handleAjaxError("Error communicating with external service", _req, args);
           }
           return;
       }
    }
        
    /**
     *  Library name is added as a script element which will be loaded when the page is rendered
     *  @param lib library to add 
     */
    this.addLibrary = function(lib, cb) {
      var libs = [];
      libs.push(lib);
      return _jmaki.addLibraries(libs, cb);
    }
    
    /**
     * Register widget with jMaki 
     * @param widget Object respresenting the widget
     */
    this.addWidget = function(widget) {
        widgets.push(widget);
        if (this.loaded){this.loadWidget(widget);}
    }
 
     /**
     * Register widget with jMaki 
     * @param widget Object respresenting the widget
     */
    this.addExtension = function(ext) {
        _jmaki.extensions.push(ext);
    }
    
    /**
     * Bootstrap or load all registered widgets
     */
    this.bootstrapWidgets = function() {
        _jmaki.loaded = true;
        for (var l=0; l < widgets.length; l++) {
            this.loadWidget(widgets[l]);
        }
    }
    
    /**
     * Bootstrap or load all registered extensions
     */
    this.loadExtensions = function() {
        for (var l=0; l < _jmaki.extensions.length; l++) {
            this.loadExtension(_jmaki.extensions[l]);
        }
    }
  
   /**
     * Checks wheter a script has been loaded yet
     */
    this.writeScript = function(_s, _id) {
        if (_jmaki.loaded == true) {
            if (document.getElementById(_id)) {
                document.getElementById(_id).innerHTML = "Attempt to write a script that can not be dynamically load widget with  id " + _id + ". Consider using the widget in an iframe.";
            }
        } else {
            document.write("<script src='" + _s + "'></script>");
        }
    }
   
    /**
     * Loads the style sheet by adding a link element to the DOM 
     * @param target name of style sheet to load 
     */
    this.loadStyle = function(target) {
        var styleElement = document.createElement("link");
        styleElement.type = "text/css";
        styleElement.rel="stylesheet"
        if (target[0] == '/') target = _jmaki.webRoot + target;
        styleElement.href = target;
        if (document.getElementsByTagName('head').length == 0) {
            var headN = document.createElement("head");
            document.documentElement.insertBefore(headN, document.documentElement.firstChild);
        }
        document.getElementsByTagName('head')[0].appendChild(styleElement);
    }
    /**
     * Replace style class
     * @param root root of the oldStyle classes
     * @param oldStyle name of class or classes to replace
     * @param targetStyle name of new class or classes to use 
     */
    this.replaceStyleClass = function (root, oldStyle, targetStyle) {
        var elements = this.getElementsByStyle(oldStyle,root);
        for (var i=0; i < elements.length; i++) {
            // Handle cases where there are multiple classnames
            if (elements[i].className.indexOf(' ') != -1) {
                var classNames = elements[i].className.split(' ');
                for (var ci in classNames) {
                    if (classNames[ci] == oldStyle) {
                        classNames[ci] = targetStyle;
                    }
                }
                // now reset the styles with the replaced values
                elements[i].className = classNames.join(' ');
            } else  if (elements[i].className == oldStyle) {
                elements[i].className = targetStyle;
            }
        }
    }
    
    /**
    * Find a set of child nodes that contain the className specified
    * @param className is the targetClassName you are looking for
    * @param root  An optional root node to start searching from. The entire document will be searched if not specfied.
    *
    */
    this.getElementsByStyle = function(className, root){
        var elements = [];
        if (typeof root != 'undefined') {
            var rootNode = root;
            if (typeof root == 'string') {
                rootNode = document.getElementById(root);
            }    
            elements = this.getAllChildren(rootNode, []);
        } else {
            elements = (document.all) ? document.all : document.getElementsByTagName("*");
        }
	var found = [];
	for (var i=0; i < elements.length; i++) {
	// Handle cases where there are multiple classnames
            if (elements[i].className.indexOf(' ') != -1) {
                var cn = elements[i].className.split(' ');
                for (var ci =0; ci < cn.length; ci++) {
                    if (cn[ci] == className) {
                        found.push(elements[i]);
                    }
                }
            } else  if (elements[i].className == className) {
                found.push(elements[i]);
            }
        }
        return found;
    }
    
    /**
     * Utility Function to get children
     * @param element for which to get the children
     */
    this.getAllChildren = function(target, children) {
        var _nc = target.childNodes;
        for (var l=0; _nc && l <  _nc.length; l++) {
            if (_nc[l].nodeType == 1) {
                children.push(_nc[l]);
                if (_nc[l].childNodes.length > 0) {
                    this.getAllChildren(_nc[l], children);
                }
            }
        }
        return children;
    }
    
        /**
     * Load extension
     * @param extension Object representing widget to load
     */
    this.loadExtension = function(_ext) {
        var targetName ="jmaki.extensions." + _ext.name + ".Extension";    
        var con = this.findObject(targetName);
        if (typeof con != "function") {
            jmaki.log("Could not find widget constructor for: " + targetName + ". Please make sure the extension constructor is properly defined.");
        } else {
          var ex = new con(_ext);
          if (ex.postLoad) ex.postLoad.call(_globalScope);
        }
    }
    
    
    /**
     * Load a widget
     * @param widget Object representing widget to load
     */
    this.loadWidget = function(_jmw) {
        // see if the widget has been defined.
        if (_jmaki.attributes.get(_jmw.uuid) != null) {
            return;
        }
        var targetName ="jmaki.widgets." + _jmw.name + ".Widget";     
        var con = this.findObject(targetName);       
        if (typeof con != "function") {
            logError("Could not find widget constructor for: " + targetName + ". Please make sure the widget constructor is properly defined.", document.getElementById(_jmw.uuid));
        }
        var wimpl;
        // bind the value using a @{foo.obj} notation       
        if ((typeof _jmw.value == 'string') && _jmw.value.indexOf("@{") == 0) {      
            var _vw = /[^@{].*[^}]/.exec(_jmw.value);
            _jmw.value = _jmaki.findObject(new String(_vw));
        }
        // do not wrap IE with exception handler
        // because we cant' get the right line number
        var _uuid = _jmw.uuid;      
        if (_jmaki.MSIE) {
            var _uuid = _jmw.uuid;
            var oldError = null;
            if (window.onerror) {
                oldError = window.onerror;
            }
            var eh = function(message, url, line) {
                var _puuid = _uuid;
                logWidgetError(targetName, _puuid,url, line, message, document.getElementById(_puuid));
            }
            window.onerror = eh;
            wimpl = new con(_jmw);
            window.onerror = null;
            if (oldError) {
                window.onerror = oldError;
            }              
        } else if (typeof con == 'function'){
          try {
                wimpl = new con(_jmw);
           } catch (e){
                var line = "unknown";
                var description = null;
                if (e.lineNumber) line = e.lineNumber;
                if (e.message) description = e.message;
 
                if (_jmaki.debug) {
                    logWidgetError(targetName, _jmw.uuid,_jmw.script, line, description , document.getElementById(_jmw.uuid));
                    return;
                }
            }
        } else if (typeof con == 'undefined') {          
            logError("Unable to find widget constructor " + targetName + " check log and make sure constructor is defined.", document.getElementById(_jmw.uuid));
            return;
        }
        if (typeof wimpl == 'object') {
            _jmaki.attributes.put(_jmw.uuid, wimpl);
            if (wimpl.postLoad) wimpl.postLoad.call(_globalScope);
            // map in any subscribe handlers.
            if (_jmw.subscribe && _jmw.subscribe.push) { //string also have length property
                for (var _wi = 0; _wi < _jmw.subscribe.length; _wi++) {
                    var _t = _jmw.subscribe[_wi].topic;
                    var _m = _jmw.subscribe[_wi].handler;
                    var _h = null;
                    if (typeof _m == 'string' && _m.indexOf("@{") == 0) {
                         var _hw = /[^@{].*[^}]/.exec(_m);
                        _h = _jmaki.findObject(new String(_hw));
                    } else if (wimpl[_m]) {
                        _h = wimpl[_m];
                    }       
                    if (_h != null) _jmaki.subscribe(_jmw.subscribe[_wi].topic,_h);
                }
            }
            _jmaki.publish("/jmaki/runtime/widget/loaded", { id : _jmw.uuid});
        } else {          
            logError("Unable to create an instance of " + targetName + ". See the error log for more details.", document.getElementById(_jmw.uuid ));
        }
    }
    
    function logWidgetError(name,uuid, url, line, _m, div) {
        var message= "<span>Error loading " + name + " : id=" + uuid + "<br>" +
        "Script: "  + url + " (line:" + line + ")." +
        "<br>Message: " + _m + "</span>";
        logError(message, div);
    }
 
    function logError(message, div) {
        if (div == null || typeof div.className == 'undefined') {
            div = document.createElement("div");
            document.body.appendChild(div);
        }
        div.className = "";
        div.style.color = "red";
        div.innerHTML = message;
    }
    /**
     * An easy way to get a instance of a widget.
     * returns null if their is not a widget with the id.
     */
    this.getWidget = function(id) {
        return _jmaki.attributes.get(id);
    }
    
    /**
     * destroy all registered widgets under the target node
     * @param _root - The _root to start at. All widgets will be removed if not specified.
     */
    this.clearWidgets = function(_root) {
        if (typeof _root == "undefined") {
            var _k = _jmaki.attributes.keys();
            for (var l=0; l < _k.length; l++) {
                _jmaki.removeWidget(_k[l]);            
            }
            _jmaki.loaded = false;
            widgets = [];
        } else {
            var _ws = _jmaki.getAllChildren(_root,[]);           
            for (var l=0; l < _ws.length; l++) {
                var _w;
                if (_ws[l].id) _jmaki.removeWidget(_ws[l].id);          
            }
        }
    }
    
    this.removeWidget = function(_wid) {
        var _w = _jmaki.getWidget(_wid);
        if ( _w  && typeof _w.destroy == 'function') {
            _w.destroy();
            var _p = document.getElementById(_wid);
            _p.parentNode.removeChild(_p);
        } 
        _jmaki.attributes.remove(_wid);   
    }
    

    this.inspect = function(_o, _depth) {
        if (typeof _depth == "undefined"){
            _depth =0;
        } else  if (_depth >= _jmaki.inspectDepth) {
            return _o;
        } else {
            _depth++;
        }

        var _rs = [];
        if (typeof _o == 'undefined')_o = this;
        if (_o instanceof Array) {
            for (var i=0; i < _o.length; i++) {
                _rs.push(i + " : " + _jmaki.inspect(_o[i],_depth));
            }
            return "[" +  _rs.join(" , ") + "]";     
        } else if (typeof _o == "string") {
           return "'" + _o + "'";
        } else if (typeof _o == "number" || 
            typeof _o == "boolean") {
           return _o;           
        } else if (typeof _o == "object") {
            for (var _oi in _o) {
                try {
                if (_oi != 'toString')_rs.push(_oi  + " : " + _jmaki.inspect(_o[_oi ], _depth));
                } catch(e){}
            }
            if (_rs.length >0) return "{" + _rs.join(" , ") + "}";
            else return "{}";
        } else return _o;
    }
    
    /*
     * Add a glue listener programatcially. following is an example.
     *
     *{topic : "/dojo/fisheye",action: "call", target: { object: "jmaki.dynamicfaces",functionName: "fishEyeValueUpdate"}}
     *   or 
     * @param l as topic and 
     * @param t as the target object path ending with a function 
     */
    this.subscribe = function(l, t) {    
        if (typeof l == 'undefined') return;
        // handle key word arguments
        if (typeof l == 'object' && !(l instanceof RegExp)) {          
            if (l.topic) l.topic = _jmaki.trim(l.topic);
            if (l.topicRegExp) l.topic = new RegExp(l.topicRegExp);
            lis = l;
        // function binding
        } else if (typeof t == 'string'){
          lis = {};     
          if (l.topicRegExp) lis.topic = new RegExp(l.topicRegExp);
          else lis.topic = l;
          lis.target = {};
          var _is = t.split('.');
          lis.action = "call";
          lis.target.functionName = _is.pop();
          lis.target.object = _is.join('.');
        // inline function
        } else if (typeof t == 'function') {
          lis = {};
          if (l.topicRegExp) lis.topic =  new RegExp(l.topicRegExp);
          else lis.topic = l;          
          lis.target = {};
          lis.action = "call";
          lis.target.functionHandler = t;
        } else {
          _jmaki.log("jmaki:subscribe error:  Handler undefined for " + l);
        }
        if (typeof lis != 'undefined'){
            if (typeof _jmaki.subs == 'undefined')_jmaki.subs = [];            
            if (!lis.id) lis.id = _jmaki.genId();
            if (typeof lis.topic == "string") {
            	// trim
            	lis.topic = _jmaki.trim(lis.topic);
            	var _wc = lis.topic.indexOf("*");
            	// look for embedded wildcards
            	if (_wc != -1 &&
            	    _wc != 0 &&
            	    _wc != lis.topic.length -1) {            	   
            	    lis.topic = new RegExp(lis.topic.substring(0, _wc) + "(.*)" + lis.topic.substring(_wc + 1, lis.topic.length)) ;
            	} 
            }
            if (lis.topic){
                lis.toString = _jmaki.inspect;
                _jmaki.subs.push(lis);
                
            }else {
                _jmaki.log("jmaki:subscribe error:  topic or topicRegExp required for " + l);
                return null;
            }
            return lis;
        }
        return null;
    }
    
    this.trim = function(t) {
        return  t.replace(/^\s+|\s+$/g, "");
    }
    
    // for backwards compatibility
    this.addGlueListener = this.subscribe;
    
    /*
     * @param _src is the source object
     * @param _par is the class to extend
     */
    this.extend = function(_src, _par) {
        _src.prototype = new _par();
        _src.prototype.constructor = _src;
        _src.superclass = _par.prototype;
        for (i in _par) {
            _src.prototype[i] = _par[i];
        }
    }
    
    this.hideLogger = function() {
      if (jmaki.ld)_jmaki.ld.style.visibility = 'hidden';
    }
    
    this.clearLogger = function() {
      var b = document.getElementById("jmakiLoggerContent");
      if (b) b.innerHTML = "";
    }
    
    this.log = function(text, level) {
      
        if (!_jmaki.debug ) return;
        // cached MSIE messages until after the page has been created
        if (_jmaki.MSIE && !_jmaki.initialized) {          
            if (!_jmaki._messages) _jmaki._messages = [];
            _jmaki._messages.push({ text : text, level : level});
            return;
        }
        var ld = document.getElementById("jmakiLogger");
        var b = document.getElementById("jmakiLoggerContent");        
        if (!ld){      	
            ld = document.createElement("div");
            ld.id = 'jmakiLogger';
            ld.style.border = "1px solid #000000";
            ld.style.fontSize = "12px";
            ld.style.position  = "absolute";
            ld.style.zIndex  = "999";
            ld.style.bottom = "0px";
            ld.style.background = "#FFFF00";
            ld.style.right ="0px";
            ld.style.width = "500px";
            ld.style.height = "200px";    
            var tb = "<div  style='height: 14px; background : black; color : white; font-size : 10px'><div style='float:left;width:450px;text-align : center; '>jMaki Logger</div><div style='right: 0px,text-align:left'><a href='javascript:jmaki.clearLogger()' title='Hide' style='color:white; text-decoration: none'>[Clear]</a> <a href='javascript:jmaki.hideLogger()' title='Hide' style='color:white; text-decoration: none'>[X]</a></div></div>";
            var tbE = document.createElement("div");
            tbE.innerHTML = tb;
            ld.appendChild(tbE);
            b = document.createElement("div");
            b.id ='jmakiLoggerContent';
            b.style.height = "186px";
            b.style.overflowY = "auto";
            ld.appendChild(b);
            if (document.body) {
               document.body.appendChild(ld);
            }

        }
        if (ld && jmaki.loaded)ld.style.visibility = 'visible' ;
        var lm = document.createElement("div");
        lm.style.clear = "both";
        if (text.length > 125 && _jmaki.verboseDebug == false) {
            var lid = _jmaki.genId();           
            var tn = document.createElement("div");
            tn.innerHTML = "<div style='float:left;width:435px;height:12px;overflow:hidden'>" + text.substring(0,135) + "</div><div style='float:left'>...&nbsp;</div><a id='" + lid + "_href' href=\"javascript:jmaki.showLogMessage(\'" + lid +  "\')\" style='text-decoration: none'><span id='" + lid + "_link'>[more]</span></a>";
            var mn = document.createElement("div");
            mn.id = lid;
            mn.innerHTML = text;    
            mn.style.margin = "5px";
            mn.style.background = "#FF9900";
            mn.style.display = "none";
            lm.appendChild(tn);
            lm.appendChild(mn);
        } else lm.innerHTML =  text;    
        if (b)b.appendChild(lm);
    }
    
    this.showLogMessage = function(id) {
        var n = document.getElementById(id);
        if (n && n.style){
            n.style.display = "block";
            var h = document.getElementById(id + "_href");
            h.href = "javascript:jmaki.hideLogMessage('" + id + "')";
            var l = document.getElementById(id + "_link");
            l.innerHTML = "&nbsp;[X]"
        }   
    }

    this.hideLogMessage = function(id) {
        var n = document.getElementById(id);
        if (n && n.style){
            n.style.display = "none";
            var h = document.getElementById(id + "_href");
            h.href = "javascript:jmaki.showLogMessage('" + id + "')";
            var l = document.getElementById(id + "_link");
            l.innerHTML = "[more]"            
        }      
    }    
    
    /**
     * Initialize jMaki by loading the config.json
     *  Write in the glue by loading dependencies and
     *  Register listeners.
     */
    this.initialize = function() {     
        if (!_jmaki.config)_jmaki.config = {};
        if (!_jmaki.config.glue)_jmaki.config.glue = {};
        jmaki.doAjax({ url : this.webRoot + this.resourcesRoot + "/config.json",
             asynchronous : false,
             timeout : 3000,
             callback :  function(req) {
                if (req.responseText != '') {
                    var obj = eval('(' + req.responseText + ')');
                    if (obj.config) {
                        _jmaki.config.theme = obj.config.theme;
                        // write out the dependent libraries so we have access
                        if (obj.config.glue.timers) {  
                            _jmaki.addTimers(obj.config.glue.timers);
                        }
                        if (obj.config.gluelisteners){
                            for (var gl=0; gl < obj.config.glue.listeners.length;gl++) {
                                  _jmaki.subscribe (obj.config.glue.listeners[gl]); 
                            }
                        }
                    }
                }
        }
        });
        postInitialize();
    }
    
    var timers = [];
    
    /**
     * Create a namespace with the given string
     */
    this.namespace = function(_path, target) {
        // get the top level object
        var paths = _path.split('.');
        var _obj = _globalScope[paths[0]];
        if (typeof _obj == 'undefined') _globalScope[paths[0]] = _obj = {};
        for (var ii = 1; ii < paths.length; ii++) {
            if (typeof _obj[paths[ii]] != 'undefined' ) {
                _obj = _obj[paths[ii]];                                       
            } else {
                _obj[paths[ii]] = {};
                _obj = _obj[paths[ii]];
            }
        }
        // if object provided it becomes the last in the chain
        if (typeof target == 'object') {
            _obj = target;
        }
        return _obj;
    }
    
    this.findObject = function(_path) {
        var paths = _path.split('.');
        var _obj = _globalScope[paths[0]];
        var found = true;
        if (typeof _obj != 'undefined' ){
            for (var ii =1; ii < paths.length; ii++) {
                var _lp = paths[ii];
                if (_lp.indexOf('()') != -1){                  
                  var _ns = _lp.split('()');
                  if (typeof _obj[_ns[0]] == 'function'){
                      var _fn = _obj[_ns[0]];              
                      return _fn.call(_globalScope);
                  }
                }     
                if (typeof _obj[_lp] != 'undefined' ) {
                    _obj = _obj[_lp];                                       
                    found = true;
                } else {
                    found = false;
                    break;
                }
            }
            if (found) {
                return _obj;
            }
        }
        return null;
    }
    
    this.Timer = function(args, isCall) {
        var _src = this;
        this.args = args;
        var _target;
        
        this.processTopic = function() {
            for (var ti = 0; ti < args.topics.length; ti++){
                _jmaki.publish(args.topics[ti], {topic: args.topics[ti],
                type:'timer',
                src:_src,
                timeout: args.to});
            }
        }
        
        this.processCall = function() {
            if (!_target) {
             var  _obj = _jmaki.findObject(args.on);
                if (typeof _obj == 'function'){
                    _target = new _obj();
                } else if (typeof _obj == 'object'){
                    _target = _obj;
                }
            }
            if ((_target && typeof _target == 'object')) {
              if(typeof _target[args.fn] == 'function') {
                _target[args.fn]({type:'timer', src:_src, timeout: args.to});
              }
            }
        }
        
        this.run = function() {
            if (isCall) {
                _src.processCall();
            } else {
                _src.processTopic();
            }
            _globalScope.setTimeout(_src.run,args.to);
        }
    }
    
    this.addTimer = function(_timer){
        var timers = [];
        timers.push(_timer);
        this.addTimers(timers);
    }
    
    this.addTimers = function(_timers){
        if (typeof _timers != 'undefined'){
            for (var _l=0; _l <_timers.length;_l++ ) {
                // create a wrapper and add the timer
                var _timer = _timers[_l];              
                if (_timer.action == 'call' &&
                _timer.target != 'undefined' &&
                _timer.target.object != 'undefined' &&
                _timer.target.functionName != 'undefined' &&
                typeof _timer.timeout != 'undefined') {
                    // create the timer
                    var args = {on: _timer.target.object,
                    fn: _timer.target.functionName,
                    to: _timer.timeout
                    }
                    var timer = new _jmaki.Timer(args,true);
                    timers.push(timer);
                    timer.run();
                    
                } else if (_timers[_l].action == 'publish') {
                    var args = {topics: _timers[_l].topics,
                    to: _timer.timeout
                    }
                    var timer = new _jmaki.Timer(args,false);
                    timers.push(timer);
                    timer.run();
                }
            }            
        }
    }
    
    function postInitialize() {
        if (_jmaki.initialized) return;
        else _jmaki.initialized = true;
        // log any messages that might be queued up during pre-init
        if (_jmaki._messages) {
            for (var i=0; i < _jmaki._messages.length; i++) {
                var _m = _jmaki._messages[i];
                _jmaki.log(_m.text, _m.level);
            }
        }
        _jmaki.publish("/jmaki/runtime/intialized", {});
        _jmaki.loadExtensions();
        _jmaki.publish("/jmaki/runtime/extensionsLoaded", {});
        _jmaki.bootstrapWidgets();

        _jmaki.publish("/jmaki/runtime/widgetsLoaded", {});
        // load the theme
        if ( _jmaki.config && _jmaki.config.theme) {
            var theme = _jmaki.config.theme;
            if (!/(^http)/i.test(theme)) theme = _jmaki.webRoot + theme;             
            _jmaki.loadStyle(theme);
        }
        _jmaki.publish("/jmaki/runtime/loadComplete", {});
    }
    /**
     *  All for a filter to be applied to a dataset
     *  @param input - An object you wish to filter
     *  @param a string representing the path to the object or
     *    a funciton reference to procress the input
     */
    this.filter = function(input, filter){
        if (typeof filter == 'string') {
            var h = _jmaki.findObject(filter);
            return h.call(_globalScope,input);
        } else if (typeof filter == 'function'){
            return filter.call(_globalScope, input);
        }
    }
    
    /**
     *  Find the postion of an Element
     *
     */  
  this.getPosition =  function(_e) {
      var pX = 0;        
      var pY = 0;
      if (typeof _e.offsetParent == 'number') {
         pX = _e.offsetLeft;        
         pY = _e.offsetTop;
         while (_e.offsetParent) {
              _e = _e.offsetParent;
              pX += _e.offsetLeft;          
              pY += _e.offsetTop;
          }
      }
      return {x: pX, y: pY};          
  }
    
  this.DContainer = function(args){    
        var _self = this;
        var uuid;
        var _container;
       
        if (typeof args.target == 'string') {
            uuid = args.target;
            _container = document.getElementById(target);
        } else {
            uuid = args.target.id;
            _container = args.target;      
        }
        if (typeof overflow != 'undefined' && _container.style.overflow) {
            _container.style.overflow = 'true';
        }
        var oldWidth;
        this.url = null;
        var autoSizeH = false;
        var autoSizeW = false;
        
        if (args.autosize) {
            autoSizeH = true;
            autoSizeW = true;
        }
        
        // default sizes are all based on the width of the container   
        var VIEWPORT_WIDTH;
        var VIEWPORT_HEIGHT;
        
        this.loadURL = function(_url){
            if (_url.message) _url = _url.message;            
            if (typeof _url == 'string') { 
                _self.url = _url; 
            } else if (_url.url) {               
                _self.url = _url.url;
            } else if (_url.value) {
                _self.url = _url.value;
            }
            if (args.useIframe) {
                // wait for the iframe if it hasn't loaded
                if (!_self.iframe) {
                    var _t = setInterval(function() {
                        if (document.getElementById(uuid + "_iframe")) {
                            clearInterval(_t);               
                            _self.iframe = document.getElementById(uuid + "_iframe");
                            _self.iframe.src =  _self.url;
                            init();
                        }
                    }, 5);
              } else {
                _self.iframe.src = _self.url;
                }
            } else {        
                _jmaki.injector.inject({url: _self.url, injectionPoint: _container});
            }
        }

        this.resize = function() {
            var pos = _jmaki.getPosition(_container);           
            if (autoSizeH || autoSizeW){
                if (!_container.parentNode) return;
                var pos = _jmaki.getPosition(_container);
                if (_container.parentNode.nodeName == "BODY") {
                    if (window.innerHeight){
                        VIEWPORT_HEIGHT = window.innerHeight - pos.y -40;
                        VIEWPORT_WIDTH = window.innerWidth - 20;
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
                            VIEWPORT_WIDTH = _tNode.clientWidth -20;
                            VIEWPORT_HEIGHT = _tNode.clientHeight - pos.y - 15;
                        }
                    }
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
                        VIEWPORT_WIDTH = _tNode.clientWidth;
                        VIEWPORT_HEIGHT = _tNode.clientHeight;
                    }
                }         
                if (autoSizeH) {                  
                    if (VIEWPORT_HEIGHT < 0) VIEWPORT_HEIGHT = 320;
                    _container.style.height = VIEWPORT_HEIGHT + "px";
                }
                if (autoSizeW) {
                    _container.style.width = VIEWPORT_WIDTH + "px";
                }
            } else {
                _container.style.width = VIEWPORT_WIDTH + "px";
                _container.style.height = VIEWPORT_HEIGHT + "px";          
            }
            if (VIEWPORT_HEIGHT < 0) {
                VIEWPORT_HEIGHT = 320;
            }
            if (VIEWPORT_WIDTH < 0) {
                VIEWPORT_WIDTH = 500;
            }
            
            if (args.useIframe) {
                if (_self.iframe) {                  
                    _self.iframe.style.height = VIEWPORT_HEIGHT + "px";
                    _self.iframe.style.width = VIEWPORT_WIDTH + "px";
                }
            }
            // used for tracking with IE
            oldWidth = document.body.clientWidth;           
        }

        this.setContent = function(_c) {
            var _con;
            if (_c.message)_c = _c.message;
            if (_c.value) _con = _c.value;
            else _con = _c;
            if (!_self.iframe)_container.innerHTML = _con;
        }        
        
        function init() {
            if (window.attachEvent) {
                window.attachEvent('onresize', layout);
            } else if (window.addEventListener) {
                window.addEventListener('resize', layout, true);
            }     
            if (args.startWidth) {
                VIEWPORT_WIDTH = Number(args.startWidth);
                _container.style.width = VIEWPORT_WIDTH + "px";
            } else {
                VIEWPORT_WIDTH = _container.clientWidth;
                autoSizeW = true;
            }
            
            if (args.startHeight) {
                VIEWPORT_HEIGHT = Number(args.startHeight);
            } else {
                VIEWPORT_HEIGHT = _container.clientHeight;
                autoSizeH = true;
            }
            if (VIEWPORT_HEIGHT <= 0) VIEWPORT_HEIGHT = 320;
            _container.style.height = VIEWPORT_HEIGHT + "px";
            if (args.useIFrame &&  _self.iframe) {
                _self.iframe.style.height = VIEWPORT_HEIGHT + "px";
            }
     
            // listen for load requests
            // TODO for backward compatibility (for now)
            if (args.topic) {
                _jmaki.subscribe(args.topic, _self.loadURL);
                _jmaki.subscribe(args.topic  + "/setInclude", _self.loadURL);
                _jmaki.subscribe(args.topic  + "/setContent", _self.setContent);
            }
            _self.resize();
            if (args.url && !args.useIframe) {
                _self.loadURL(args.url);
            } else if (args.content && !_self.iframe) {            
                _container.innerHTML = args.content;
            }
        }
                
        var resizing = false;
        var lastSize = 0;
        
        function layout() {
            if (!_jmaki.MSIE) {
                _self.resize();
                return;
            }
            // special handling for ie resizing.
            // we wait for no change for a full second before resizing.
            if (oldWidth != document.body.clientWidth && !resizing) {
                if (!resizing) {
                    resizing = true;
                    setTimeout(layout,500);
                }
            } else if (resizing && document.body.clientWidth == lastSize) {
                resizing = false;
                _self.resize();
            } else if (resizing) {
                lastSize = document.body.clientWidth;
                setTimeout(layout, 500);
            }
        }
        
        if (args.useIframe && args.useIframe == true) {
            var srcString = "";
            if (args.url) srcString = "src='" + args.url + "'";
            var content = "Loading...";
            if (args.content) content = args.content;
            // use this technique as creating the iframe programmatically does not allow us to turn the border off
            var iframeTemplate = "<IFRAME ID='" + uuid + "_iframe' " + srcString + " FRAMEBORDER=0 SCROLLING=" + ((!args.overflow) ? 'NO' : 'YES') + "></IFRAME>";
            _container.innerHTML = iframeTemplate;
            // wait for the iframe
            var _t = setInterval(function() {
                if (document.getElementById(uuid + "_iframe")) {
                    clearInterval(_t);               
                    _self.iframe = document.getElementById(uuid + "_iframe");
                    setTimeout(function(){init();},0);
                }
            }, 5);
        } else init();
    }
    
    this.destroy = function() {
        if (window.attachEvent) {
            window.dettachEvent('onresize', layout);
        } else if (window.addEventListener) {
            window.removeEventListener('resize', layout, true);
        } 
  } 
   
    
  this.Injector = function() {
 
  var _uuid = new Date().getMilliseconds();
  var _injector = this;
  var _processing = false;

  var styles = [];
  
  var tasks = [];
  
  this.inject = function(task) {
   // make sure jmaki creates a list of libraries it can not load
    if (tasks.length == 0 && !_processing) {
        inject(task);
    } else {
        tasks.push(task);
    }
  }

  /**
   * 
   * Load template text aloing with an associated script
   * 
   * Argument p properties are as follows:
   *
   * url :              Not required but used if you want to get the template from
   *                    something other than the injection serlvet. For example if
   *                    you want to load content directly from a a JSP or HTML file.
   * 
   * p.injectionPoint:  Not required. This is the id of an element into. If this is
   *                    not specfied a div will be created under the root node of
   *                    the document and the template will be injected into it.
   *                    Content is injected by setting the innerHTML property
   *                    of an element to the template text.
   */
  function inject(task) {
      _processing = true;
      _jmaki.doAjax({
            method:"GET",
            url: task.url,
            asynchronous: false,
            callback: function(req){
                   getContent(req.responseText, task);               
               //if no parent is given append to the document root   
               var injectionPoint;
               if (typeof task.injectionPoint == 'string') {
                   injectionPoint = document.getElementById(task.injectionPoint);
                   // wait for the injection point
                   if (!document.getElementById(task.injectionPoint)) {
                       var _t = setInterval(function() {
                           if (document.getElementById(task.injectionPoint)) {
                               clearInterval(_t);
                               injectionPoint = document.getElementById(task.injectionPoint);
                               setTimeout(function(){processTask(injectionPoint,task);},0);                      
                           }
                       }, 25);
                   } else {
                       processTask(injectionPoint, task);             
                   }
                } else {
                    processTask(task.injectionPoint, task);
                }
         },
         onerror : function(){
            var ip = task.injectionPoint;
            if (typeof task.injectionPoint == 'string') {
                ip = document.getElementById(task.injectionPoint);
            }
            ip.innerHTML = "Unable to load URL " + task.url + "."
            processNextTask();
         }

       });
  }
  
  function processTask(injectionPoint, task) {
      _jmaki.clearWidgets(injectionPoint);
      var _id = "injector_" + _uuid;
      var data = task.content + "<div id='" + _id + "'></div>";
      injectionPoint.innerHTML = data;
      // wait for the content to be loaded
      var _t = setInterval(function() {
          if (document.getElementById(_id)) {
              clearInterval(_t);
              try {
                  _injector.loadScripts(task,processNextTask);
              } catch (e) {
                  injectionPoint.innerHTML = "<span style='color:red'>" + e.message + "</span>";
              }
          }
      }, 25);
  }
  
  // pass in a reference to the task
  // start the next task
  function processNextTask() {
      if (tasks.length >0) {
          var _t = tasks.shift();
          inject(_t);
      };
      _processing = false;
  }
  

  /**
   * 
   * Load template text aloing with an associated script
   * 
   * Argument p properties are as follows:
   *
   * url :              Not required but used if you want to get the template from
   *                    something other than the injection serlvet. For example if
   *                    you want to load content directly from a a JSP, JSF call, PHP, or HTML file.
   */
  this.get = function (p) {
      var _data;
       _jmaki.doAjax({
            method:"GET",
            url: p.url,
            asynchronous: false,
            callback: function(req){
                _data = getContent(req.responseText);
            }
           }
           );
           return _data;
  }

  /**
   * If were returning an text document remove any script in the
   * the document and add it to the global scope using a time out.
   */
  function getContent(rawContent, _task) {
   
   _task.embeddedScripts = [];
   _task.embeddedStyles = [];
   _task.scriptReferences = [];
   _task.styleReferences = [];
  
    var _t = rawContent;
    var bodyText = "";

    // recursively go through and weed out the scripts

    var gscripts = document.getElementsByTagName("script");
    var gstyles = document.getElementsByTagName("link");
    while (_t.indexOf("<script") != -1) {
            var realStart = _t.indexOf("<script");
            var scriptSourceStart = _t.indexOf("src=", (realStart));
            var scriptElementEnd = _t.indexOf(">", realStart);
            var end = _t.indexOf("</script>", (realStart)) + "</script>".length;
            if (realStart != -1 && scriptSourceStart != -1) {
                var scriptSourceName;
                var scriptSourceLinkStart= scriptSourceStart + 5;
                var quoteType =  _t.substring(scriptSourceStart + 4, (scriptSourceStart +5))
                var scriptSourceLinkEnd= _t.indexOf("\"", (scriptSourceLinkStart + 1));
              	scriptSourceLinkEnd= _t.indexOf(quoteType, (scriptSourceLinkStart + 1));
                if (scriptSourceStart < scriptElementEnd) {
                    scriptSourceName = _t.substring(scriptSourceLinkStart, scriptSourceLinkEnd);
                    // prevent multiple inclusions of the same script
                    var exists = false;
                    for (var i = 0; i < gscripts.length; i++) {
                        if (typeof gscripts[i].src) {
                            if (gscripts[i].src == scriptSourceName) {
                                exists = true;
                                break;
                            }
                        }
                    }
                    if (!exists) {
                        _task.scriptReferences.push(scriptSourceName);
                    }
                }
            }
           // now remove the script body
           var scriptBodyStart =  scriptElementEnd + 1;
           var sBody = _t.substring(scriptBodyStart, end - "</script>".length);
           if (sBody.length > 0) {
              	_task.embeddedScripts.push(sBody);
           }
           //remove script
           _t = _t.substring(0, realStart) + _t.substring(end, _t.length);
           scriptSourceLinkEnd = -1;
      }
      while (_t.indexOf("<style") != -1) {
           var realStart = _t.indexOf("<style");
           var styleElementEnd = _t.indexOf(">", realStart);
           var end = _t.indexOf("</style>", (realStart)) ;
           var styleBodyStart =  styleElementEnd + 1;
           var sBody = _t.substring(styleBodyStart, end);
           if (sBody.length > 0) {
              _task.embeddedStyles.push(sBody);
           }
           //remove style
           _t = _t.substring(0, realStart) + _t.substring(end + "</style>".length, _t.length);
        }
        // get the links    
        while (_t.indexOf("<link") != -1) {
            var realStart = _t.indexOf("<link");
            var styleSourceStart = _t.indexOf("href=", (realStart));
            var styleElementEnd = _t.indexOf(">", realStart) +1;
            if (realStart != -1 && styleSourceStart != -1) {
                var styletSourceName;
                var styleSourceLinkStart= styleSourceStart + 6;
                var quoteType =  _t.substring(styleSourceStart + 5, (styleSourceStart + 6))                
                var styleSourceLinkEnd= _t.indexOf(quoteType, (styleSourceLinkStart + 1));
                if (styleSourceStart < styleElementEnd) {
                    styleSourceName = _t.substring(styleSourceLinkStart, styleSourceLinkEnd);
	              	var exists = false;
                        for (var i = 0; i < gstyles.length; i++) {
                            if (typeof gstyles[i].src != 'undefined') {
                                if (gstyles[i].src == styleSourceName) {
                                    exists = true;	
                                }
                            }
                        }
		          if (!exists) {
		          	_task.styleReferences.push(styleSourceName);
	    	      }
                }
                //remove style
                _t = _t.substring(0, realStart) + _t.substring(styleElementEnd, _t.length);
            }
        }
        
        var head = document.getElementsByTagName("head")[0];
        
        // inject the links
        for(var loop = 0; _task.styleReferences && loop < _task.styleReferences.length; loop++) {
            var link = document.createElement("link");
            link.href = _task.styleReferences[loop];
            link.type = "text/css";
            link.rel = "stylesheet";
            head.appendChild(link);
        }
        
        var stylesElement;
        if (_task.embeddedStyles.length > 0) {
            stylesElement = document.createElement("style");
            stylesElement.type="text/css";
            var stylesText;
            for(var loop = 0; loop < _task.embeddedStyles.length; loop++) {
                stylesText = stylesText + _task.embeddedStyles[loop];
            }
            if (document.styleSheets[0].cssText) {
               document.styleSheets[0].cssText = document.styleSheets[0].cssText + stylesText;
            } else {
                stylesElement.appendChild(document.createTextNode(stylesText));
                head.appendChild(stylesElement);
            }
        }
        _task.content = _t;
      }
  
      this.loadScripts = function(task, initFunction) {    
          var _loadEmbeded = function() {
              // evaluate the embedded javascripts in the order they were added
              for(var loop = 0;task.embeddedScripts && loop < task.embeddedScripts.length; loop++) {
                  var script = task.embeddedScripts[loop];
                  // append to the script a method to call the scriptLoaderCallback
                  eval(script);
                  if (loop == (task.embeddedScripts.length -1)) {
                      if (typeof initFunction != 'undefined') initFunction();
                      return;
                  }
              }
              if (task.embeddedScripts && task.embeddedScripts.length == 0 && typeof initFunction != 'undefined') initFunction();
          }
          if (task.scriptReferences && task.scriptReferences.length > 0){
              // load the global scripts before loading the embeded scripts
              return _jmaki.addLibraries(task.scriptReferences.reverse(),_loadEmbeded);   
          } else {
              _loadEmbeded();
          }
          return true;
    }
  }
  this.injector = new this.Injector(); 
}

if (typeof jmaki == 'undefined') {
    var jmaki = new Jmaki();  
    jmaki.widgets = {};

    var oldLoad  = window.onload;
    
    /**
     * onload calls bootstrap function to initialize and load all registered widgets
     * override initial onload.
     */
    window.onload = function() {
        if (!jmaki.initialized) {
            jmaki.initialize();
        } else {
            jmaki.bootstrapWidgets();
            return;
        }
        if (typeof oldLoad  == 'function') {
            oldLoad();
        }
    }
}
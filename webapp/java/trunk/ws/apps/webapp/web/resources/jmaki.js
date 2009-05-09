var _globalScope = window;

function Jmaki() {
    
    var _jmaki = this;    
    this.version = '1.8.1';
    this.debugGlue = false;
    this.verboseDebug = false;
    this.debug = false;
    var widgets = [];
    this.loaded = false;
    this.initialized = false;
    this.webRoot = "";
    this.resourcesRoot = "resources";
    this.preextensions = [];
    this.inspectDepth = 2;
    var timers = [];
    this.publishToParent = false;
    this.displayErrorsInline = true;
    
    var _doc = document;
    var _counter = 0;
    this.MSIE  = /MSIE/i.test(navigator.userAgent);
    
    
    /**  Map is a general map object for storing key value pairs
      * 
      *  @param mixin - default set of properties
      *
      */
    this.Map = function(mixin) {
 
        var map;
        if (typeof mixin == 'undefined') map = {};
        else map = mixin;
        
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
        };
        /**
         * Put stores the value in the table
         * @param key the index in the table where the value will be stored
         * @param value the value to be stored 
         */
        this.put = function(key,value) {
            map[key] = value;
        };
        
        /**
         * Return the value stored in the table
         * @param key the index of the value to retrieve
         */
        this.get = function(key) {
            return map[key];
        };
        
        /**
         * Remove the value from the table
         * @param key the index of the value to be removed
         */
        this.remove =  function(key) {
            delete map[key];
        };
        /**
         *  Clear the table
         */
        this.clear = function() {
            delete map;
            map = {};
        };
    };
    
    /**
     *  Generate a unqiue id
     */
    this.genId = function() {
        return "jmk_" + _counter++;    
    };
    
    /**
     * Utility function to see if a variable is defined.
     */
    function isDefined(_target) {
        return (typeof _target != "undefined");
    }    
    
    // default locale - fallback if all fails
    this.defaultLocale = "jmaki-en-us";
    
    // default locale for messages
    this.locale = "jmaki-en-us";
    // localized messages    
    
    this.defaultMessages = {
        "request_timeout" : "Request for {0} timed out.",
        "invalid_json" : "Invalid JSON.",
        "publish_function_not_found" : "Publish Error : function {0} not found on object {1}",
        "publish_object_not_found" : "Publish Error :  Object not found: {0}",
        "publish_match" : "<span style='color:green'>Subscribe Match :</span> : Topic: {0} listener {1}",
        "publish" : "<span style='color:red'>Publish </span> : Topic: {0} message {1}",
        "subscribe_handler_required" : "Subscribe Error : Handler required for subscriber {0}.",
        "subscribe_topic_required" : "Subscribe Error : topic or topicRegExp required for {0}.",
        "ajax_url_required" : "doAjax error: url required.",
        "ajax_error" : "jMaki.doAjax Error: {0}",        
        "ajax_request_open_error" : "doAjax error making request: {0}",
        "ajax_send_body_error" : "doAjax error sending body of request to {0}.",
        "ajax_server_error" : "doAjax error communicating with {0}. Server returned status code {1}.",
        "write_dynamic_script_error" : "Attempt to write a script that can not be dynamically load widget with  id {0}. Consider using the widget in an iframe.",
        "widget_constructor_not_found" : "Unable to find widget constructor for {0}.",
        "extension_constructor_not_found" : "Unable to find extension constructor for {0}.",
        "widget_instantiation_error" : "Unable to create an instance of {0}. Enable logging for more details.",
        "unknown" : "unknown",
        "widget_error" : "<span>Error loading {0} : id={1}<br>Script: {2} (line: {3}).<br>Message: {4}</span>",
        "jmaki_logger" : "jMaki Logger",
        "clear" : "Clear",
        "x_close" : "[X]",
        "clear_logger" : "Clear Logger",
        "hide_logger" : "Hide Logger",
        "more" : "[more]",
        "jmaki_version" : "jMaki Version : {0}",
        "unable_to_load_url" : "Unable to load URL {0}."   
    };
    
    // Localized messages 
    // where the first set is a set of languages
    this.messages = new this.Map();
    
    this.dcontainers =  new this.Map();
    
    this.extensions = new this.Map();
    
    /**
     * Add a set of localzied messages
     *
     * @param locale - locale for the messages
     * @param messages - object literal of messsages in key value pairs
     *
     */
    this.addMessages = function(locale, messages) {
        _jmaki.messages.put(locale, new _jmaki.Map(messages));
    };
    
    // This map is intialized with the default languages and messages
    _jmaki.addMessages(_jmaki.defaultLocale,_jmaki.defaultMessages);
    
    /**
     * Get a localized message for the given id.
     *
     * @param id - The message id
     * @param args - If provided these will be used to format the message
     *
     * @return null if the message could not be found
     *
     * Messages will be searched for jmaki.locale messages and then
     * the jmaki.defaultLocale messages.
     *
     */
    this.getMessage = function(id,args) {
        var message = null;
        if (_jmaki.messages.get(_jmaki.locale) &&
            _jmaki.messages.get(_jmaki.locale).get(id)) {
            message = _jmaki.messages.get(_jmaki.locale).get(id);
        // fallback
        } else if (_jmaki.messages.get(_jmaki.defaultLocale) &&
            _jmaki.messages.get(_jmaki.defaultLocale).get(id)) {
            message = _jmaki.messages.get(_jmaki.defaultLocale).get(id);
        }
        if (isDefined(args)) {
            return _jmaki.messageFormat(message,args);    
        }
        return message;        
    };
    
    this.messageFormat = function(message, args) {
        for (var i=0; isDefined(message) && isDefined(args) && i < args.length; i++) {
            var rex = new RegExp("\\{" + i + "\\}", "g");
            message =  message.replace(rex, args[i]);
        }
        return message;
    };
        
    this.attributes = new this.Map();	
 
    function getElement(id) {
        return _doc.getElementById(id);
    }
   
    /**
     *  Unsubscribe a listener
     *  @param _lis 
     */
    this.unsubscribe = function(_lis) { 
        for (var _l=0; _jmaki.subs && _l < _jmaki.subs.length;_l++ ) {
            if (_jmaki.subs[_l].id  == _lis.id) {
                _jmaki.subs.splice(_l,1);
                break;
            }
        }
    };  
	
	function matchWildcard(pattern,topic) {
		
		var patpos = 0;
		var patlen = pattern.length;
		var strpos = 0;
		var strlen = topic.length;

		var i=0;
		var star = false;

		while (strpos+i<strlen) {
			if (patpos+i<patlen) {
				switch (pattern.charAt(patpos + i)) {
				case '?': {
					i++;
					continue;
				}
				case '*':
					star = true;
					strpos += i;
					patpos += i;
					do {
						++patpos;
						if (patpos == patlen)
							return true;
					} while (pattern.charAt(patpos) == '*');
					i=0;
					continue;
				}
				if (topic.charAt(strpos + i) != pattern.charAt(patpos + i)) {
					if (!star) return false;
					strpos++;
					i=0;
					continue;
				}
				i++;
			} else {
				if (!star) return false;
				strpos++;
				i=0;
			}
		}
		do {
			if (patpos + i == patlen) return true;
		} while(pattern.charAt(patpos + i++)=='*');
		return false;
	}
	
	
    /**
     *  Publish an event to a topic
     *  @param name Name of the topic to be published to
     *  @param args Palyoad of the publish
     *  @param bubbleDown Sends events down to children if set
     *  @param bubbleUp Sends event to parent contexts if they contain jmaki object and current context created by jmaki
     */
    this.publish = function(name, args, bubbleDown, bubbleUp) {
        if (!isDefined(name) || !isDefined(args)) return;  
        if (_jmaki.debugGlue) {
            _jmaki.log(jmaki.getMessage("publish",  [name ,_jmaki.inspect(args)]));
        }

        // check the glue for listeners
        if (_jmaki.subs){
            for (var _l=0; _l < _jmaki.subs.length;_l++ ) {
                var _listener = _jmaki.subs[_l];
                     if ((_listener.topic instanceof RegExp &&
_listener.topic.test(name)) 
                   || _listener.topic == name   
                   || (typeof _listener.topic.charAt == 'function' &&
matchWildcard(_listener.topic,name))
                   ) {     
 
                    // set the topic on payload
                    args.topic = name;
                    if (_jmaki.debugGlue) {
                    	var _vname = name;
	                if (_listener.topicString) _vname = _listener.topicString;   
                    	_jmaki.log(jmaki.getMessage("publish_match", [_vname, _listener]));                    
                    }
                    if (_listener.action == 'call' && _listener.target) {
                        // get the top level object                   
                        var _obj;
                        var myo = 'undefined';
                        if (_listener.target.functionName) {						
                            _obj = _jmaki.findObject(_listener.target.object);  									                           
                            // create an instance of the object if needed.
                            if (typeof _obj == 'function') {
                                myo = new _obj;
                            } else if (_obj) {
                                myo = _obj;
                            } else {
                              _jmaki.log(_jmaki.getMessage("publish_function_not_found",
                                   [_listener.target.functionName,_listener.target.object]));
                            }					
                            if (isDefined(myo) &&
                                typeof myo[_listener.target.functionName] == 'function'){
                                myo[_listener.target.functionName].call(window,args);
                            } else {
                                   _jmaki.log(_jmaki.getMessage("publish_object_not_found",
                                        [_listener.target.functionName,_listener.target.object]));
                            }                        
                        } else if (_listener.target.functionHandler) {
                            _listener.target.functionHandler.call(window,args);
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
        if (isDefined(bubbleDown)) bd = bubbleDown;
        if (bd && window.frames && window.frames.length > 0) {
            var _frames = _jmaki.dcontainers.keys();
            for (var i=0; i < _frames.length; i++){
              var _dc = _jmaki.dcontainers.get(_frames[i]);
              if (_dc.iframe && !_dc.externalDomain && window.frames[_dc.uuid + "_iframe"] && window.frames[_dc.uuid + "_iframe"].jmaki){
                  window.frames[_dc.uuid + "_iframe"].jmaki.publish("/global" + name, args, true, false);
              }
            }
        }
        //  publish to parent frame if we are a sub-frame. This will prevent duplicate events
        if (_jmaki.publishToParent){
            var bu = true;
            if (isDefined(bubbleUp)) bu = bubbleUp;
              if (bu && window.parent.jmaki){
                  window.parent.jmaki.publish("/global" + name, args, false, true);
            }
        }        
    };

    /**
     * Load a set of libraries in order and call the callback function
     */
    this.addLibraries = function(_o, _cb, _inp, _cu) {
   
        // check to see if anything is still processing and if not 
        // call the callback      
        var checkQueue = function() {
            if (_inprocess.keys().length == 0) {            
                if (isDefined(_cb)){                                    
                    setTimeout(function(){_cb();}, 0);
                }
                delete _inprocess;
                _jmaki.processingScripts = false;
                updateAjaxQueue(); 
            }
           return;  
       };  
        var _libs;
        var _inprocess;
        var _cleanup = true;
        // overload the function to allow for object literals
        if (_o instanceof Array) {
             _libs = _o;
             _inprocess = _inp;
             _cleanup = _cu;
        } else {
            _libs = _o.libs;
            _cb = _o.callback;
            _inprocess = _o.inprocess;
            _cleanup = _o.cleanup;          
        }
        // queue the request if there are scripts being loaded.
        // this prevents the 2 connections from being sucked up.
        if (!isDefined(_inprocess) && (_jmaki.processingScripts ||
            _jmaki.processingAjax)) {
            _inprocess = new _jmaki.Map();
            if (!isDefined(_jmaki._scriptQueue)) {       
                _jmaki._scriptQueue =[];
            }        
            _jmaki._scriptQueue.push({libs : _libs, callback : _cb, cleanup : _cleanup});
            return;
        } else if (!isDefined(_inprocess)) {
            _jmaki.processingScripts = true;
            _inprocess = new _jmaki.Map();
        }
         
        if (_libs.length <= 0) {
            checkQueue();
        }
        var _uuid = new Date().getMilliseconds();
        var _lib = _libs[_libs.length-1];               
        var _s_uuid = "c_script_" + _jmaki.genId();
        var head = _doc.getElementsByTagName("head")[0];
        var e = _doc.createElement("script");
        e.start = _uuid;
        e.id =  _s_uuid;
        head.appendChild(e);
        
        var se = getElement(_s_uuid);
        _inprocess.put(_s_uuid,_lib);
        var loadHandler = function (_id) {                 
            var _s = getElement(_id);               
            // remove the script node
            if (_s  && !(isDefined(_cleanup) && _cleanup == false)) _s.parentNode.removeChild(_s);
            _inprocess.remove(_id);
            if (_libs.length-1 > 0) {           
                _libs.pop();
                _jmaki.addLibraries({ libs : _libs, callback : _cb, inprocess : _inprocess, cleanup : _cleanup});
            }
            checkQueue();
        };
        
        // wait for the script to be laoded
        if (_jmaki.MSIE) {
            se.onreadystatechange = function () {
                if (this.readyState == 'loaded') {
                    var _id = _s_uuid;
                    loadHandler(_id);
                }
            }; 
            getElement(_s_uuid).src = _lib;
        } else {
            // the onload handler works on opera, ff, safari
            // and the addEventListener will not work on opera
            se.onload = function(){            
              	  var _id = _s_uuid;
                  loadHandler(_id);
            };
            setTimeout(function(){
            	getElement(_s_uuid).src = _lib;
            	}, 0);
        }
    };
    


    /**
     *  Get the XMLHttpRequest object
     *  
     *  Allow for config override to allow for older ActiveX XHR for local file
     *  System with IE7
     *
     */
    this.getXHR = function () {
        if (window.XMLHttpRequest &&
             !( _jmaki.MSIE &&
              typeof(_jmaki.config.forceActiveXXHR) == "boolean" &&
             _jmaki.config.forceActiveXXHR == true)) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } else return null;
    };
    
    function handleAjaxError(_m, _r, args){
       if (args.onerror) {
             args.onerror(_m,_r);
           } else {
         _jmaki.log(_jmaki.getMessage("ajax_error", [_m]));
       } 
    }
    
    function updateAjaxQueue() {
		if (_jmaki.ajaxRequestQueue &&
		_jmaki.ajaxRequestQueue.length > 0) {
	        _jmaki.doAjax(_jmaki.ajaxRequestQueue.pop());
		} else {
		    _jmaki.processingAjax = false;
		    if (_jmaki._scriptQueue && _jmaki._scriptQueue.length >0) {
                    var _n = _jmaki._scriptQueue[0];
                   _jmaki._scriptQueue.shift();
                   _jmaki.addLibraries(_n);
                } 
		}
    }
    /**
    * Generalized XMLHttpRequest which can be used from evaluated code. Evaluated code is not allowed to make calls.
    * @param args is an object literal containing configuration parameters including method[get| post, get is default], body[bodycontent for a post], asynchronous[true is default]
    */
   this.doAjax= function(args) {
       if (typeof args == 'undefined' || !args.url) {
           _jmaki.log(_jmaki.getMessage("ajax_url_required"));
           return;
       }
       // sync up the processing queues for script and ajax loading
       // synchronous requests should not be stopped
       if ((_jmaki.processingScripts && args.asynchronous) ||
           (_jmaki.processingAjax && 
           args.asynchronous) ) {
           	if (!_jmaki.ajaxRequetQueue) _jmaki.ajaxRequestQueue = [];
           	_jmaki.ajaxRequestQueue.push(args);     	
           	return;
       }
       _jmaki.processingAjax = true;
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
               handleAjaxError(_jmaki.getMessage("request_timeout", [args.url]), _req, args);
               updateAjaxQueue();
               return;
              }
           }, args.timeout);
       }
       
       if  (isDefined(args.asynchronous)) {
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
                   _c = true;
                   handleAjaxError(_jmaki.getMessage("ajax_server_error", [args.url, _req.status]), _req, args);
               }
               updateAjaxQueue();
            return;
           }
       	 }
       }
       try {
          if (!_c)_req.open(method, args.url, async);
       } catch(e) {         
         _c = true;
         handleAjaxError(_jmaki.getMessage("ajax_request_open_error", [args.url]),_req, args);
         updateAjaxQueue();
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
         _c = true;
         handleAjaxError(_jmaki.getMessage("ajax_send_body_error", [args.url]), _req, args);
         updateAjaxQueue();
         return;
       }       
       if (_c == false && async == false) {
           _c = true;
           if (_req.status ==200 || _req.status ==0) {
                if (callback) callback(_req);
           } else {
               _c = true;
               handleAjaxError(_jmaki.getMessage("ajax_server_error", [args.url,_req.status]), _req, args);
           }
           updateAjaxQueue();
           return;
       }
    };
        
    /**
     *  Library name is added as a script element which will be loaded when the page is rendered
     *  @param lib library to add
     *  @param cb Callback handler
     */
    this.addLibrary = function(lib, cb) {
      var libs = [];
      libs.push(lib);
      return _jmaki.addLibraries({libs : libs, callback : cb});
    };
    
    /**
     * Register widget with jMaki 
     * @param widget Object respresenting the widget
     */
    this.addWidget = function(widget) {
        widgets.push(widget);
        if (this.loaded){this.loadWidget(widget);}
    };
 
     /**
     * Register widget with jMaki 
     * @param ext Object respresenting the extension params
     */
    this.addExtension = function(ext) {
        _jmaki.preextensions.push(ext);
    };
    
    /**
     * Register widget with jMaki 
     * @param id The id of the extension
     */
    this.getExtension = function(id) {
        return _jmaki.extensions.get(id);
    };
    
    /**
     * Bootstrap or load all registered widgets
     */
    this.bootstrapWidgets = function() {
        _jmaki.loaded = true;
        for (var l=0; l < widgets.length; l++) {
            _jmaki.loadWidget(widgets[l]);
        }
    };
    
    /**
     * Bootstrap or load all registered extensions
     */
    this.loadExtensions = function() {
        for (var l=0; l < _jmaki.preextensions.length; l++) {
            _jmaki.loadExtension(_jmaki.preextensions[l]);
        }
    };
  
   /**
     * Checks wheter a script has been loaded yet
     */
    this.writeScript = function(_s, _id) {
        if (_jmaki.loaded == true) {
            if (getElement(_id)) {
                getElement(_id).innerHTML = _jmaki.getMessage("write_dynamic_script_error", [_id]);
            }
        } else {
            _doc.write("<script src='" + _s + "'></script>");
        }
    };
   
    /**
     * Loads the style sheet by adding a link element to the DOM 
     * @param target name of style sheet to load 
     */
    this.loadStyle = function(target) {
        var styleElement = _doc.createElement("link");
        styleElement.type = "text/css";
        styleElement.rel="stylesheet";
        if (target[0] == '/') target = _jmaki.webRoot + target;
        styleElement.href = target;
        if (_doc.getElementsByTagName('head').length == 0) {
            var headN = _doc.createElement("head");
            _doc.documentElement.insertBefore(headN, _doc.documentElement.firstChild);
        }
        _doc.getElementsByTagName('head')[0].appendChild(styleElement);
    };
    
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
    };
    
    /**
    * Find a set of child nodes that contain the className specified
    * @param className is the targetClassName you are looking for
    * @param root  An optional root node to start searching from. The entire document will be searched if not specfied.
    *
    */
    this.getElementsByStyle = function(className, root){
        var elements = [];
        if (isDefined(root)) {
            var rootNode = root;
            if (typeof root == 'string') {
                rootNode = getElement(root);
            }
            elements = this.getAllChildren(rootNode, []);           
        } else {
            elements = (_doc.all) ? _doc.all : _doc.getElementsByTagName("*");
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
    };
    
    /**
     * Utility Function to get children
     * @param target Element for which to get the children. All document chilren are loaded if not specified
     * @param children An array used interally to build up a list of children found
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
    };
    
    /**
     * Load extension
     * @param _ext Object representing widget to load
     */
    this.loadExtension = function(_ext) {
        if (_jmaki.extensions.get(_ext)) return;
        var targetName ="jmaki.extensions." + _ext.name + ".Extension";
        var con = _jmaki.findObject(targetName);
        if (typeof con != "function") {
            _jmaki.log(_jmaki.getMessage("extension_constructor_not_found", [targetName]));
        } else {
          var ex = new con(_ext);
          if (ex.postLoad) ex.postLoad.call(window);
          _jmaki.extensions.put(_ext.name, ex);
        }
        
    };
    
    
    /**
     * Load a widget
     * @param _jmw Object representing widget to load
     */
    this.loadWidget = function(_jmw) {
        // see if the widget has been defined.
        if (_jmaki.attributes.get(_jmw.uuid) != null) {
            return null;
        }
        var targetName ="jmaki.widgets." + _jmw.name + ".Widget";     
        var con = _jmaki.findObject(targetName);       
        if (typeof con != "function") {
            logError(_jmaki.getMessage("widget_constructor_not_found", [targetName]), getElement(_jmw.uuid));
            return null;
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
            var oldError = null;
            if (window.onerror) {
                oldError = window.onerror;
            }
            var eh = function(message, url, line) {
                var _puuid = _uuid;
                logWidgetError(targetName, _puuid,url, line, message, getElement(_puuid));
            };
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
                var line = _jmaki.getMessage("unknown");
                var description = null;
                if (e.lineNumber) line = e.lineNumber;
                if (e.message) description = e.message;
 
                if (_jmaki.debug) {
                    logWidgetError(targetName, _jmw.uuid,_jmw.script, line, description , getElement(_jmw.uuid));
                    return null;
                }
            }
        }
        if (typeof wimpl == 'object') {
            _jmaki.attributes.put(_jmw.uuid, wimpl);           
            if (wimpl.postLoad) wimpl.postLoad.call(window);
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
            return wimpl;
        } else { 
            logError(_jmaki.getMessage("widget_instantiation_error",[targetName]), getElement(_jmw.uuid ));
        }
        return null;
    };
    
    function logWidgetError(name,uuid, url, line, _m, div) {
        var message= _jmaki.getMessage("widget_error", [name, uuid, url, line, _m]);
        logError(message, div);
    }
 
    function logError(message, div) {        
        if (_jmaki.displayErrorsInline) {
            if (!isDefined(div) || !div) div = _doc.createElement("div");           
            div.className = "";
            div.style.color = "red";            
            _doc.body.appendChild(div);
           div.innerHTML = message;
        } else {
            _jmaki.log(message);
        }
    }
    
    /**
     * An easy way to get a instance of a widget.
     * returns null if their is not a widget with the id.
     */
    this.getWidget = function(id) {
        return _jmaki.attributes.get(id);
    };
    
    /**
     * destroy all registered widgets under the target node
     * @param _root - The _root to start at. All widgets will be removed if not specified.
     */
    this.clearWidgets = function(_root) {

        if (!isDefined(_root)) {
            var _k = _jmaki.attributes.keys();
            for (var l=0; l < _k.length; l++) {
                _jmaki.removeWidget(_k[l]);            
            }
            _jmaki.loaded = false;
            widgets = [];
        } else {
           var _ws = _jmaki.getAllChildren(_root,[]);            
           for (var ll=0; ll < _ws.length; ll++) {         
                if (_ws[ll].id) _jmaki.removeWidget(_ws[ll].id);          
            }
        }
    };
    
    this.removeWidget = function(_wid) {
        var _w = _jmaki.getWidget(_wid);   
        if (_w) {          
            if ( typeof _w.destroy == 'function') {
                _w.destroy();
            }
            var _p = getElement(_wid);
            if(null != _p) _p.parentNode.removeChild(_p);
        }
        _jmaki.attributes.remove(_wid);   
    };


    this.inspect = function(_o, _inspectDepth, _currentDepth) {      
        var _ind = _jmaki.inspectDepth;
        var _cd = 0;
        
        if (typeof _inspectDepth == "number"){
            _ind =_inspectDepth;
        }       
        if (typeof _currentDepth != "undefined"){
            _cd = _currentDepth;
        } 
        if (_cd >= _ind && _ind != -1) {
           if (typeof _o == "string") {
               return "'" + _o + "'";
           } else  return _o;
        } else  {
            _cd++;
        }
              
        var _rs = [];
        if (typeof _o == "undefined")return 'undefined';
     
        if (_o instanceof Array) {
            for (var i=0; i < _o.length; i++) {               
                _rs.push(_jmaki.inspect(_o[i],_ind,_cd));
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
                    if (typeof _o[_oi] != "function") _rs.push(_oi  + " : " + _jmaki.inspect(_o[_oi],_ind,_cd));
                } catch(e){}
            }
            if (_rs.length > 0) {
                 return "{" + _rs.join(" , ") + "}";
            }
            else return "{}";
        } else return _o;
    };
    
    /*
     * Add a glue listener programatcially. following is an example.
     *
     *{topic : "/dojo/fisheye",action: "call", target: { object: "jmaki.dynamicfaces",functionName: "fishEyeValueUpdate"}}
     *   or 
     * @param l as topic and 
     * @param t as the target object path ending with a function 
     */
    this.subscribe = function(l, t) {    
        if (!isDefined(l)) return null;
        // handle key word arguments
        var lis;
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
          _jmaki.log(jmaki.getMessage("subscribe_handler_required", [l]));
        }
        if (isDefined(lis)){
            if (!isDefined(_jmaki.subs))_jmaki.subs = [];            
            if (!lis.id) lis.id = _jmaki.genId();
            if (lis.topic){
                lis.toString = function() { return _jmaki.inspect(this)};
                _jmaki.subs.push(lis);     
            } else {
                _jmaki.log(jmaki.getMessage("subscribe_topic_required", [l]));
                return null;
            }
            return lis;
        }
        return null;
    };
    
    this.trim = function(t) {
        return  t.replace(/^\s+|\s+$/g, "");
    };
        
    /*
     * @param _src is the source object
     * @param _par is the class to extend
     */
    this.extend = function(_src, _par) {
        _src.prototype = new _par();
        _src.prototype.constructor = _src;
        _src.superclass = _par.prototype;
        for (i in _src.prototype) {
            _src[i] = _src.prototype[i];
        }
    };
    
    this.hideLogger = function() {
      var ld = getElement("jmakiLogger");
      if (ld)ld.style.visibility = 'hidden';
    };
    
    this.clearLogger = function() {
      var b = getElement("jmakiLoggerContent");
      if (b) b.innerHTML = "";
    };
    
    this.log = function(text, level) {
        // cached messages until after the page has been created
        if (!_jmaki.initialized) {          
            if (!_jmaki._messages) _jmaki._messages = [];
            _jmaki._messages.push({ text : text, level : level});
            return;
        }
        if (!_jmaki.debug ) return;
        var ld = getElement("jmakiLogger");
        var b = getElement("jmakiLoggerContent");        
        if (!ld){      	
            ld = _doc.createElement("div");
            ld.id = 'jmakiLogger';
            ld.style.border = "1px solid #000000";
            ld.style.fontSize = "12px";
            ld.style.position  = "absolute";
            ld.style.zIndex  = "999";
            ld.style.bottom = "0px";
            ld.style.background = "#FFFF00";
            ld.style.right ="0px";
            ld.style.width = "600px";
            ld.style.height = "300px";
            
            var tb = "<div  style='height: 14px; background : black; color : white; font-size : 10px'>" +
                     "<div style='float:left;width:545px;text-align:center'>" +
                     _jmaki.getMessage("jmaki_logger") + 
                     "</div><div style='right:0px,text-align:left'><a href='javascript:jmaki.clearLogger()' title='" +
                     _jmaki.getMessage("clear_logger") +
                     "' style='color:white;text-decoration:none'>[" +
                     _jmaki.getMessage("clear") +
                     "]</a> <a href='javascript:jmaki.hideLogger()' title='" +
                     _jmaki.getMessage("hide_logger") +
                      "' style='color:white;text-decoration:none'>" + 
                      _jmaki.getMessage("x_close") + 
                      "</a></div></div>";
            var tbE = _doc.createElement("div");
            tbE.innerHTML = tb;
            ld.appendChild(tbE);
            b = _doc.createElement("div");
            b.id ='jmakiLoggerContent';
            b.style.height = "286px";
            b.style.overflowY = "auto";
            ld.appendChild(b);
            if (_doc.body) {
               _doc.body.appendChild(ld);
            }

        }
        if (ld && _jmaki.loaded)ld.style.visibility = 'visible' ;
        var lm = _doc.createElement("div");
        lm.style.clear = "both";
        if (text && text.length > 125 && _jmaki.verboseDebug == false) {
            var lid = _jmaki.genId();           
            var tn = _doc.createElement("div");
            tn.innerHTML = "<div style='float:left;width:535px;height:12px;overflow:hidden'>" + text.substring(0,135) + "</div><div style='float:left'>...&nbsp;</div><a id='" + lid + "_href' href=\"javascript:jmaki.showLogMessage(\'" + lid +  "\')\" style='text-decoration: none'><span id='" + lid + "_link'>" + _jmaki.getMessage("more") + "</span></a>";
            var mn = _doc.createElement("div");
            mn.id = lid;
            mn.innerHTML = text;    
            mn.style.margin = "5px";
            mn.style.background = "#FF9900";
            mn.style.display = "none";
            lm.appendChild(tn);
            lm.appendChild(mn);
        } else lm.innerHTML =  text;    
        if (b)b.appendChild(lm);
    };
    
    this.showLogMessage = function(id) {
        var n = getElement(id);
        if (n && n.style){
            n.style.display = "block";
            var h = getElement(id + "_href");
            h.href = "javascript:jmaki.hideLogMessage('" + id + "')";
            var l = getElement(id + "_link");
            l.innerHTML = "&nbsp;" + _jmaki.getMessage("x_close");
        }   
    };

    this.hideLogMessage = function(id) {
        var n = getElement(id);
        if (n && n.style){
            n.style.display = "none";
            var h = getElement(id + "_href");
            h.href = "javascript:jmaki.showLogMessage('" + id + "')";
            var l = getElement(id + "_link");
            l.innerHTML = _jmaki.getMessage("more");            
        }      
    };   
    
    /**
     * Initialize jMaki by loading the config.json
     *  Write in the glue by loading dependencies and
     *  Register listeners.
     */
    this.initialize = function() {     
        if (!_jmaki.config) {
            _jmaki.config = {};
          _jmaki.doAjax({ url : this.webRoot + this.resourcesRoot + "/config.json",
               asynchronous : false,
               timeout : 3000,
               onerror : function() { /* do nothing and continue*/},
               callback :  function(req) {
                  if (req.responseText != '') {
                      var obj = eval('(' + req.responseText + ')');
                      if (obj.config) {
                          _jmaki.config = obj.config;

                      }
                  }
              }
          });
        }
        postInitialize();
    };
    
    /**
     * Create a namespace with the given string
     */
    this.namespace = function(_path, target) {
        // get the top level object
        var paths = _path.split('.');
        var _obj = window[paths[0]];
        if (!isDefined(_obj)) window[paths[0]] = _obj = {};
        for (var ii = 1; ii < paths.length; ii++) {
            if (isDefined(_obj[paths[ii]])) {
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
    };
    
    
    
    /*

The code was adopt with minor modifications from:
http://www.json.org/json2.js
*/

_jmaki.json = function () {

        function f(n) {    // Format integers to have at least two digits.
            return n < 10 ? '0' + n : n;
        }

        var m = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        };

        function stringify(value, whitelist) {
            var a,          // The array holding the partial texts.
                i,          // The loop counter.
                k,          // The member key.
                l,          // Length.
                r = /["\\\x00-\x1f\x7f-\x9f]/g,
                v;          // The member value.

            switch (typeof value) {
            case 'string':

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe sequences.

                return r.test(value) ?
                    '"' + value.replace(r, function (a) {
                        var c = m[a];
                        if (c) {
                            return c;
                        }
                        c = a.charCodeAt();
                        return '\\u00' + Math.floor(c / 16).toString(16) +
                                                   (c % 16).toString(16);
                    }) + '"' :
                    '"' + value + '"';

            case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

                return isFinite(value) ? String(value) : 'null';

            case 'boolean':
                return String(value);
            case 'null':
                return String(value);
            case 'date': {
              return value.getUTCFullYear() + '-' +
                 f(value.getUTCMonth() + 1) + '-' +
                 f(value.getUTCDate())      + 'T' +
                 f(value.getUTCHours())     + ':' +
                 f(value.getUTCMinutes())   + ':' +
                 f(value.getUTCSeconds())   + 'Z';                   
            }
            case 'object':

// Due to a specification blunder in ECMAScript,
// typeof null is 'object', so watch out for that case.

                if (!value) {
                    return 'null';
                }

// If the object has a toJSON method, call it, and stringify the result.

                if (typeof value.toJSON === 'function') {
                    return stringify(value.toJSON());
                }
                a = [];
                if (typeof value.length === 'number' &&
                        !(value.propertyIsEnumerable('length'))) {

// The object is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                    l = value.length;
                    for (i = 0; i < l; i += 1) {
                        a.push(stringify(value[i], whitelist) || 'null');
                    }

// Join all of the elements together and wrap them in brackets.

                    return '[' + a.join(',') + ']';
                }
                if (whitelist) {

// If a whitelist (array of keys) is provided, use it to select the components
// of the object.

                    l = whitelist.length;
                    for (i = 0; i < l; i += 1) {
                        k = whitelist[i];
                        if (typeof k === 'string') {
                            v = stringify(value[k], whitelist);
                            if (v) {
                                a.push(stringify(k) + ':' + v);
                            }
                        }
                    }
                } else {

// Otherwise, iterate through all of the keys in the object.

                    for (k in value) {
                        if (typeof k === 'string') {
                            v = stringify(value[k], whitelist);
                            if (v) {
                                a.push(stringify(k) + ':' + v);
                            }
                        }
                    }
                }

// Join all of the member texts together and wrap them in braces.

                return '{' + a.join(',') + '}';
            }
        }

        return {
            serialize: stringify,
            deserialize: function (text, filter) {
                var j;
                text = _jmaki.trim(text);
                function walk(k, v) {
                    var i, n;
                    if (v && typeof v === 'object') {
                        for (i in v) {
                            if (Object.prototype.hasOwnProperty.apply(v, [i])) {
                                n = walk(i, v[i]);
                                if (n !== undefined) {
                                    v[i] = n;
                                }
                            }
                        }
                    }
                    return filter(k, v);
                }

// Parsing happens in three stages. In the first stage, we run the text against
// regular expressions that look for non-JSON patterns. We are especially
// concerned with '()' and 'new' because they can cause invocation, and '='
// because it can cause mutation. But just to be safe, we want to reject all
// unexpected forms.

// We split the first stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace all backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.
                if (/^[\],:{}\s]*$/.test(text.replace(/\\./g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(:?[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
// In the second stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.
                    j = eval('(' + text + ')');

// In the optional third stage, we recursively walk the new structure, passing
// each name/value pair to a filter function for possible transformation.

                    return typeof filter === 'function' ? walk('', j) : j;
                }

// If the text is not JSON parseable, then a SyntaxError is thrown.
               throw jmaki.getMessage('invalid_json');
            }
        };

    }();
    
    
    this.findObject = function(_path) {
        var paths = _path.split('.');
        var found = false;		
        var _obj = window[paths[0]];
		if (_obj && paths.length == 1) found = true;	
        if (isDefined(_obj)){
            for (var ii =1; ii < paths.length; ii++) {
                var _lp = paths[ii];
                if (_lp.indexOf('()') != -1){                  
                  var _ns = _lp.split('()');
                  if (typeof _obj[_ns[0]] == 'function'){
                      var _fn = _obj[_ns[0]];              
                      return _fn.call(window);
                  }
                }     
                if (isDefined(_obj[_lp])) {
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
    };
    
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
        };
        
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
        };
        
        this.run = function() {
            if (isCall) {
                _src.processCall();
            } else {
                _src.processTopic();
            }
            window.setTimeout(_src.run,args.to);
        };
    };
    
    this.addTimer = function(_timer){
        var timers = [];
        timers.push(_timer);
        this.addTimers(timers);
    };
    
    this.addTimers = function(_timers){
        if (isDefined(_timers)){
            for (var _l=0; _l <_timers.length;_l++ ) {
                // create a wrapper and add the timer
                var _timer = _timers[_l];              
                if (_timer.action == 'call' &&
                isDefined(_timer.target) &&
                isDefined(_timer.target.object) &&
                isDefined(_timer.target.functionName) &&
                isDefined(_timer.timeout)) {
                    // create the timer
                    var args = {on: _timer.target.object,
                    fn: _timer.target.functionName,
                    to: _timer.timeout
                    };
                    var t1 = new _jmaki.Timer(args,true);
                    timers.push(t1);
                    t1.run();
                } else if (_timers[_l].action == 'publish') {
                    var args2 = {topics: _timers[_l].topics,
                    to: _timer.timeout
                    };
                    var t2 = new _jmaki.Timer(args2,false);
                    timers.push(t2);
                    t2.run();
                }
            }            
        }
    };
    
    function postInitialize() {
        if (_jmaki.initialized) return;
        else _jmaki.initialized = true;

        if (_jmaki.config.logLevel) {
            switch (_jmaki.config.logLevel) {
                case 'debug' : {
                        _jmaki.debug = true;
                        _jmaki.log(_jmaki.getMessage("jmaki_version",[_jmaki.version]));
                        break;                                    
                    }
                case 'all' : {
                        _jmaki.debug = true;
                        _jmaki.debugGlue = true;
                        break;
                    }
                case  'off' : {
                        _jmaki.debug = false;
                        break;
                    }
            }
        }                    
        // write out the dependent libraries so we have access
        if (_jmaki.config.glue) {
            if (_jmaki.config.glue.timers) {  
                _jmaki.addTimers(_jmaki.config.glue.timers);
            }
            if (_jmaki.config.gluelisteners){
                for (var gl=0; gl < _jmaki.config.glue.listeners.length;gl++) {
                    _jmaki.subscribe (_jmaki.config.glue.listeners[gl]); 
                }
            } 
        }        
        
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
     *  @param filter a string representing the path to the object or
     *    a funciton reference to procress the input
     */
    this.filter = function(input, filter){
        if (typeof filter == 'string') {
            var h = _jmaki.findObject(filter);
            return h.call(window,input);
        } else if (typeof filter == 'function'){
            return filter.call(window, input);
        }
        return null;
    };
 
     /**
    * A function to cloning an object or array so that different references do not
    * end up with shared references.
    * 
    * @param - t A single object or array
    *
    */
    this.clone = function(t) {
       var _obj;
       if (t instanceof Array) {         
           _obj = new Array();
           for (var _j=0;_j< t.length;_j++) {
               _obj.push(_jmaki.clone(t[_j]));
           }
       } else if (t instanceof Object) {          
           _obj = new Object();
           for (var _jj in t) {
                _obj[_jj] = _jmaki.clone(t[_jj]);
           }
       } else {
           _obj = t;
       }
       return _obj;
    };
 
    /*  This function takes an object literal and performs actions if present
     *  or it publishes a message to the provided topic.
     *
     *  _t = object literal { topic : 'topic to publish to', 
     *                        widgetId : 'source widget id',
     *                        targetId : 'foo',
     *                        action : [ 
     *                            { topic : '/some topic', message : { payload}}
     *                        ],
     *                        value : 'somevalue'
     *                      }
     * The action, targetId, and value properties are optional.
     * The topic and widgetId are required
     *
     */
    this.processActions = function(_t) { 	
        if (_t) {
            var _topic = _t.topic;
            var _m = {widgetId : _t.widgetId, type : _t.type, targetId : _t.targetId};
            if (typeof _t.value != "undefined") _m.value = _t.value;
            var action = _t.action;
            if (!action) _topic = _topic + "/" + _t.type;
            if (action && action instanceof Array) {
              for (var _a=0; _a < action.length; _a++) {
                  var payload = _jmaki.clone(_m);
                  if (action[_a].topic) payload.topic = action[_a].topic;
                  else payload.topic = _t.topic;
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
    };
   
    /**
     *  Find the postion of an Element
     *
     */ 
     this.getPosition = function(_e){
        var pX = 0;
        var pY = 0;
        if(_e.offsetParent) {
            while(true){
                pY += _e.offsetTop;
                pX += _e.offsetLeft;
                if(_e.offsetParent == null){
                    break;
                }
                _e = _e.offsetParent;
            }
        } else if(_e.y) {
                pY += _e.y;
                pX += _e.x;
        }
        return {x: pX, y: pY};
    };
    
    this.getDimensions = function(n, min) {
        if (typeof n == 'undefined' ||
            n == null) return null;
        var _min = 0;
        if (typeof min != 'undefined') _min = min;
        var rn = n.parentNode;
        while(rn && true) {          
            if (rn.clientHeight > _min) break;
            if (rn.parentNode && rn.parentNode.clientHeight)rn = rn.parentNode;
            else break;
        }
        if (!rn) return null;
        return {h : rn.clientHeight,w : rn.clientWidth};
    };

  this.DContainer = function(args){ 
      var _self = this;
        var _container;
        
        if (typeof args.target == 'string') {
            this.uuid = args.target;
            _container = getElement(this.uuid);
        } else {
            this.uuid = args.target.id;
            _container = args.target;      
        }
        if (!this.uuid) this.uuid = _jmaki.genId();
        // add to a reference of the jmaki containers
        _jmaki.dcontainers.put(_self.uuid, this);
        
        var oldWidth;
        this.url = null;
        this.externalDomain = false;
        var autoSizeH = false;
        var autoSizeW = false;
        
        if (args.autosize) {
            autoSizeH = true;
            autoSizeW = true;
        }
        
        if (typeof args.autosizeH == 'boolean') {
            autosizeH = args.autosizeH;
        }
        if (typeof args.autosizeW == 'boolean') {
            autoSizeW = args.autosizeW;         
        }
     
        // default sizes are all based on the width of the container   
        var VIEWPORT_WIDTH;
        var VIEWPORT_HEIGHT;
        
        function getHost(url) {
            var host = "";
            // get the second 1/2             
            var _p = url.split("://");
            if (_p[1]) {                 
                if (_p[1].indexOf("/") != -1) {
                    host = _p[1].substring(0, _p[1].indexOf("/"));
                } else {               
                    host = _p[1];
                }
            }
            return host;
        }
        
        this.clear = function() {              
            if (args.useIframe) {
                if (_self.iframe) {
                    _self.loadURL("");
                } else {
                    args.url = "";
                }
            } else {
                _jmaki.clearWidgets(_container);
                _container.innerHTML = "";
            }
        };
        
        this.loadURL = function(_url){
            // shut down all events published to iframe
            if (_self.iframe) {
                _self.externalDomain = true;
            }                    
            if (_url.message) _url = _url.message;
            if (typeof _url == 'string') { 
                _self.url = _url; 
            } else if (_url.url) {               
                _self.url = _url.url;
            } else if (_url.value) {
                _self.url = _url.value;
            }
            // check for jmaki and enable events to flow to parent jmaki instances
            function enableEvents() {
                // check to see if we are in the same domain for pushing messages from the bus
                // check if we are an external link             
                if (/http/i.test( _self.url) &&  top.window.location.host != getHost( _self.url)) {          
                    _self.externalDomain = true;
                } else {
                    _self.externalDomain = false;
                }              
                if (!_self.externalDomain) {
                    var _w = _self.iframe.contentWindow ? _self.iframe.contentWindow  : _self.iframe.window;              
                    if (_w && _w.jmaki) {
                        _w.jmaki.publishToParent = true;
                    }
                }
            }
            if (args.useIframe) {
                // wait for the iframe if it hasn't loaded
                if (!_self.iframe) {
                    var _t = setInterval(function() {
                        if (getElement(_self.uuid + "_iframe")) {
                            clearInterval(_t);               
                            _self.iframe = getElement(_self.uuid + "_iframe");
                            // wire on event listener to wait for iframe load and then
                                if (_jmaki.MSIE){
                                    _self.iframe.onreadystatechange = function() {
                                        if (this.readyState == 'complete') enableEvents();
                                    }
                                } else {
                                  _self.iframe.onload = enableEvents;                                
                                }                               
                            _self.iframe.src =  _self.url;
                            init();
                        }
                    }, 5);
              } else {
                  if (_jmaki.MSIE){
                      _self.iframe.onreadystatechange = function() {
                          if (this.readyState == 'complete') enableEvents();
                      }
                  } else {
                      _self.iframe.onload = enableEvents;                                
                  }
                  _self.iframe.src = _self.url;
             }
            } else {              
                _jmaki.injector.inject({url: _self.url, injectionPoint: _container});
                /*
                if (/http/i.test(_url) &&  top.window.location.host != getHost(_url)) {          
                    _self.externalDomain = true;
                } else {
                    _self.externalDomain = false;
                } */               
            }
        };
        
        this.setSize = function(size) {
            if (size.w) {
                VIEWPORT_WIDTH = size.w;
                _container.style.width = VIEWPORT_WIDTH + "px";
                if (_self.iframe) 
                    _self.iframe.style.width = VIEWPORT_WIDTH -2 + "px";                
            }
            if (size.h) {
                VIEWPORT_HEIGHT = size.h;
                _container.style.height = VIEWPORT_HEIGHT + "px";
                if (_self.iframe)                         
                    _self.iframe.style.height = VIEWPORT_HEIGHT -2 + "px";
            }
        };
        
        this.resize = function() {
            var _dim = _jmaki.getDimensions(_container);            
            if (autoSizeH || autoSizeW){
                if (!_container.parentNode) return;
                var pos = _jmaki.getPosition(_container);
                if (_container.parentNode.nodeName == "BODY") {
                    if (window.innerHeight){
                        if (autoSizeH)VIEWPORT_HEIGHT = window.innerHeight - pos.y ;
                        if (autoSizeW)VIEWPORT_WIDTH = window.innerWidth - 20;                 
                    } else {
                        if (_dim == null) { 
                            if (autoSizeW)VIEWPORT_WIDTH = 400;
                        } else {                     
                            if (autoSizeW)VIEWPORT_WIDTH = _dim.w -20;
                            if (autoSizeH)VIEWPORT_HEIGHT = _dim.h - pos.y;
                        }
                    }
                } else {
                    if (_dim == null) {
                        if (autoSizeW)VIEWPORT_WIDTH = 400;
                    } else {                     
                        if (autoSizeW)VIEWPORT_WIDTH = _dim.w;
                        if (autoSizeH)VIEWPORT_HEIGHT = _dim.h;
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
                    _self.iframe.style.height = VIEWPORT_HEIGHT -2 + "px";
                    _self.iframe.style.width = VIEWPORT_WIDTH -2 + "px";
                }
            }
            // used for tracking with IE
            oldWidth = _doc.body.clientWidth;           
        };
        
        this.setContent = function(_c) {
            var _con;
            if (_c.message)_c = _c.message;
            if (_c.value) _con = _c.value;
            else _con = _c;
            if (!_self.iframe)_container.innerHTML = _con;
            else {
                _self.clear();
                // recreate the ifrfame
                _container.innerHTML = "";
                createIframe(_c);                
            }
        }; 
        
        function init() {
            if (window.attachEvent) {
                window.attachEvent('onresize', layout);
            } else if (window.addEventListener) {
                window.addEventListener('resize', layout, true);
            }
            var _ot = _container;
            if (_self.iframe) {
                _ot = _self.iframe;           
            }      
            if (args.overflow) _ot.style.overflow = args.overflow;
            if (args.overflowX)_ot.style.overflowX = args.overflowX;
            if (args.overflowY)_ot.style.overflowY = args.overflowY;   
   
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
            _self.resize();
            if (args.url && !args.useIframe) {
                _self.loadURL(args.url);
            } else if (args.content && !_self.iframe) {            
                _container.innerHTML = args.content;
            } else if (args.url && !args.url) {
                _self.loadURL(args.url);
            }
            if (_self.iframe) _self.iframe.style.display = "inline";
        }
        
        var resizing = false;
        var lastSize = 0;
        
        function createIframe(content) {
            _self.iframe = getElement(_self.uuid + "_iframe");
            if (_self.iframe) _self.iframe.parentNode.removeChild(_self.iframe);
            // use this technique as creating the iframe programmatically does not allow us to turn the border off
            var iframeTemplate = "<iframe style='display:none' id='" + _self.uuid + "_iframe' name='" + _self.uuid +
                "_iframe' frameborder=0 scrolling=" +
                ((args.overflow == 'hidden') ? 'NO' : 'YES') + "></iframe>";
            _container.innerHTML = iframeTemplate;
            // wait for the iframe
            var _t = setInterval(function() {
                if (getElement(_self.uuid + "_iframe")) {
                    clearInterval(_t);               
                    _self.iframe = getElement(_self.uuid + "_iframe");
                    setTimeout(function(){
                        if (/http/i.test(_self.url) &&  top.window.location.host != getHost(_self.url)) {          
                            _self.externalDomain = true;
                        } else {
                            _self.externalDomain = false;
                        }                        
                        if (!_self.externalDomain && content) {
                            var _w = _self.iframe.contentWindow ? _self.iframe.contentWindow  : _self.iframe.window;              
                            if (_w && _w.document.body) {
                                _w.document.body.innerHTML = content;
                            }
                        }              
                        init();},0);
                }
            }, 5);          
        }
        
        function layout() {
            if (!_jmaki.MSIE) {
                _self.resize();
                return;
            }          
            // special handling for ie resizing.
            // we wait for no change for a full second before resizing.
            if (oldWidth != _doc.body.clientWidth && !resizing) {
                if (!resizing) {
                    resizing = true;
                    setTimeout(layout,500);
                }
            } else if (resizing && _doc.body.clientWidth == lastSize) {
                resizing = false;
                _self.resize();
            } else if (resizing) {
                lastSize = _doc.body.clientWidth;
                setTimeout(layout, 500);
            }
        } 
        if (args.useIframe && args.useIframe == true) {
            createIframe(args.content);
        } else init();
    };
    
    this.destroy = function() {
        _jmaki.dcontainers.remove(_self.uuid);
        if (window.attachEvent) {
            window.dettachEvent('onresize', layout);
        } else if (window.addEventListener) {
            window.removeEventListener('resize', layout, true);
        } 
    };
    
  this.Injector = function() {
 
  var _uuid = new Date().getMilliseconds();
  var _injector = this;
  var _processing = false;
 
  var tasks = [];
  
  this.inject = function(task) {
   // make sure jmaki creates a list of libraries it can not load
    if (tasks.length == 0 && !_processing) {
        inject(task);
    } else {
        tasks.push(task);
    }
  };

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
                   injectionPoint = getElement(task.injectionPoint);
                   // wait for the injection point
                   if (!getElement(task.injectionPoint)) {
                       var _t = setInterval(function() {
                           if (getElement(task.injectionPoint)) {
                               clearInterval(_t);
                               injectionPoint = getElement(task.injectionPoint);
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
                ip = getElement(task.injectionPoint);
            }
            _jmaki.clearWidgets(ip);
            ip.innerHTML = _jmaki.getMessage("unable_to_load_url", [task.url]);
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
          if (getElement(_id)) {
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
      }
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
  };

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

    // recursively go through and weed out the scripts

    var gscripts = _doc.getElementsByTagName("script");
    var gstyles = _doc.getElementsByTagName("link");
    while (_t.indexOf("<script") != -1) {
            var realStart = _t.indexOf("<script");
            var scriptSourceStart = _t.indexOf("src=", (realStart));
            var scriptElementEnd = _t.indexOf(">", realStart);
            var end = _t.indexOf("</script>", (realStart)) + "</script>".length;
            if (realStart != -1 && scriptSourceStart != -1) {
                var scriptSourceName;
                var scriptSourceLinkStart= scriptSourceStart + 5;
                var quoteType =  _t.substring(scriptSourceStart + 4, (scriptSourceStart +5));
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
           var rs = _t.indexOf("<style");
           var styleElementEnd = _t.indexOf(">", rs);
           var e2 = _t.indexOf("</style>", rs) ;
           var styleBodyStart =  styleElementEnd + 1;
           var sBody2 = _t.substring(styleBodyStart, e2);
           if (sBody2.length > 0) {
              _task.embeddedStyles.push(sBody2);
           }
           //remove style
           _t = _t.substring(0, rs) + _t.substring(e2 + "</style>".length, _t.length);
        }
        // get the links    
        while (_t.indexOf("<link") != -1) {
            var rs2 = _t.indexOf("<link");
            var styleSourceStart = _t.indexOf("href=", rs2);
            var styleElementEnd2 = _t.indexOf(">", rs2) +1;
            if (rs2 != -1 && styleSourceStart != -1) {
                var styleSourceName;
                var styleSourceLinkStart= styleSourceStart + 6;
                var qt =  _t.substring(styleSourceStart + 5, (styleSourceStart + 6));           
                var styleSourceLinkEnd= _t.indexOf(qt, (styleSourceLinkStart + 1));
                if (styleSourceStart < styleElementEnd2) {
                    styleSourceName = _t.substring(styleSourceLinkStart, styleSourceLinkEnd);
	              	var exists2 = false;
                        for (var ii = 0; ii < gstyles.length; ii++) {
                            if (isDefined(gstyles[ii].href)) {
                                if (gstyles[ii].href == styleSourceName) {
                                    exists2 = true;	
                                }
                            }
                        }
		        if (!exists2) {
		          	_task.styleReferences.push(styleSourceName);
	    	      }
                }
                //remove style
                _t = _t.substring(0, rs2) + _t.substring(styleElementEnd2, _t.length);
            }
        }
        
        var head = _doc.getElementsByTagName("head")[0];
        
        // inject the links
        for(var loop = 0; _task.styleReferences && loop < _task.styleReferences.length; loop++) {
            var link = _doc.createElement("link");
            link.href = _task.styleReferences[loop];
            link.type = "text/css";
            link.rel = "stylesheet";
            head.appendChild(link);
        }
        
        var stylesElement;
        if (_task.embeddedStyles.length > 0) {
            stylesElement = _doc.createElement("style");
            stylesElement.type="text/css";
            var stylesText;
            for(var j = 0; j < _task.embeddedStyles.length; j++) {
                stylesText = stylesText + _task.embeddedStyles[j];
            }
            if (_doc.styleSheets[0].cssText) {
               _doc.styleSheets[0].cssText = _doc.styleSheets[0].cssText + stylesText;
            } else {
                stylesElement.appendChild(_doc.createTextNode(stylesText));
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
                      if (isDefined(initFunction)) initFunction();
                      return;
                  }
              }
              if (task.embeddedScripts && task.embeddedScripts.length == 0 && isDefined(initFunction)) initFunction();
          };
          if (task.scriptReferences && task.scriptReferences.length > 0){
              // load the global scripts before loading the embeded scripts
              return _jmaki.addLibraries({ libs : task.scriptReferences.reverse(), callback : _loadEmbeded, cleanup : false});   
          } else {
              _loadEmbeded();
          }
          return true;
     };
  };
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
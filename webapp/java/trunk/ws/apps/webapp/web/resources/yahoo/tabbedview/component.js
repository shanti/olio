// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.tabbedview");

/**
 * Yahoo UI Tabbed View Widget
 *
 * @author Greg Murray
 *      original author of the widget
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
 *      Updates to topic and glue publish/subscribe model and added methods for adding/removing/getting tabs
 * @constructor
 * @see http://developer.yahoo.com/yui/tabview/
 */
jmaki.widgets.yahoo.tabbedview.Widget = function(wargs) {
    
    var _widget = this;
    var uuid = wargs.uuid;
    var publish = "/yahoo/tabbedview";
    var subscribe = ["/yahoo/tabbedview", "/tabbedview"];    
    var isIE = /MSIE/i.test(navigator.userAgent);
    var items;
    var selected = 0;
    var tabMappings = {};
    
    var container = document.getElementById(wargs.uuid);
    
    if (document.body) {
    	if (!/yui-skin-sam/i.test(document.body.className)){
    		document.body.className += " yui-skin-sam";
    	}
    }

    var showedModelWarning = false;
    
    function showModelDeprecation() {
        if (!showedModelWarning) {
             jmaki.log("Dojo tabbed view  widget with id " + wargs.uuid + " uses the incorrect data format. Please see " +
                       "<a href='http://wiki.java.net/bin/view/Projects/jMakiTabbedViewDataModel'>" +
                       "http://wiki.java.net/bin/view/Projects/jMakiTabbedViewDataModel</a> " +
                       "for the proper format.");
             showedModelWarning = true;
        }   
    }      
    
    //read the widget configuration arguments
    if (typeof wargs.args != 'undefined') {
        //overide topic name if needed
        if (typeof wargs.args.topic != 'undefined') {
            publish = wargs.args.topic;
            jmaki.log("Yahoo tabbedview: widget uses deprecated topic. Use publish instead.");
        }  
    }
    
    if (wargs.publish) {
	publish = wargs.publish;
    }
    if (wargs.subscribe){
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
    }
 
    _widget._tabView = new YAHOO.widget.TabView(wargs.uuid);
    
    function clone(t) {
       var obj = {};
       for (var i in t) {
            obj[i] = t[i];
       }
       return obj;
    }

   
    function processActions(m, pid, _type, value) {
        jmaki.processActions({ action : m.action, 
                               topic : publish,
                               widgetId : wargs.uuid,
                               type : _type,
                               targetId : pid,
                               value : value});
    } 

    var handler = function(e) {
        var tab = e.newValue;
        processActions(tab,tab.tid);     
        if (tab.url && tab.contentLoaded == false){
                tab.dcontainer.loadURL(tab.url);
                tab.contentLoaded = true;
        }       
    };
    _widget._tabView.addListener('activeTabChange', handler);
    
    /**
     * returns the current 'active' tab (YAHOO.widget.Tab)
     * @return active tab object (YAHOO.widget.Tab)
     */
    this.getActiveTab = function() {
        var activeTab = _widget._tabView.get("activeTab");
        return activeTab;
    }
       
    this.setContent = function(e, c) {
        var tabId;
        var content;
        if (e.message)e = e.message;
        if (e.targetId) tabId = e.targetId;
        else tabId = e;
        if (e.value) content = e.value;
        else content = c;
        if (tabMappings[tabId]) {
            var tab = tabMappings[tabId];        
            if (content){
                tab.dcontainer.setContent(content);
                tab.contentLoaded = true;
            }
        }
    }
       
    this.setInclude = function(e, c) {
        var tabId;
        var include;
        if (e.message)e = e.message;
        if (e.targetId) tabId = e.targetId;
        else tabId = e;
        if (e.value) include = e.value;
        else include = c;

        if (tabMappings[tabId]) {
            var tab = tabMappings[tabId];
            if (include){            
                tab.dcontainer.loadURL(include);
                tab.contentLoaded = true;
            }
        }
    }     
    
   this.selectTab = function(e) {
        var tabId;
        if (e.message)e = e.message;
        if (e.targetId) tabId = e.targetId;
        else tabId = e;

        if (tabMappings[tabId]) {
            var tab = tabMappings[tabId];
            _widget._tabView.set("activeTab", tabMappings[tabId]);  
            if (tab.url && tab.contentLoaded == false){
                tab.dcontainer.loadURL(tab.url);
                tab.contentLoaded = true;
            }
        }
    }
    
    /**
     * Add a tab (static)
     * @param label tab label
     * @param content tab content
     */
    this.addTab = function(label, content) {
        var t = new YAHOO.widget.Tab({
            label: label,
            content: content,
            active: true
        });
        _widget._tabView.addTab(t);
    }
    
    /** 
     * Remove a tab
     * @param tab a YAHOO.widget.Tab that is to be removed
     */
    this.removeTab = function(tab) {
        if(typeof tab != 'undefined') {
            _widget._tabView.removeTab(tab);
        } else {
            YAHOO.log("undefined tab at removeTab","warn");
        }
    }
    
    /**
     * Returns the tab at a given index
     * @param index tab index
     * @return a YAHOO.widget.Tab tab at that index
     */
    this.getTab = function(index) {
        return _widget._tabView.getTab(index);
    }
    
    /**
     * Returns the tab at a given id
     * @param tabid tab index
     * @return a YAHOO.widget.Tab tab at that index
     */
    this.getTabById = function(tabId) {
        return tabMappings[tabId];
    }    
    
    /**
     * Returns the length of the tabs array
     */
    this.getTabCount = function() {
        return _widget._tabView.get("tabs").length;
    }

    function init() {
        var selected;
        var first;
        _widget.container = document.getElementById(wargs.uuid);
        _widget.dim = jmaki.getDimensions(_widget.container, 50);
        for(var _ii=0; _ii < items.length; _ii++) {
            var _row = items[_ii];

            var _tid = _row.id;

            if (!_tid) _tid = wargs.uuid + '_tab_' + _ii;

            var content;
            if (_row.content)  content = ("<div style='height:" + h + "px;width:" + w + "' id='" + _tid +"'>" + _row.content + "</div>");
            else if (_row.include || row.url) content = ("<div style='height:" + h + "px;width:" + w + "' id='" + _tid +"'>Loading</div>");
            
            // calculate height here the total SAM height is 53 px
            var h =  _widget.dim.h - 53; 
            var w =  document.getElementById(wargs.uuid).parentNode.clientWidth - 2;
            if (h <= 50) h = 300;
            if (isIE) h = h - 3;
            var _r = new YAHOO.widget.Tab({
                label: _row.label,
                active: true
            });
            if (!first) first = _tid;
            if (_row.selected) selected = _tid;
            var _url = undefined;
            if (_row.lazyLoad && _row.lazyLoad == true) {
                _r.url = _row.include;                
                _r.contentLoaded = false; 
                if (_row.url) {
                    showModelDeprecation();
                    _r.url = _row.url;
                }        
            } else if (_row.include){
                _url = _row.include;
                _r.url = _row.include;
                _r.contentLoaded = true;
            } else if (_row.url) {
                showModelDeprecation();
                _url = _row.url;
                _r.contentLoaded = true;
            }

            _r.tid = _tid;   
            _r.label = _row.label;
            _widget._tabView.addTab(_r);
            if (_row.action) _r.action = _row.action;

            tabMappings[_tid] = _r;

            var of = 'hidden';
            if (_row.overflow) of = _row.overflow;
            var iframe = _row.iframe;
                
            var cv = _r.get('contentEl');

            cv.id = _tid;         
            var iargs = {
                target: cv,
                useIframe : iframe,
                overflow: of,
                content : content,
                startHeight : h,
                startWidth : w,
                autosize : true
            };
            _r.dcontainer = new jmaki.DContainer(iargs);
            _r.index =_ii;
            if (_url) _r.dcontainer.loadURL(_url);

        }
        if (selected) _widget.selectTab(selected);
        else if (first)_widget.selectTab(first);
 
        function doSubscribe(topic, handler) {
             var i = jmaki.subscribe(topic, handler);
             _widget.subs.push(i);
         }
    
         this.destroy = function() {
             for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
                 jmaki.unsubscribe(_widget.subs[i]);
             }
         }
        for (var _i=0; _i < subscribe.length; _i++) {
	    _widget.subs = [];
            doSubscribe(subscribe[_i]  + "/select", _widget.selectTab);
            doSubscribe(subscribe[_i] + "/setContent", _widget.setContent);
            doSubscribe(subscribe[_i] + "/setInclude", _widget.setInclude);

        }   
    }
    
   if (wargs.value) {     
        if (wargs.value.tabs) {
            showModelDeprecation();
            items = wargs.value.tabs;
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
                container.innerHTML = "Error loading widget data. No data.";
                return;
            }
            var obj = eval("(" + req.responseText + ")");
            
            if (obj.tabs) {
                showModelDeprecation();
                items = obj.tabs;
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
                container.innerHTML = "Error loading widget data.";
            }
        });       
    }
};

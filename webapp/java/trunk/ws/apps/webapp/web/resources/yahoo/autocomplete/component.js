// Define the namespace
jmaki.namespace("jmaki.widgets.yahoo.autocomplete");

/**
 * Yahoo UI Autocomplete Widget
 *
 * @author Edwin Goei
 * @author Greg Murray
 * @constructor
 * @see http://developer.yahoo.com/yui/autocomplete/
 */
 
jmaki.widgets.yahoo.autocomplete.Widget = function(wargs) {
	
    var _widget = this;
    var publish = "/yahoo/autocomplete";
    var subscribe = ["/yahoo/autocomplete"];
    var uuid = wargs.uuid; 
    var service = wargs.service;
    var containerId = uuid + "_container";
    var container = document.getElementById(wargs.uuid);
    var inputContainer = document.getElementById(wargs.uuid + "_autocomplete");
    var acContainer = document.getElementById(wargs.uuid + "_container");
    acContainer.style.width = inputContainer.clientWidth + "px";
    var autoComp;
    var completionMethod;
    var vmappings = {};
    var counter=0;
    var ds;

    if (wargs.args && wargs.args.completionMethod) {
        completionMethod = wargs.args.completionMethod;
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
    function genKey(){
        return wargs.uuid + "_item_" + counter++;
    }
    
    function modelConverter(data) {
        var model = [];
        for (var i=0; i < data.length; i++) {
            var _val = data[i].value;
            var _label = data[i].label;
            // if only a value is specified make the label equal to the value
            if (!_val && _label) _val = _label;
            if (_val && !_label) _label = _val;
            if (data[i].selected &&  data[i].selected == true) {
               _widget.selected = _label;             
            }

            var key =  data[i].id;
            if (!key) key = genKey();
            vmappings[_val] = { targetId : key, action : data[i].action};
            model.push([_label,_val]);
                
        }
        return model;
    }    

    this.init = function() {

	    if (wargs.value) {
	    	 ds = new YAHOO.widget.DS_JSArray(modelConverter(wargs.value)); 
	    } else if (wargs.service) {
			var schema = ["item", "title"];
		    ds = new YAHOO.widget.DS_XHR(service, schema);
		    ds.responseType = YAHOO.widget.DS_XHR.TYPE_XML;
		    ds.scriptQueryAppend = "method=" + completionMethod;
	    }
	   
            _widget.nativeWidget = new YAHOO.widget.AutoComplete(uuid + "_autocomplete", uuid + "_container", ds);
            _widget.nativeWidget.prehighlightClassName = "yui-ac-prehighlight"; 
            _widget.nativeWidget.typeAhead = true; 
            _widget.nativeWidget.useShadow = true; 
            _widget.nativeWidget.forceSelection = true;
            _widget.nativeWidget.itemSelectEvent.subscribe(function(ev,item,o){ 
                var i=vmappings[item[2][1]];
                processActions(i, i.targetId, item[2][1]);
	    }); 
    };
 
     function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }
    
    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
    };
    function clone(t) {
       var obj = {};
       for (var i in t) {
            obj[i] = t[i];
       }
       return obj;
    }    
    
    function processActions(m, pid, value) {
        if (m) {
            var _topic = publish + "/onSelect";
            var _m = {widgetId : wargs.uuid, topic : publish, type : 'onSelect', targetId : pid, value : value};

            var action = m.action;

            if (action && action instanceof Array) {
              for (var _a=0; _a < action.length; _a++) {
                  var payload = clone(_m);
                  if (action[_a].topic) payload.topic = action[_a].topic;
                  else payload.topic = publish;
                  if (action[_a].message) payload.message = action[_a].message;
                  jmaki.publish(payload.topic,payload);
              }
            } else {
              if (m.action && m.action.topic) {
                  _topic = _m.topic = m.action.topic;
              }
              if (m.action && m.action.message) _m.message = m.action.message;                
              jmaki.publish(_topic,_m);
            } 
        }
    }     
    
    this.setValues = function(e){  
        var _values;
        if (e.message && e.message.value) _values = e.message.value;
        else _values = e;
        var _selected;
        // clear out the selected value so we can reselect
        _widget.selected = undefined;
        if (_values) {
           vmappings = {};
           _widget.setValue("");
           if (_widget.selected)_widget.setValue(_widget.selected);
           ds = new YAHOO.widget.DS_JSArray(modelConverter(_values));
           // set the new data sources
           this.wrapper.dataSource = ds;
        }
    };    

    this.postLoad = function() {
        _widget.subs = [];
        if (wargs.args && wargs.args.selected) {        
            _widget.nativeWidget.setValue(wargs.args.selected);     
        } else if (_widget.selected) {
           _widget.setValue(_widget.selected);    
        }
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/select", _widget.setValue);
            doSubscribe(subscribe[_i]  + "/setValues", _widget.setValues);
        }
    };

    this.getValue = function() { 
        return inputContainer.value;
    };
    
    this.setValue = function(_n) {
        inputContainer.value = _n;
    };
    
    this.init();
};

// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.weather");

/**
 * Yahoo Weather Widget
 *   This widget lets you find the weather forecast using Yahoo Weather RSS feed.
 *   It uses the XMLHttpProxy to access the RSS feed. It can operate in hidden mode 
 *   if needed.
 * 
 *   US Zipcodes are already valid location ids. To find out another location id outside
 *   USA, you can use http://xoap.weather.com/search/search?where=city,country
 *
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>  
 *
 * @see http://developer.yahoo.com/weather/
 */
jmaki.widgets.yahoo.weather.Widget = function(wargs) {
    
    var topic = "/yahoo/weather";
    var subscribe = ['/yahoo/weather'];
    var uuid = wargs.uuid;
    var _widget = this;        
    var shown = false;
    var location = "95054";
    var unit = "f";
    
    //read configuration arguments
    if (typeof wargs.args != 'undefined') {
        //overide topic name if needed
        if(typeof wargs.args.location != 'undefined') {
            location = wargs.args.location;            
        }
        if(typeof wargs.args.unit != 'undefined') {
            unit = wargs.args.unit;            
        }
        if(typeof wargs.args.showConfig != 'hidden') {
            shown = wargs.args.showConfig;
        }
    }
    if (wargs.publish) {
		topic = wargs.publish;
	}

    if (wargs.subscribe){
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);         
        }
    }
    
    if(shown) {
        var form = document.getElementById(uuid + "_form");
        form.style.display = "block";
    }
    
    /**
     * Returns the current weather forecast for location and unit
     * if the one of the optional parameters is not set,
     * they will be pulled automatically from the controls.
     *
     * @param l location id or zipcode (optional)
     * @param u 'f' for Fahrenheit and 'c' for Celsius (optional)
     */
    this.getForecast = function(l,u) {
      
        var _location = location;
        var _unit = unit;
        if (typeof u != "undefined") _unit = u;
        // for glue / actions
        if (l.value) l = l.value;
        if (l.zip) _location = l.zip;
        else _location = l;
      
        //take them from controls...
        if (shown) {
            _location = encodeURIComponent(document.getElementById(uuid + "_location").value);
            _unit = encodeURIComponent(document.getElementById(uuid + "_unit").value);
        }

        var url = jmaki.xhp + "?id=yahooweather&urlparams=" + encodeURIComponent("p=" + _location + "&u=" + _unit);
        jmaki.doAjax({
            url: url, 
            callback: function(req) { 
                var _req = req;
                postProcess(_req);
            }
        });
    }
    
    /**
     * called by doAjax after finishing an HTTP request
     */
    function postProcess(req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var v = {success:false};
                if(req.responseText.length > 0) {
                    var response = eval("(" + req.responseText + ")");
                    if(response.data.ok) {
                    	try {
                        v = {success:true,data:response.data}; 
                        var r = document.getElementById(uuid + "_results");
                        if(typeof v.data.forecast[0] != 'undefined')  {
                            if(typeof v.data.forecast[0].description != 'undefined')  {    
                                r.innerHTML = v.data.forecast[0].description;
                            }
                        }
                    	} catch(e) {}
                    } 
                } 
                //the new format is here (as in v)
                //with status flag sent
                jmaki.publish(topic + "/onChange", {id: uuid, value:v} );                
            } 
        }
        
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
    
    this.postLoad = function() {
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/getForecast", _widget.getForecast);
        }        
        _widget.getForecast(location,unit);
        
    };
};
// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.slider");

/**
* Yahoo UI Horizontal/Vertical and split-button Slider Widget
* Code originally adapted from yahoo.vslider
* Code updated to use new split button
*
* @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
* @constructor
* @see http://developer.yahoo.com/yui/slider/
*/
jmaki.widgets.yahoo.slider.Widget = function(wargs) {
    
    var publish = "/yahoo/slider";
    var subscribe = ['/yahoo/slider','/slider'];
    var wrapper;
    var uuid = wargs.uuid;
    var self = this;
    
    //abbrevations
    var YDom = YAHOO.util.Dom;
    
    var sliderType = "H"; //default to horizontal "H"
    var tickSize = 10;  // Slider Tick Size (not pixel)
    var animate = true; //animation on/off flag
    var minValue = 0;   //minimum value (not pixel)
    var maxValue = 100; //maximum value (not pixel)
    var initialValue = 0; //initial value (not pixel)
    var sizeInPixels = 200; //slider size in pixels
    
    /**
    * Deprecation helper function
    */
    function showDeprecationMsg(deprecated,recommended)
    {
        jmaki.log("Yahoo slider: widget uses deprecated argument '"+
            deprecated + "'. Please Use '"+ recommended +"' instead.");
    }
    //read the widget configuration arguments
    if (typeof wargs.args != 'undefined') {
        var args = wargs.args;
        if (typeof args.topic != 'undefined') {
            //deprecated... (only for compatibility)
            publish = args.topic;
        }
        if (typeof args.sliderType != 'undefined') {
            sliderType = args.sliderType;
        }
        if (typeof args.sliderLeft != 'undefined') {
            //deprecated... (use minimum value instead)
            showDeprecationMsg("sliderLeft","minValue");
        }
        if (typeof args.sliderRight != 'undefined') {
            //deprecated... (use maxValue instead)
            showDeprecationMsg("sliderRight","maxValue");
        }
        if(typeof args.sizeInPixels != 'undefined') {
            sizeInPixels = args.sizeInPixels;
        }
        if (typeof args.scaleFactor != 'undefined') {
            showDeprecationMsg('scaleFactor','NA');
        }
        if (typeof args.animate != 'undefined') {
            animate = args.animate;
        }
        if (typeof args.minValue != 'undefined') {
            minValue = args.minValue;
        }
        if (typeof args.maxValue != 'undefined') {
            maxValue = args.maxValue;
        }
        if (typeof args.initialValue != 'undefined') {
            initialValue = args.initialValue;
        }
        if (typeof args.tickSize != 'undefined') {
            //not that it is now in values not pixels
            tickSize = args.tickSize;
        }
    }
    
    //make sure initialValue is within bounds
    if(initialValue < minValue) {
        initialValue = minValue;
    }
    if(initialValue > maxValue) {
        initialValue = maxValue;
    }
    //validate tick size
    if(tickSize > maxValue) {
        jmaki.log("tickSize is bigger than maxValue. Please set a reasonable value for tickSize");
        tickSize = 1;
    }
    //calculate scale factor
    var scaleFactor = (maxValue - minValue) / sizeInPixels;
    
    if (wargs.publish) {
        publish = wargs.publish;
    }
    
    if (wargs.subscribe) {
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
    }
    
    
    /**
    * subscribe to a jmaki topic (with tracking enabled)
    * This is needed for this.destroy
    */
    function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        self.subs.push(i);
    }
    
    /**
    * cleanup jmaki glue handler code..
    */
    this.destroy = function() {
        for(var i=0; self.subs && i < self.subs.length; i++) {
            jmaki.unsubscribe(self.subs[i]);
        }
    };
    
    /**
    * Select a new value in the slider
    */
    this.select = function(d) {
        if(!wrapper) {
            jmaki.log("Error: slider is not initialized.");
        }
        if(d && d.message && typeof d.message.value == "number") {
            var v = Math.round((d.message.value-minValue) / scaleFactor);
            wrapper.setValue(v, false);
        }else
        {
            jmaki.log("message or value is not defined in action");
        }
    };
    
    /**
    * Return a cloned copy of t
    */
    function clone(t) {
        var obj = {};
        for (var i in t) {
            obj[i] = t[i];
        }
        return obj;
    }
    
    /**
    * Generic process actions method used to consume action
    */
    function processActions(m, pid, _type, value) {
        if (m) {
            var _topic = publish;
            var _m = {widgetId : wargs.uuid, type : _type, targetId : pid};
            var action = m.action;
            if (!action) {
                _topic = _topic + "/" + _type;
            }
            if (typeof value != "undefined") {
                _m.value= value;
            }
            if (action && action instanceof Array) {
                for (var _a=0; _a < action.length; _a++) {
                    var payload = clone(_m);
                    if (action[_a].topic) {
                        payload.topic = action[_a].topic;
                    } else
                    {
                        payload.topic = publish;
                    }
                    if (action[_a].message) {
                        payload.message = action[_a].message;
                    }
                    jmaki.publish(payload.topic,payload);
                }
            } else
            {
                if (m.action && m.action.topic) {
                    _topic = _m.topic = m.action.topic;
                }
                if (m.action && m.action.message) {
                    _m.message = m.action.message;
                }
                jmaki.publish(_topic,_m);
            }
        }
    }
    
    /**
    * initialize slider
    */
    function initSlider() {
        var prefix = sliderType.toLowerCase();
        var sliderBg = uuid + "_" + prefix + "sliderbg";
        var sliderThumb = uuid + "_" + prefix + "sliderthumb";
        
        
        //Calculate best tick size...
        var tickSizePx = 0;//disable ticking by default
        for(var i = tickSize; i < maxValue/2; i++) {
            var sizePx = Math.round(i / scaleFactor);
            if(sizeInPixels % sizePx === 0) {
                //divides by slider pixel size
                tickSizePx = sizePx;
                tickSize = i;
                break;
            }
        }
        
        if(sliderType == 'H') {
            //horizontal slider
            YDom.setStyle(sliderBg,"width", (sizeInPixels+17) + "px");
            wrapper = YAHOO.widget.Slider.getHorizSlider(
                sliderBg,  sliderThumb,  0, sizeInPixels, tickSizePx);
            YDom.setStyle(sliderBg,"display","block");
        } else if(sliderType == 'V')
        {
            //vertical slider
            YDom.setStyle(sliderBg,"height", (sizeInPixels+8) + "px");
            wrapper = YAHOO.widget.Slider.getVertSlider(
                sliderBg,  sliderThumb,  0, sizeInPixels, tickSizePx);
            YDom.setStyle(sliderBg,"display","block");
        } else
        {
            //button slider
            // Create a Menu instance to house the slider instance
            var menu = new YAHOO.widget.Overlay(uuid + "_menu");
            
            // Create a Button instance of type "split"
            var buttonId = uuid + "_btn";
            var buttonValId = uuid + "_btnval";
            var button = new YAHOO.widget.Button(buttonId,{
                type: "split",
                label: '<span id="' + buttonValId + '" class="bslidercurrval"></span>',
                menu: menu });
            
            //Create a placeholder for slider And
            //render the Menu into the Button's container
            menu.setBody("<div id=\""+ sliderBg + "\" class=\"hsliderbg\" tabindex=\"-1\">" +
                "<div id=\"" + sliderThumb + "\" class=\"hsliderthumb\">" +
                "<img src=\"" + wargs.widgetDir + "/assets/thumb-n.gif\"></div></div>");
            menu.body.id = uuid + "_container";
            menu.render(buttonId);

            // Align the Menu to its Button
            menu.align();
            menu.hide();
            
            //Create a new horizontal slider instance, placing
            //it inside the body element of the Menu instance.
            YDom.setStyle(sliderBg,"width", (sizeInPixels+17) + "px");
            wrapper = YAHOO.widget.Slider.getHorizSlider(
                sliderBg,  sliderThumb,  0, sizeInPixels, tickSizePx);
            YDom.setStyle(sliderBg,"display","block");
            
            //on slider change update button value...
            wrapper.subscribe("change",function(offsetFromStart) {
                // use the scale factor to convert the pixel offset into a real value
                var btnVal = document.getElementById(buttonValId);
                if(btnVal) {
                    var v = Math.round(offsetFromStart * scaleFactor) + minValue;
                    btnVal.innerHTML = "" + v;
                }
            });
            
            //this is need to solve focus bugs on ie and gecko browsers
            var sliderEl = YDom.get(sliderBg);
            var focusSlider = function() {
                if ((YAHOO.env.ua.ie || YAHOO.env.ua.gecko) && sliderEl) {
                    window.setTimeout(function () {
                        sliderEl.focus();
                    }, 0);
                }
            };
            // Focus the Slider instance each time it is made visible
            menu.subscribe("show", focusSlider);
        }
        
        if(wrapper) {
            wrapper.animate = animate;
            wrapper.setValue(Math.round((initialValue-minValue) / scaleFactor),false);
            //onChange event
            var onChange = function(offsetFromStart) {
                // use the scale factor to convert the pixel offset into a real value
                var v = Math.round(offsetFromStart * scaleFactor) + minValue;
                jmaki.publish(publish + "/onSelect", {widgetId:uuid, value:v});
            };
            wrapper.subscribe("change",onChange);
            
            // track the subscribers so we can later remove them
            self.subs = [];
            for (var _i=0; _i < subscribe.length; _i++) {
                doSubscribe(subscribe[_i]  + "/select", self.select);
            }
        }
    }
    if (!jmaki.loaded) jmaki.subscribe("/jmaki/runtime/loadComplete",initSlider);
    else initSlider();
};
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
// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.colorpicker");

/**
* Y! color picker jMaki Widget
*
* It works in two modes:
* 1) as normal colorpicker widget
* 2) as popup colorpicker button widget
*
* Note: this replaces yahoo.rgbslider which was based on a slider example
* and is now a generic yahoo-supported widget
*
* @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
* @constructor
* @see http://developer.yahoo.com/yui/colorpicker/
*/
jmaki.widgets.yahoo.colorpicker.Widget = function(wargs) {
    
    var publish = "/yahoo/colorpicker";
    var subscribe = ['/yahoo/colorpicker','/colorpicker'];
    var self = this;
    var uuid = wargs.uuid;
    var containerId;
    var wrapper;
    
    //abbreviations
    var YDom = YAHOO.util.Dom;
    
    //default color picker configuration
    var cfg = {
        mode: 'normal',
        showControls: true,
        showHexControls: true,
        showHexSummary: true,
        showHsvControls: false,
        showRgbControls: true,
        showWebSafe: true,
        red: 255,
        green: 255,
        blue: 255
    };
 
    if (document.body) {
    	if (!/yui-skin-sam/i.test(document.body.className)){
    		document.body.className += " yui-skin-sam";
    	}
    }
    
    //TODO change widget image... [100x100]
    
    //read the widget's config arguments
    if (typeof wargs.args != 'undefined') {
        var args = wargs.args;
        if (typeof args.mode != 'undefined') {
            var modes = {'button':'button', 'normal':'normal'};
            var o = modes[args.mode];
            cfg.mode = (typeof o != 'undefined') ? o : 'normal';
        }
        if (typeof args.showControls != 'undefined') {
            cfg.showControls = args.showControls;
        }
        if (typeof args.showHexControls != 'undefined') {
            cfg.showHexControls = args.showHexControls;
        }
        if (typeof args.showHexSummary != 'undefined') {
            cfg.showHexSummary = args.showHexSummary;
        }
        if (typeof args.showHsvControls != 'undefined') {
            cfg.showHsvControls = args.showHsvControls;
            if(cfg.showHsvControls) {
                //cannot show both...
                cfg.showHexSummary = false;
            }
        }
        if (typeof args.showRgbControls != 'undefined') {
            cfg.showRgbControls = args.showRgbControls;
        }
        if (typeof args.showWebSafe != 'undefined') {
            cfg.showWebSafe = args.showWebSafe;
        }
    }
    
    if (wargs.publish) {
        publish = wargs.publish;
    }
    if (wargs.subscribe) {
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
    }
    if(wargs.value) {
        var v = wargs.value;
        if(v.rgb && v.rgb.length >= 3) {
            var rgb = v.rgb;
            cfg.red = rgb[0];
            cfg.green = rgb[1];
            cfg.blue = rgb[2];
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
    
    if(wargs.service) {
        //TODO implement service functionality
        jmaki.log("service is not currently implemented");
    }
    
    if(cfg.mode == 'button') {
        containerId = uuid + "_btn";
    } else
    {   //cfg.mode == 'normal'
        containerId = uuid + "_div";
    }
    
    
    /**
    * Select a new color in the picker
    */
    this.select = function(d) {
        if(!wrapper) {
            jmaki.log("Error: Color picker is not initialized.");
        }
        if(d && d.message && d.message.value) {
            var v = d.message.value;
            if(v.rgb && v.rgb.length >= 3) {
                self.setValue(v.rgb[0],v.rgb[1],v.rgb[2],false);
            }
            
        }else
        {
            jmaki.log("message or value is not defined in action");
        }
    };
    
    /**
    * Sets the red, green and blue (firing /onSelect) optional...
    */
    this.setValue = function(red,green,blue,fireOnSelect) {
        if(!wrapper) {
            jmaki.log("Error: Color picker is not initialized.");
            return;
        }
        wrapper.setValue([red,green,blue],fireOnSelect);
    };
    
    /**
    * Returns RGB,HSV and hex values in a JSON structure
    */
    this.getValue = function() {
        if(!wrapper) {
            jmaki.log("Error: Color picker is not initialized.");
        }
        return {
            rgb: [wrapper.get("red"),wrapper.get("green"),wrapper.get("blue")],
            hsv: [wrapper.get("hue"),wrapper.get("saturation"),wrapper.get("value")],
            hex: '#' + wrapper.get("hex"),
            websafe: wrapper.get("websafe")
        };
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
    * Create the color picker
    */
    function initColorPicker() {
        var baseUrl = wargs.widgetDir + "../resources/libs/yahoo/v2.6.0/colorpicker/assets/";
        if(cfg.mode == 'button') {
            //button
            var currentColorId = uuid + "_current-color";
            
            // Create a Menu instance to house the ColorPicker instance
            var menu = new YAHOO.widget.Overlay(uuid + "_menu");
            menu.cfg.setProperty("zIndex",1);
            
            // Create a Button instance of type "split"
            var button = new YAHOO.widget.Button(containerId,{
                type: "split",
                label: '<em id="' + currentColorId + '" class="current-color"></em>',
                menu: menu });
            
            //Create a placeholder for ColorPicker And
            //render the Menu into the Button's container
            menu.setBody("&#32;");
            menu.body.id = uuid + "_overlay";
            menu.render(containerId);
            
            // Align the Menu to its Button
            menu.align();
            menu.hide();
            
            //Create a new ColorPicker instance, placing
            //it inside the body element of the Menu instance.
            wrapper = new YAHOO.widget.ColorPicker(menu.body.id, {
                showcontrols: false,
                images: {
                    PICKER_THUMB: baseUrl + "picker_thumb.png",
                    HUE_THUMB:    baseUrl + "hue_thumb.png"
                }
            });
            
            
            //reflect user's color changes on button's color square
            wrapper.on("rgbChange", function () {
                YDom.setStyle(currentColorId, "backgroundColor",
                    "#" + this.get("hex")
                );
                jmaki.publish(publish + "/onSelect", {widgetId:uuid, value:self.getValue()} );
            });
            
        } else
        {
            //cfg.mode == 'normal'
            wrapper = new YAHOO.widget.ColorPicker(containerId, {
                showcontrols: cfg.showControls,
                showhexcontrols: cfg.showHexControls,
                showhexsummary: cfg.showHexSummary,
                showhsvcontrols: cfg.showHsvControls,
                showrgbcontrols: cfg.showRgbControls,
                showwebsafe: cfg.showWebSafe,
                images: {
                    PICKER_THUMB: baseUrl + "picker_thumb.png",
                    HUE_THUMB:    baseUrl + "hue_thumb.png"
                }
            });
            //subscribe to the rgbChange event
            var onRgbChange = function() {
                jmaki.publish(publish + "/onSelect", {widgetId:uuid, value:self.getValue()} );
            };
            wrapper.on("rgbChange", onRgbChange);
            
            YDom.setStyle(containerId,"display","block");
            
        }
        
        //set initial color...
        self.setValue(cfg.red,cfg.green,cfg.blue,false);
        
        // track the subscribers so we can later remove them
        self.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/select", self.select);
        }
        
    }
    
    if (!jmaki.loaded) jmaki.subscribe("/jmaki/runtime/loadComplete",initColorPicker);
    else initColorPicker();
};

jmaki.debug = true;

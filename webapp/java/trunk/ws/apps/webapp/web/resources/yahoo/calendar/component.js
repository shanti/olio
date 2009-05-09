// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.calendar");

/**
* Yahoo UI normal & split-button Calendar Widget
*
* Code updated to use new split button and improved
* Note that date format is using the same formats supported by
* YAHOO.util.Date.format
*
* @author Greg Murray
* @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
* @constructor
* @see http://developer.yahoo.com/yui/calendar/
*/
jmaki.widgets.yahoo.calendar.Widget = function(wargs) {
    var publish = "/yahoo/calendar";
    var subscribe = ['/yahoo/calendar','/calendar'];
    var self = this;
    var uuid = wargs.uuid;
    var containerId = uuid + "_calendar";
    var wrapper = null;
    var button = null;
    var menu = null;

    //abbreviations
    var YDom = YAHOO.util.Dom;

    //default parameters
    var date = new Date();
    var mode = "normal";
    var format = "YYYY/MM/DD";

    //read the widget configuration arguments
    if(typeof wargs.args != 'undefined') {
        var args = wargs.args;

        //overide topic name if needed
        if(typeof args.topic != 'undefined') {
            publish = args.topic;
            jmaki.log("Yahoo calendar: widget uses deprecated topic. Use publish instead.");
        }
        if(typeof args.mode != 'undefined') {
            var modes = {'button':'button', 'normal':'normal'};
            var m = modes[args.mode];
            mode = (typeof m != 'undefined') ? m : 'normal';
        }
        if(typeof args.format != 'undefined') {
            var formats = {
                "MM/DD/YYYY":"MM/DD/YYYY", "YYYY/MM/DD":"YYYY/MM/DD",
                "DD/MM/YYYY":"DD/MM/YYYY"
            };
            var o = formats[args.format];
            format = (typeof o != 'undefined') ? o : "YYYY/MM/DD";
        }
    }

    if(wargs.publish) {
        publish = wargs.publish;
    }
    if(wargs.subscribe) {
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
    }

    //read date from value
    if(wargs.value) {
        var value = wargs.value;
        if(/\d{1,2}\/\d{1,2}\/\d{4}/.test(value)) {
            //accept only mm/dd/yyyy
            date = new Date(value);
        } else
        {
            jmaki.log("value must formatted as MM/DD/YYYY");
        }
    }

    // add a saveState function where service is ued
    if(wargs.service) {
        this.saveState = function() {
            var value = self.getValue();
            if(value) {
                // we need to be able to adjust this
                jmaki.doAjax({
                    url: wargs.service + "?cmd=update",
                    method: "post",
                    content: {value : value.toString()},
                    callback: function(req) {
                        if (req.readyState == 4) {
                            if (req.status == 200) {
                                // take some action if needed
                            }
                        }
                    }}
                );
            } else
            {
                jmaki.log("value is null");
            }
        };
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
    * Returns first selected date
    */
    this.getValue = function() {
        if(!wrapper) {
            jmaki.log("Calendar is not yet initialized. Aborting...");
            return null;
        } else if(wrapper.getSelectedDates().length >0)
        {
            return wrapper.getSelectedDates()[0];
        } else
        {
            return null;
        }
    };

    /**
    * sets the date...
    */
    this.setValue = function(date,renderNow) {
        if(!wrapper) {
            jmaki.log("Calendar is not yet initialized. Aborting...");
            return;
        }

        wrapper.select(date);


        var selectedDates = wrapper.getSelectedDates();
        if(selectedDates.length > 0) {
            var d = selectedDates[0];
       
           
            if(renderNow) {
                wrapper.render();
                //Workaround IE's border-collapse:collapse Issue
                //http://developer.yahoo.com/yui/container/#knownissues
                if(menu && !menu.cfg.getProperty("visible")) {
                    wrapper.hide();
                }
            }
        }
    };

    /**
    * Select a new date in the calendar
    */
    this.select = function(d) {
        if(!wrapper) {
            jmaki.log("Error: Calendar is not initialized.");
            return;
        }

        if(d && d.message && typeof d.message.value == "string") {
            var strDate = d.message.value;
            if(/\d{1,2}\/\d{1,2}\/\d{4}/.test(strDate)) {
                //accept only mm/dd/yyyy
                self.setValue(strDate,true);
            } else
            {
                jmaki.log("Can only accept date format must be DD/MM/YYYY");
            }
        } else
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
    * format date helper function
    */
    function formatDate(aDate) {
        var sDate = (aDate) ? YAHOO.util.Date.format(aDate,{format:format}) : "";
        return sDate;
    }

    /**
    * Initialize Calendar in normal or button overlay mode
    */
    function initCalendar() {
	//default onSelect handler
        var onSelect = function(type,args,obj) {
            if(args) {
                var dateObj = this._toDate(args[0][0]);
                jmaki.publish(publish + "/onSelect",
                    {widgetId:uuid,id:uuid,value:dateObj});
                if(button) {
                    button.set("label",formatDate(dateObj));
                }
            }
            if(menu) {
                menu.hide();
            }
        };        

        if(mode == "normal") {
            //normal calendar
            wrapper = new YAHOO.widget.Calendar(uuid,containerId);
            if (wargs.args != null && wargs.args.pagedate)  
                	wrapper.cfg.setProperty("pagedate",wargs.args.pagedate,false);  
                	                                                           
            if (wargs.args != null && wargs.args.selected)                                               
                wrapper.cfg.setProperty("selected",wargs.args.selected,false);
            else   //select an initial date
                self.setValue(date,false);           

            wrapper.render();
            
            wrapper.selectEvent.subscribe(onSelect, wrapper, true);

        } else
        {
            //button calendar
            // Create a Overlay instance to house the Calendar instance
            menu = new YAHOO.widget.Overlay(uuid + "_menu");

            // Create a Button instance of type "split"
            var buttonId = uuid + "_btn";
            button = new YAHOO.widget.Button(buttonId,{
                type: "split",
                label: formatDate(date),
                menu: menu
            });

            //Create a placeholder for calendar And
            //render the Menu into the Button's container
            menu.setBody("&#32;");
            menu.body.id = uuid + "_overlay";
            menu.render(buttonId);

            // Align the Menu to its Button
            menu.align();

            //Create a new calendar instance, placing
            //it inside the body element of the Menu instance.
            wrapper = new YAHOO.widget.Calendar(uuid,menu.body.id);
            wrapper.render();

            //Subscribe to the Calendar instance's "changePage" event to
            //keep the Overlay visible when either the previous or next page
            //controls are clicked.
            wrapper.changePageEvent.subscribe(function () {
                window.setTimeout(function () { menu.show(); }, 0);
            });

            //select an initial date
            self.setValue(date,false);
            wrapper.selectEvent.subscribe(onSelect, wrapper, true);

            //Workaround IE's border-collapse:collapse Issue
            //http://developer.yahoo.com/yui/container/#knownissues
            menu.hide();
            wrapper.hide();
            var onButtonOption = function() {
                wrapper.show();
            };
            button.on("option",onButtonOption);
        }

        // track the subscribers so we can later remove them
        self.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/select", self.select);
        }

    }
    if (!jmaki.loaded) jmaki.subscribe("/jmaki/runtime/loadComplete",initCalendar);
    else initCalendar();
};

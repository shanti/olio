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
 *
*/
jmaki.namespace("jmaki.widgets.blueprints.list.attendeeList");

jmaki.widgets.blueprints.list.attendeeList.Widget = function(wargs) {

    var self = this;    
    var container;
    var list;
    var data;
    attendeeWidget = this;
    
    if (wargs.args) {
        if (wargs.args.count) {
            count = Number(wargs.args.count);
        }
    }

    this.addItem = function(text) {
        var li = document.createElement("li");
        li.innerHTML = text;
        list.appendChild(li);
    }
    
    this.destroy = function() {
        delete list;
        delete container;
    }

    this.render = function() {
        list.innerHTML = "";
        if (typeof context == 'undefined')
            context = "";
        for (var _i=0; _i < data.attendees.length; _i++) {
            var val = "<a href=\"";
            val += context + "/person?actionType=display_person&user_name=";
            val += data.attendees[_i].userName;
            val += "\">";
            val += data.attendees[_i].userName;
            val += "</a>"
            self.addItem(val);
        }
    }
    
    this.applyTemplate = function(name, value, _t) {
         _t = _t.replace("@{" + name + "}", value);
        return _t;
    }   
    
    if (wargs.service) {
        jmaki.doAjax({url: wargs.service, callback: function(req) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var _in = eval('(' + req.responseText + ')');
                    if (_in) {
                        data = _in.result;
                    }
                    init();
                }
            }
        }});
    } else {
        alert ("Use service attribute");
    }

    function init() {
        var attendButton;
        if (data.status == "added" || data.status == "attending") {
            attendButton = "<input name=\"unattend\" type=\"button\" value=\"Unattend\" onclick=\"attendeeWidget.deleteAttendee();\"/>"; 
        }
        else if (data.status == "deleted" || data.status == "not_attending") {
            attendButton = "<input name=\"attend\" type=\"button\" value=\"Attend\" onclick=\"attendeeWidget.addAttendee();\"/>"; 
        }
        else {
            attendButton = "";
        }
        
        var elem = document.getElementById(wargs.uuid + "_h2");
        elem.innerHTML = "" + data.attendees.length + " Attendees:"
        elem = document.getElementById(wargs.uuid + "_attending");
        elem.innerHTML = attendButton;
           
        container = document.getElementById(wargs.uuid);
        list = document.getElementById(wargs.uuid + "_list");
        self.render();
    }
    
    this.addAttendee = function() {
        jmaki.doAjax({url: addAttendeeLink, callback:self.handleAttendee});
    }

    this.deleteAttendee = function() {
        jmaki.doAjax({url: deleteAttendeeLink, callback:self.handleAttendee});
    }
    
    this.handleAttendee = function(req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var _in = eval('(' + req.responseText + ')');
                if (_in) {
                    data = _in.result;
                }
                init();
            }
        }
        
        var payload = "attendeeListModified";
        jmaki.publish("/blueprints/list/attendeeList/onModify", payload);
    }
}

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
jmaki.namespace("jmaki.widgets.blueprints.list.friendList");

jmaki.widgets.blueprints.list.friendList.Widget = function(wargs) {

    var self = this;    
    var container;
    var list;
    var items = [];
    var index = 0;
    var count = 2;
    
    var next;
    var prev;
    var showNav = true;

    var data;

    if (wargs.args) {
        if (wargs.args.count) {
            count = Number(wargs.args.count);
        }
        if (typeof wargs.args.showNavigation != 'undefined') {        
            showNav = wargs.args.showNavigation;
            var nav  = document.getElementById(wargs.uuid + "_nav");
            if (nav && showNav == false) nav.style.display = "none";
        }
    }

    this.addItem = function(text) {
        var li = document.createElement("li");
        li.innerHTML = text;
        list.appendChild(li);
    }
    
    this.destroy = function() {
        delete list;
        delete next;
        delete prev;
        delete container;
    }

    this.render = function() {
        // remove the innerHTML
        list.innerHTML = "";
        for (var _i=index; _i < data.length && _i < (index + count); _i++) {
          self.addItem(items[_i]);
        }
        if ((index + count) >= data.length) {
            next.style.visibility = "hidden";
        } else {
            next.style.visibility= "visible";
        }
        if (index == 0) {
            prev.style.visibility = "hidden";
        } else {
            prev.style.visibility= "visible";
        }
    }
    

    
    this.getNext = function() {
        if (index + count < data.length) index += count;
        self.clearList();        
        self.render();
    }
    
    this.getPrevious = function() {
        if (index > 0){
            index -= count;
            prev.enabled = "true";
        } else {
            prev.enabled = "false";
            index = 0;
        }
        self.clearList();        
        self.render();
        
    }
    
    this.clearList = function() {
        for (var i=list.childNodes.length - 1; i > 0 ; i--) {
           list.removeChild(list.childNodes[i]);
        }
    }

    this.applyTemplate = function(_obj, _t) {
        for (var i in _obj) {
            var ta = "@{" + i + "}";

            while (_t.indexOf(ta) != -1) {
                     _t = _t.replace(ta, _obj[i]);   
            }
        }
        return _t;
    }       

    if (wargs.value && wargs.value.friendsList) {
        data = wargs.value.friendsList;
        init();
    } else if (wargs.service) {
        jmaki.doAjax({url: wargs.service, callback: function(req) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var _in = eval('(' + req.responseText + ')');
                    if (_in.friendsList) {
                        data = _in.friendsList;
                    }
                    init();
                }
      
            }
        }});
    }

    function init() {
       // this is the template for all li
       var template = unescape(document.getElementById(wargs.uuid + "_template").innerHTML + "");
       for (var _i=index; _i < data.length; _i++) {
         var f = {};
         f.name = data[_i].name;
         f.url = "person.jsp?user_name=" + data[_i].name;
         f.imageURL = data[_i].imageURL;
         items.push(t = self.applyTemplate(f, template));
         delete f;
       }    
       container = document.getElementById(wargs.uuid);
       list = document.getElementById(wargs.uuid + "_list");
       next = document.getElementById(wargs.uuid + "_next");
       prev = document.getElementById(wargs.uuid + "_previous");
       next.onclick = self.getNext;
       prev.onclick = self.getPrevious;
       self.render();
    }

}

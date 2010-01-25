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
jmaki.namespace("jmaki.widgets.yahoo.editor");

/**
 * Yahoo UI Rich Text Editor Widget
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
 * @constructor
 * @see http://developer.yahoo.com/yui/editor/
 */
jmaki.widgets.yahoo.editor.Widget = function(wargs) {
    var publish = "/yahoo/editor";
    var _widget = this;
    var uuid = wargs.uuid;
    
    var _container = document.getElementById(wargs.uuid);
    var ie = /MSIE/i.test(navigator.userAgent);
    var safari = /WebKit/i.test(navigator.userAgent);
 
    // yahoo requires the document body have a yui-skin-sam class on it
    if (document.body) {
        if (!/yui-skin-sam/.test(document.body.className)) document.body.className += " yui-skin-sam";
    }

    //read arguments
    if (wargs.publish) publish = wargs.publish;

    var dim = jmaki.getDimensions(_container);
    // 82 is the height of the menubar
    var eh = dim.h + 82;
    
    var editorCfg = {
        height: eh  - 2 + "px",
        width:  dim.width + "px",
        dompath: true, //Turns on the bar at the bottom
        animate: true //Animates the opening, closing and moving of Editor windows
    }
    //create the rich text editor
    this.wrapper = new YAHOO.widget.Editor(uuid + "_container", editorCfg);

    //only initialize after editor contents has loaded
    this.wrapper.on('editorContentLoaded', function(ev) {
        YAHOO.log('editorContentLoaded');
        //read value first into editor
        if (typeof wargs.value != 'undefined') {  
            _widget.setValue(wargs.value);
        } 
        var e = jmaki.getElementsByStyle("yui-toolbar-subcont", document.getElementById(uuid));
        if (e[0]) {
             e[0].style.height = "96px";
        }        
        //read service url contents into editor
        if(typeof wargs.service != 'undefined') {
            jmaki.doAjax({url: wargs.service, callback: function(req) {
                var response = req.responseText;
                YAHOO.log('response = ' + response);
                if(response.length > 0) {
                    _widget.setValue(response);
                }
            }});
        }    
    });


    //Subscribe to the toolbarLoaded Custom event fired in render
    this.wrapper.on('toolbarLoaded', function() { 
                YAHOO.log('Editor Toolbar Loaded..', 'info', 'example');
                
        //Setup the config for the new "Insert Icon" button
        var saveCfg = {
            type: 'push', //Using a standard push button
            label: 'Save', //The name/title of the button
            value: 'save' //The "Command" for the button            
        };
        //Add the new button to the Toolbar Group called insertitem.        
        _widget.wrapper.toolbar.addButtonToGroup(saveCfg, 'insertitem');
        //subscribe to click event
        _widget.wrapper.toolbar.on('saveClick', function(ev) {
            /*var icon = '';
            this._focusWindow();
            if (ev.icon) {
                icon = ev.icon;
            }
            this.execCommand('insertimage', icon);
            */
            //TODO save icons must be provided
            YAHOO.log('saveClick calling saveState!');
            _widget.saveState();
        }, _widget.wrapper, true);
    });

    //render editor
    this.wrapper.render();
        
        
    /**
     * Sets editor html contents
     */
    this.setValue = function(html) {
        this.wrapper.setEditorHTML(html);
    }

    /**
     * Returns editor html contents (unfiltered)
     */
    this.getValue = function() {
        return this.wrapper.getEditorHTML();
    }

    /**
     * Save state when asked ;-)
     */    
    this.saveState = function() {
	jmaki.publish(publish + "/onSave", {widgetId: wargs.uuid, value: _widget.getValue()});
    }
};

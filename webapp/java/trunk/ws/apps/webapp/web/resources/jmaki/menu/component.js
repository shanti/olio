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
jmaki.namespace("jmaki.widgets.jmaki.menu");

jmaki.widgets.jmaki.menu.Widget = function(wargs) {
    
    var _widget = this;
    _widget.model = null;	
    _widget.container = document.getElementById(wargs.uuid);

    var topMenus = {};
    var navMenus = {};
    var menus = [];
    var hideTimer;
    var publish = "/jmaki/menu";
       
    var currentTheme = "default";

    _widget.container.className += " jmk-menu-title-" + currentTheme;

    function addStyle(style, nStyle){
       if (style.indexOf(nStyle) != -1) return style;
       if (style.length > 0) style += " ";
       return (style + nStyle);
    }

    function removeStyle(style, oStyle){
        if (style.indexOf(oStyle) == -1) return style;
        var styles = style.split(' ');
        var nStyle = "";
        for (var i = 0; i < styles.length; i++) {
            if (styles[i] != oStyle) nStyle += styles[i] + " ";
        }
        return nStyle;
    }

    function showMenu(e){
        hideMenus();
        var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
        var pos = jmaki.getPosition(topMenus[src.id]);
        navMenus[src.id].style.top = pos.y + _widget.container.clientHeight - 2 + "px";
        navMenus[src.id].style.left = pos.x + "px";
        navMenus[src.id].style.display = "block";
    }

    function processActions(_t, _pid, _type, _value) {
    	jmaki.processActions( {
    		value : _value,
    		type : _type,
    		action : _t.action,
    		widgetId : wargs.uud,
    		targetId : _pid,
    		topic : publish
    	});
    }

    function labelSelect(e) {
        hideMenus();
        var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
        var sp = src.id.split("_");
        var href;
        var url = src.item.url;
	if (typeof url == 'undefined') {
	    href = src.item.href;
	} else {
	    href = url;
	    jmaki.log("jMaki menu: widget uses deprecated url property. Use href instead. ");
	}
        if (href) {
            window.location.href = href;
        }else if (src.item.action ) {
            processActions(src.item, src.item.targetId);
            
        } 
    }

    function menuSelect(e){
        hideMenus();
        var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
        var sp = src.id.split("_");
        var href;
        var url = src.item.url;
	if (typeof url == 'undefined') {
	    href = src.item.href;
	} else {
	    href = url;
	    jmaki.log("jMaki menu: widget uses deprecated url property. Use href instead. ");
	}
        if (href) {
            window.location.href = href;
        } else {
            processActions(src.item, src.item.targetId);
        }
    }
    
    function hideMenus(){
        for (var _i in menus) {
            if (menus[_i].style) menus[_i].style.display = "none";
        }
        for (var _i2 in topMenus) {
            if (topMenus[_i2].className) topMenus[_i2].className = removeStyle(topMenus[_i2].className, "jmk-menu-bg-hover");
        }
    }
    
    function startHide() {
        hideTimer = setTimeout(hideMenus, 2000);
    }
    
    function stopHide() {
        if (hideTimer != null)  clearTimeout(hideTimer);
    }

    function menuList(menuStyle) {
        var menuList = document.createElement("li");
        menuList.className = menuStyle;
        menuList.onmouseout = startHide;
        menuList.onmousemove = stopHide;
        return menuList;
    }

    function menuElement(label, menuStyle, id, item) {
        var menuE = document.createElement("li", menuStyle);
        menuE.className = menuStyle;
        menuE.id = id;
        menuE.item = item;
        var tmi = document.createTextNode(label);
        menuE.appendChild(tmi);
        return menuE;
    }
    
    this.clear = function() {
        //TODO : make this use dom remove
       _widget.container.innerHTML = "";
       topMenus = {};
       navMenus = {};
       menus = [];
       _widget.model = [];
       hideTimer = null;       
    };

    this.init = function(value) {
        
        if (wargs.publish) {
	    publish = wargs.publish;
        }
        
        // since we add right to left reverse the ordering
        _widget.model = value.menu.reverse();
        
        var menuUL = document.createElement("ul");
        menuUL.className = "jmk-menu-top";
        _widget.container.appendChild(menuUL);
        var endSpacer = document.createElement("li");
        endSpacer.className = "jmk-menu-top jmk-menu-end-spacer";
        menuUL.appendChild(endSpacer);
        
        for (var i=0; i < _widget.model.length; i++) {
            var me = menuElement(_widget.model[i].label, "jmk-menu-top", wargs.uuid + "_topmenu_" + i, _widget.model[i]);
            if (_widget.model[i].menu) {
                me.onmouseover = showMenu;
                
            } else {
                me.onclick = labelSelect;
            }
            me.onmousemove = function(e){            
                var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
                src.className = removeStyle(src.className, "jmk-menu-top");
                src.className = addStyle(src.className, "jmk-menu-top-hover");
            }
            me.onmouseout = function(e){                 
                var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
                src.className = removeStyle(src.className, "jmk-menu-top-hover");
                src.className = addStyle(src.className, "jmk-menu-top");    
            }
            menuUL.appendChild(me);
            topMenus[wargs.uuid + "_topmenu_" + i] = me;
            
            var ml = menuList("jmk-menu-container jmk-menu-container-" + currentTheme);
            navMenus[wargs.uuid + "_topmenu_" + i] = ml;
            menuUL.appendChild(ml);
            menus.push(ml);
            if (_widget.model[i].id) _widget.model[i].targetId = _widget.model[i].id;
            else _widget.model[i].targetId = wargs.uuid+"_menu_"+i;
            
            if (i < _widget.model.length -1) {
                var spacerDiv = document.createElement("li");
                spacerDiv.appendChild(document.createTextNode("|"));
                spacerDiv.className = "jmk-menu-separator";
                menuUL.appendChild(spacerDiv);
            }
        }
        
        for(var oi=0; oi<_widget.model.length; ++oi) {
            var mis = _widget.model[oi].menu;
	    if ( typeof mis != 'undefined') { // not just a label
                for (var ii=0; ii < mis.length; ii++){
                    var mi = menuElement(mis[ii].label, "jmk-menu-item-" + currentTheme, wargs.uuid + "_" + oi + "_" + ii,mis[ii]); 
                    mi.onclick = menuSelect;
                    mi.onmouseout = function(e){
                        var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
                        src.className = removeStyle(src.className, "jmk-menu-bg-hover");
                    };
                    mi.onmousemove = function(e){
                        var src = (typeof window.event == 'undefined') ? e.target : window.event.srcElement;
                        src.className = addStyle(src.className, "jmk-menu-bg-hover");
                    }
                    navMenus[wargs.uuid + "_topmenu_" + oi].appendChild(mi);
                    if (mis[ii].id) mis[ii].targetId = mis[ii].id;
                    else mis[ii].targetId = wargs.uuid+"_menu_"+ii;
                }
            }
        }
    };
    
    this.postLoad = function() {
        if (wargs.publish) publish = wargs.publish;
        if (wargs.value) {
            _widget.init(wargs.value);
        } else if (wargs.service) {
            jmaki.doAjax({url: wargs.service, callback: function(req) {
                    var data = jmaki.json.deserialize(req.responseText);
                    _widget.init(data);
                }});
        }  
    };
};

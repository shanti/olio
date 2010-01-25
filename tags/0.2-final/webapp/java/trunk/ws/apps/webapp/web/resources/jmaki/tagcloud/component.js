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
jmaki.namespace("jmaki.widgets.jmaki.tagcloud");

jmaki.widgets.jmaki.tagcloud.Widget = function(wargs) {
    
    var _widget = this;
    _widget.model = {};
    var defaultSize = 15;
    
    var publish = "/jmaki/tagcloud";
    var publishSubType = "onSelect";
    
    if (wargs.args) {     
        if (wargs.args.tagSelectSubType) {
            publishSubType = wargs.args.tagSelectSubType;
        }
    }
    
    var tags = [];

    _widget.outerContainer = document.getElementById(wargs.uuid);
    _widget.container = document.getElementById(wargs.uuid + "_container");
    
    var padding = 2;
    
    this.resize = function() {
	    var dim = jmaki.getDimensions(_widget.outerContainer,55);
	    var startHeight = dim.h;    
	    _widget.outerContainer.style.height = startHeight - 2 + "px";  
	    startHeight -= 52;
	    _widget.container.style.height = startHeight + "px";
    };

    function collapseAll(target) {
        for (var i=0; i < tags.length; i++) {
          if (tags[i].expander && (target != tags[i])) {
              tags[i].isExpanding = false;
              
              tags[i].collapseImmediately = true;
              if (tags[i].isCollapsed == false){
                      collapse(tags[i].expander, tags[i]);
              }  
          }    
        }
    }
    
    function collapse(expander, target) {
       
        if (target.currentSize > target.initialSize) {
            target.currentSize -= 1;
            expander.style.fontSize = target.currentSize + "px";
            // keep exapanding
            setTimeout(function() {collapse(expander, target)}, 15);
        } else {
            expander.style.fontSize = target.initialSize + "px";           
            target.isCollapsed = true;
            target.style.visibility = "visible";
            target.isCollapsing = false;
            target.collapseImmediately = false;
            expander.style.display = "none";
        }
    }
    
   function bubble(expander,target) {
                
        if (target.currentSize < target.maxSize && target.isExpanding == true) {
            target.currentSize += 1;
            expander.style.fontSize = target.currentSize + "px";
            // keep exapanding
            setTimeout(function() {bubble(expander, target)}, 15);
            
        } else {
            target.isExpanding = false;

            if (target.collapseImmediately == true) {
                collapseHandler(target);
            }
        }
    }
    
    function collapseHandler(target) {
     
        if (target.isExpanding == true){ 
            target.collapseImmediately = true;
            return;
        }
        if (target.isCollapsing == true) return;

        setTimeout(function() {collapse(target.expander, target);}, 250);

    }
    
    function positionExpander(target) {
             // set the relative location
            var pX = 0;
            var pY = 0;
            if(target.offsetParent) {
                    pY += target.offsetTop;
                    pX += target.offsetLeft;
            } else if(target.y) {
                    pY += target.y;
                    pX += target.x;
            }            
            target.expander.style.left = pX + "px";
            target.expander.style.top = pY + "px";
    }
    
    function bubbleHandler() {
        collapseAll();
        var target = this;
        target.collapseImmediately = false;

        target.isExpanding = true;
        target.isCollapsed = false;
        if (!this.expander) {
            this.isCollapsing = false;
            this.expander = document.createElement("div");
            this.expander.className = "jmk-tagcloud-expander";
            this.expander.innerHTML = this.innerHTML;            
            this.expander.mousemove = function() {
              if (target.timeout) clearTimeout(target.timeout);  
            };
            this.expander.onmouseout = function() {
                target.isExpanding = false;         
                collapseHandler(target);
            };
            this.expander.onclick = function() {
            	var _iv = { weight : target.item.weight, label : target.item.label };
            	if (target.item.value) _iv.value = target.item.value;
                jmaki.processActions({ topic : publish, 
                    widgetId : wargs.uuid,
                    action : target.item.action, 
                    targetId : target.tagId,
                    type : publishSubType,
                    value : _iv
                });
         
                if (target.item.href && !target.item.target) {
                    window.location.href = target.item.href;
                } else if (target.item.target) {               
                    target.target = target.item.target;                    
                    target.href= target.item.href;
                }
            };
            positionExpander(target);
            _widget.container.parentNode.appendChild(this.expander);
        } else {
            positionExpander(target);
            this.expander.style.display = "block";
        }
        this.style.visibility = "hidden";    
        bubble(this.expander, this);
    }
    
    function init(){
		_widget.container.className = "jmk-tagcloud-container";
        if (_widget.model) {
            for (var i=0; i < _widget.model.items.length;i++) {
            	_widget.addTag(jmaki.clone(_widget.model.items[i]));        
            }
        }
    }

    this.addTag = function(t) {
        var tag = document.createElement("div");
        tag.innerHTML = t.label;
        tag.item = t;
        tag.className = "jmk-tagcloud-tag";
        if (!t.id) tag.tagId = wargs.uuid + "_tag_" + jmaki.genId();
        else tag.tagId = t.id;
        if (!t.weight) tag.weight = .50;
        else tag.weight = t.weight / 100;

        tag.initialSize = (defaultSize * tag.weight);
        tag.style.fontSize = tag.initialSize + "px";
        tag.currentSize = tag.initialSize;
        tag.expanding = false;
        // allow for 3 times growth
        tag.maxSize = tag.initialSize * 1.5;
        tag.onmouseover = bubbleHandler;

        tag.onclick = bubbleHandler;

        _widget.container.appendChild(tag);
        tags.push(tag);
    }; 
    
    this.postLoad = function() {
        _widget.resize();    	
        if (wargs.publish) publish = wargs.publish;
        if (wargs.value) {
            _widget.model = wargs.value;
            init();
        } else if (wargs.service) {
            jmaki.doAjax({url: wargs.service, callback: function(req) {
                _widget.model = eval('(' + req.responseText + ')');
                init();
          }});
        }
    };
};

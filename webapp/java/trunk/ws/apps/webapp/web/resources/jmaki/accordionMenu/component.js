/* Copyright 2008 You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at:
 http://developer.sun.com/berkeley_license.html
 $Id: component.js,v 1.0 2007/04/15 19:39:59 gmurray71 Exp $
*/
// define the namespaces
jmaki.namespace("jmaki.widgets.jmaki.accordionMenu");

jmaki.widgets.jmaki.accordionMenu.Widget = function(wargs) {

    var _widget = this;
    var publish = "/jmaki/accordionMenu";
    var subscribe =  ["/jmaki/accordionMenu", "/accordion"];
       
    var EXPANDED_HEIGHT = 125;
    var ITEM_HEIGHT = 0;
    var INCREMENT = 12;
    var SMALL_INCREMENT = 1;    
    
    var timeout = 5; // in ms
    
    _widget.container = document.getElementById(wargs.uuid);
    // TODO : Need to calculate border based on border and not hardcode
    // padding = 2 (outer element) 2 inner + 4 x 2 on the inner padding
    var padding = 12;

       
    var  panes = [];
    var oExpandedIndex = -1;
    var nExpandedIndex = -1;
    var oHeight = ITEM_HEIGHT;
    var nHeight = ITEM_HEIGHT;
    var tHeight = 0;
    var expanding = false;
    
    var paneMappings = {};
    
    var debug = false;
    
    if (wargs.publish) {
        publish = wargs.publish;
    }

    var dim = jmaki.getDimensions(_widget.container, 35);   

    var startHeight = dim.h - padding;
    _widget.container.style.height = startHeight + "px";
   
    function createLinks(tDiv, text, id, linkStyle) {
        var link = document.createElement("a");
        link.className = linkStyle;  
        link.appendChild(document.createTextNode(text));
        link.setAttribute("id", id);
        if (link.attachEvent) {
            link.attachEvent('onmouseover',function(e){initiateExpansion(e.srcElement.getAttribute("id"));});
        } else if (link.addEventListener) {
            link.addEventListener('mouseover',function(e){initiateExpansion(e.currentTarget.getAttribute("id"));}, true);
        }
        tDiv.appendChild(link);
    }    
    function Pane(l, item) {
       this.id = l;
       var pane = document.createElement("div");
       pane.className = "jmk-accordion-container-pane";
       this.labelDiv = document.createElement("div");
       this.labelDiv.paneId = l;
       this.labelDiv.onclick = function() {
       	initiateExpansion(this.paneId);
       };
       this.labelDiv.className = "jmk-accordion-row jmk-accordion-title";
       this.label = document.createElement("div");
       this.label.className = "jmk-accordion-label";     
       createLinks(this.label, item.label, l, "jmk-accordion-link");
       this.labelDiv.appendChild(this.label)
       pane.appendChild(this.labelDiv);       
       // this is the part the will expand and contract
       this.content = document.createElement("div");
       this.content.className = "jmk-accordion-content";
       pane.appendChild(this.content);
       this.height = 0;
       addMenuItems(item, this.content);       

       this.content.style.height = 0 + "px"
       _widget.container.appendChild(pane);        
       
    }
    
    Pane.prototype.setHeight = function(nH) {
        this.h = nH;
        if (nH <= 0) nH =0;
        if (nH >= 0) this.content.style.height = nH + "px";
        else this.content.style.height = "0px";
    }
    
    Pane.prototype.getTotalHeight = function() {
        return this.content.offsetHeight;
    }
    
    Pane.prototype.getHeight = function() {
            return this.h;
    }
    
    this.handleEvent = function(args) {
        if (args.type) {
            if (args.type == 'expand') {
                var targetPane = args.targetPane;
                initiateExpansion(targetPane);
            }
        }
    }
  
    function addMenuItems(item, content) {
        for (var l= 0; item.menu && l < item.menu.length; l++) {
            var span = document.createElement("div");
            span.className = "jmk-accordion-menuitem";

            var link = document.createElement("a");
            var target = item.menu[l].id;
            link.id = target;
            link.className = "jmk-accordion-link";
            link.item =  item.menu[l];
            link.labelText = item.menu[l].label;

            link.onclick = function(e) {
                if (!e) var e = window.event
                var t;
                if (e.target) t= e.target;
                else if (e.srcElement) t = e.srcElement;            
                jmaki.processActions({
                    targetId : this.id,
                    topic : publish,
                    widgetId : wargs.uuid,
                    action : t.item.action,
                    value : this.labelText,
                    type : 'onSelect'
                 });
                // now process hyperlinks
                if (t.item.href) {
                    // if it's just a href nativate to it
                    if (t.item.href && !t.item.target) {
                        window.location.href = t.item.href;
                    } else if (t.item.target) {
                        t.target = t.item.target;                    
                        t.href= t.item.href;
                    }
                }
            }

            link.appendChild(document.createTextNode(item.menu[l].label));

            span.appendChild(link);

            content.appendChild(span);

            if (item.menu && l < item.menu.length - 1) {
                var spacer=document.createElement("p");
                spacer.className = "jmk-accordion-menuitem-spacer";
                content.appendChild(spacer);
            }
            link = null;
        }
        content.style.display= "none";
    }
           
    this.load = function() {
        var selected = 0;
        // create all the rows
        for (var l=0; l < _widget.model.length; l++) {
            var pane = new Pane(l,_widget.model[l]);
            if (!_widget.model[l].id) _widget.model[l].id = jmaki.genId();
            paneMappings[_widget.model[l].id] = l;
            panes.push(pane);
            if (_widget.model[l].selected) selected = l;
        }
        SMALL_INCREMENT = Math.round(INCREMENT / (panes.length - 1));
        var paneHeight = 18;
     
        if (panes.length > 0)paneHeight = panes[0].labelDiv.clientHeight;
        EXPANDED_HEIGHT = (startHeight  - (paneHeight * panes.length));
        initiateExpansion(selected);
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {         
            doSubscribe(subscribe[_i]  + "/select", _widget.select);
        }         
    }

    this.select = function(e) {
        var viewId;
        if (e.message)e = e.message;
        if (e.targetId) viewId = e.targetId;
        else viewId = e;
        if (viewId){
            if (paneMappings[viewId]) initiateExpansion(paneMappings[viewId]);
        }
    };
        
    function initiateExpansion(id) {        
        // jump out if we are in progress
        if (!expanding && oExpandedIndex != Number(id)) {
            expanding = true;
            nExpandedIndex = Number(id);
            panes[nExpandedIndex].content.style.display= "block";
            expandPane(id);
        }
    }
    
    function expandPane() {
        if (expanding) {
         
         if (nHeight < EXPANDED_HEIGHT) {
             var i = INCREMENT;
             if (nHeight + INCREMENT > EXPANDED_HEIGHT )
                 i = EXPANDED_HEIGHT - nHeight;
                nHeight += i;        
                panes[nExpandedIndex].setHeight(nHeight);

                if (oExpandedIndex != -1) {
                     oHeight = panes[oExpandedIndex].getHeight();
                     oHeight = oHeight - i; 
                     panes[oExpandedIndex].setHeight(oHeight);                       
                }
            } else {
                for (var i=0; i < panes.length;i++) {
                    if (i != nExpandedIndex) {
                        panes[i].content.style.display= "none";
                        panes[i].setHeight(0);  
                    }
                }
                expanding = false;
                if (panes[oExpandedIndex])
                    panes[oExpandedIndex].labelDiv.className = "jmk-accordion-row jmk-accordion-title";
                oExpandedIndex = nExpandedIndex;
                panes[nExpandedIndex].labelDiv.className = "jmk-accordion-row jmk-accordion-title-selected";
                nExpandedIndex = -1;
                oHeight = nHeight;
                nHeight = ITEM_HEIGHT;
                return;
            }
            setTimeout(expandPane, timeout);
        }
    }
 
     function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }
 
    this.postLoad = function() {
      if (wargs.value) {
          _widget.model = wargs.value.menu;
          _widget.load();
      } else if (wargs.service) {
           jmaki.doAjax({url: wargs.service, callback: function(req) {
                    if (req.responseText != '' ) {
                        var _data = eval('(' + req.responseText + ')');
                        _widget.model = _data.menu;
                        _widget.load();
                    }
                    
        }});
      } 
    }
};

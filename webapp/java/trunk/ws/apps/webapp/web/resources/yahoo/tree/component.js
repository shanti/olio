// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.tree");

/**
 * Yahoo UI Tree Widget
 * 
 * @author Gregory Murray <gregory.murray@sun.com> 
 *       (original author)
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com> 
 *       (Added proper glue/publish support with yahoo's new event model)
 * @constructor
 * @see http://developer.yahoo.com/yui/treeview/
 */
jmaki.widgets.yahoo.tree.Widget = function(wargs) {
    
    var _widget = this;
    var uuid = wargs.uuid;
    var publish = "/yahoo/tree";
    var subscribe = ["/yahoo/tree", "/tree"];
    var counter = 0;
    
    function genId() {
        return wargs.uuid + "_nid_" + counter++;
    }
    
    var nodes = [];
    var nodeIndex;

    var showedModelWarning = false;
    
    function showModelDeprecation() {
        if (!showedModelWarning) {
             jmaki.log("Yahoo tree widget uses the incorrect data format. Please see " +
                       "<a href='http://wiki.java.net/bin/view/Projects/jMakiTreeDataModel'>" +
                       "http://wiki.java.net/bin/view/Projects/jMakiTreeDataModel</a> for the proper format.");
             showedModelWarning = true;
        }   
    }
    
    function clone(t) {
       var obj = {};
       for (var i in t) {
            obj[i] = t[i];
       }
       return obj;
    }      
    
    if (wargs.publish) publish = wargs.publish;
    if (wargs.subscribe) subscribe = wargs.subscribe; 

    this.tree = new YAHOO.widget.TreeView(wargs.uuid);
    
    //Default on node expand handler
    this.tree.subscribe("labelClick", function(node) {
        if (node.children.length && !node.action) return;
        var _m = {widgetId: uuid, topic : publish, type:"onClick", targetId : node.nid };
        var topic = publish + "/onClick";
        var action = node.action;
        if (action && action instanceof Array) {
            for (var _a=0; _a < action.length; _a++) {
                var payload = clone(_m);
                if (action[_a].topic) payload.topic = action[_a].topic;
                else payload.topic = topic;
                if (action[_a].message) payload.message = action[_a].message;
                jmaki.publish(payload.topic,payload);
            }
        } else {
            if (node.action && node.action.topic) {
                topic = _m.topic = node.action.topic;
            }
            if (node.action && node.action.message) _m.message = node.action.message;                  
            jmaki.publish(topic,_m);
        }
        
    });
    
    //Default on node collapse handler
    this.tree.subscribe("collapse", function(node) {
        jmaki.publish(publish + "/onCollapse",{widgetId: wargs.uuid, topic : publish,type:"onCollapse", label: node.label, targetId : node.nid });
    });
    
    //Default on node label click handler
    this.tree.subscribe("expand", function(node) {
        jmaki.publish(publish + "/onExpand",{widgetId: wargs.uuid, topic : publish, type:"onExpand", label: node.label, targetId : node.nid});
    });
     
    this.findNode = function(nid, root) {
        var returnNode;

        if (typeof root == 'undefined') root = _widget.tree.getRoot();


        if (root.nid == nid) {
            returnNode = root;

            return root;
        }
        if (typeof root.children != 'undefined') {

            for (var ts =0; !returnNode && root.children && ts < root.children.length; ts++) {
                returnNode = _widget.findNode(nid, root.children[ts]);
            }
        }

        return returnNode;
    }
    
    this.expandNode = function(e) {

        var nid;
        if (e.message)e = e.message;
        if (e.targetId) nid = e.targetId;
        else nid = e;
        var target = _widget.findNode(nid);
     
        if (target){
             target.expand();
             // expand all parent treenodes
             while (target = target.parent) {
                 if (target.expand)target.expand();
             } 
         }      
    }
    
    this.collapseNode = function(e) {
        var nid;
        if (e.message)e = e.message;
        if (e.targetId) nid = e.targetId;
        else nid = e;
        var target = _widget.findNode(nid);    
        if (target){
             target.collapse();
        }     
    }
    
    this.addNodes = function(e, n) {
        var ch;
        if (e.message)e = e.message;
        if (e.value) ch = e.value;
        else ch = n;
        var nid;
        if (e.targetId) nid = e.targetId;
        else nid = e;
        var target = _widget.findNode(nid);
        if (!target)target = _widget.tree.getRoot();
        if (target && ch){       
         _widget.buildTree(ch, target); 
        }      
    }
    
    this.removeChildren = function(e){
        var nid;
        if (e.message)e = e.message;
        if (e.targetId) nid = e.targetId;
        else nid = e;

        var target = _widget.findNode(nid);
        if (target) {    
            _widget.tree.removeChildren(target);
        }
    }
    
    this.removeNode = function(e) {
        var nid;
        if (e.message)e = e.message;
        if (e.targetId) nid = e.targetId;
        else nid = e;

        var target = _widget.findNode(nid);

        if (target) _widget.tree.removeNode(target,true);  
    }
    
   function doSubscribe(topic, handler) {
        var i = jmaki.subscribe(topic, handler);
        _widget.subs.push(i);
    }
    
    this.destroy = function() {
        for (var i=0; _widget.subs && i < _widget.subs.length; i++) {
            jmaki.unsubscribe(_widget.subs[i]);
        }
    }

    this.postLoad = function() {
        _widget.subs = [];
        for (var _i=0; _i < subscribe.length; _i++) {
            doSubscribe(subscribe[_i]  + "/removeNode", _widget.removeNode);
            doSubscribe(subscribe[_i] + "/removeChildren", _widget.removeChildren);
            doSubscribe(subscribe[_i] +"/addNodes", _widget.addNodes);
            doSubscribe(subscribe[_i] + "/expandNode", _widget.expandNode);
            doSubscribe(subscribe[_i]  + "/collapseNode", _widget.collapseNode);
        }      
    }
    
    /**
     * Builds the tree programtically (recursively)
     */
    this.buildTree = function(root, parent) {
        
        var rChildren = (root.children);
        var rExpanded = (root.expanded && (root.expanded == true));
        
        if (typeof parent == 'undefined') {
            parent = _widget.tree.getRoot();
        }
        
        // Backwards compatibility -- copy "title" to "label" if needed
        // but we will use "label" henceforth
        if (root.title && !root.label) {
            root.label = root.title;
        }
        // End of backwards compatibility hack
        
        var rNode = new YAHOO.widget.TextNode(root.label, parent, rExpanded);
        if (root.id) rNode.nid = root.id;
        else rNode.nid = genId();
        
        rNode.action = root.action;
        
        for (var t=0; root.children && t < root.children.length; t++) {
            var n = root.children[t];
            var hasChildren = (typeof n.children != 'undefined');
            var isExpanded = (typeof n.expanded  != 'undefined' && n.expanded == true);
            
            // Backwards compatibility -- copy "title" to "label" (as above)
            if (n.title && !n.label) {
                n.label = n.title;
            }
            // End of backwards compatibility hack
            var lNode = new YAHOO.widget.TextNode(n.label, rNode, isExpanded);
            if (n.id) lNode.nid = n.id;
            else lNode.nid = genId();
            lNode.action = n.action;
            
            //  recursively call this function to add children
            if (typeof n.children != 'undefined') {
                for (var ts=0; n.children && ts < n.children.length; ts++) {
                    _widget.buildTree(n.children[ts], lNode);
                }
            }
        }
        _widget.tree.draw();
    }
    
    //read the widget configuration arguments
    if (typeof wargs.args != 'undefined') {
        //overide publish name if needed
        if (typeof wargs.args.publish != 'undefined') {
            publish = wargs.args.publish;
        }  
    }
    if (wargs.publish) {
	publish = wargs.publish;
    }   
    if (wargs.subscribe){
        if (typeof wargs.subscribe == "string") {
            subscribe = [];
            subscribe.push(wargs.subscribe);
        }
    }     
    
    // use the default tree found in the widget.json if none is provided
    if (!wargs.value ) {
        var callback;
        // default to the service in the widget.json if a value has not been st
        // and if there is no service
        if (typeof wargs.service == 'undefined') {
            wargs.service = wargs.widgetDir + "/widget.json";
            callback = function(req) {
                    var obj = eval("(" + req.responseText + ")");
                    var jTree = obj.value.defaultValue;
                    var root = jTree.root;
                    _widget.buildTree(root);
            }
            
        } else {
            callback = function(req) {
                if (req.responseText =="") {
                    container.innerHTML = "Error loading widget data. No data."
                    return;
                }                
                var jTree = eval("(" + req.responseText + ")");
                if (!jTree.root){
                    showModelDeprecation();
                    return;
                }
                var root = jTree.root;
                _widget.buildTree(root);
            }        
        }
        var ajax = jmaki.doAjax({url : wargs.service,
                                 callback : callback,
                                 onerror : function() {container.innerHTML = "Unable to load widget data."}});
    } else if (typeof wargs.value == 'object') {
        if (wargs.value.collapseAnim) {
            _widget.tree.setCollapseAnim(wargs.value.collapseAnim);
        }
        if (wargs.value.expandAnim) {
            _widget.tree.setExpandAnim(wargs.value.expandAnim);
        }
        if (!wargs.value.root){
            showModelDeprecation();
            return;
        }
        _widget.buildTree(wargs.value.root);
    }    
}


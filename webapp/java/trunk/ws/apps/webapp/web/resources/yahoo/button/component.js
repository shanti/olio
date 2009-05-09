// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.button");

/**
* Yahoo UI Button Widget
* @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
* @constructor
* @see http://developer.yahoo.com/yui/button/
*/
jmaki.widgets.yahoo.button.Widget = function(wargs) {
    
    var publish = "/yahoo/button";
    var self = this;
    var button;
    var action;
    var buttonGroup;
    var uuid = wargs.uuid; 
    var buttonId = uuid + "_btn";
    var publishSound = false;
    var cfg;
    
    YAHOO.log("buttonId = " + buttonId);
    
    //button default event handlers
    this.onClick = function() {
        var _action;
        if (wargs.value) _action = wargs.value.action;
        jmaki.processActions(
         { topic : publish,
           targetId : 'default',
          widgetId : wargs.uuid,
          action : _action,
          type : 'onClick',
          value : cfg.label  
         }
        );
        //button, 'default', 'onClick');
        if (publishSound) {
            jmaki.publish('/jmaki/sound',
                {
                    widgetId :buttonId, 
                    url:wargs.widgetDir + '/audio/click.mp3'
                }
            );      
        }
    }
    
    this.onChange = function(e) {
        jmaki.publish(publish + "/onChange", {id:uuid,
            value:{oldValue:e.prevValue,newValue:e.newValue}});
    };
    
    //Configuration
    cfg = {
        name: buttonId,
        type: "button",
        label: "Click me",
        href: "http://ajax.dev.java.net",
        checked: false,
        val: "",
        buttons: [
            { label:'One', value:'1'}         
        ]
    };
    
    //read the widget configuration arguments
    if (typeof wargs.args != 'undefined') {
        
        //overide topic name if needed
        if (typeof wargs.args.topic != 'undefined') {
            publish = wargs.args.topic;
        }  
        
        if (typeof wargs.publishSound != 'undefined') {
            publishSound = wargs.args.publishSound;
        }
        
        if (typeof wargs.args.val != 'undefined') {
            cfg.val = wargs.args.val;
        }
        
        
        if (typeof wargs.args.label != 'undefined') {
            cfg.label = wargs.args.label;
        }
        if (typeof wargs.args.href != 'undefined') {
            cfg.href = wargs.args.href;
        }
        
        if (typeof wargs.args.container != 'undefined') {
            //deprecated argument
            jmaki.log("Yahoo button: widget uses deprecated 'container' argument. Simply remove it.");
        }
        
    }
    if (typeof wargs.value != 'undefined') {
        v = wargs.value;
        if(typeof v.buttons != 'undefined') {
            cfg.buttons = v.buttons;
        }
        if (wargs.value.action) action=wargs.value.action;
    }
    
    if (wargs.publish) publish = wargs.publish;
    
    if (wargs.value && wargs.value.label) cfg.label = wargs.value.label;
    if (wargs.value && wargs.value.value) cfg.val = wargs.value.value;
    
    /**
    * initialize the button
    */
    function initButton() {
        // Create the button
        button = new YAHOO.widget.Button(
            buttonId,
            {
                name: cfg.name,
                value: cfg.val,
                type: cfg.type, 
                label: cfg.label,
                checked: cfg.checked,
                href: cfg.href
            }
        );
        if (wargs.value && wargs.value.action) button.action = wargs.value.action;
        button.addListener("click", self.onClick);
    }
    
    /**
    * Returns the current value of the button
    */
    this.getVal = function() {
        return button.get("value");
    };
    
    /**
    * Sets the disabled property for a button
    */
    this.setDisabled = function(disabled) {
        if(typeof button  != 'undefined') {
            button.set("disabled",disabled);
        }
    };
    
    if (!jmaki.loaded) jmaki.subscribe("/jmaki/runtime/loadComplete",initButton);
    else initButton();
    
};
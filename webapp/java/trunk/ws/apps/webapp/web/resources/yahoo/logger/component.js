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
jmaki.namespace("jmaki.widgets.yahoo.logger");

/**
 * Yahoo jMaki Logger Widget
 * @author: Ahmad M. Zawawi <ahmad.zawawi@gmail.com>
 * @constructor
 * @see http://developer.yahoo.com/yui/logger/
 */
jmaki.widgets.yahoo.logger.Widget = function(wargs) {
    var topic = "/yahoo/logger";
    var self = this;
    //read the widget configuration arguments
    var cfg = {};
    if (typeof wargs.args != 'undefined') {
        //overide topic name if needed
        if (typeof wargs.args.topic != 'undefined') {
            topic = wargs.args.topic;
            jmaki.log("Yahoo logger: widget uses deprecated topic. Use publish instead.");
        }      
        // Width of console
        if (typeof wargs.args.width != 'undefined') {
            cfg.width = wargs.args.width;
        }
        // Height of container
        if (typeof wargs.args.height != 'undefined') {
            cfg.height = wargs.args.height;
        }
        // Position from left edge of viewport
        if (typeof wargs.args.left != 'undefined') {
            cfg.left = wargs.args.left;
        }
        // Position from top edge of viewport
        if (typeof wargs.args.top != 'undefined') {
            cfg.top = wargs.args.top;
        }
        // Position from right edge of viewport
        if (typeof wargs.args.right != 'undefined') {
            cfg.right = wargs.args.right;
        }
        // Position from bottom edge of viewport
        if (typeof wargs.args.bottom != 'undefined') {
            cfg.bottom = wargs.args.bottom;
        }
        // Increase default font size
        if (typeof wargs.args.fontSize != 'undefined') {
            cfg.fontSize = wargs.args.fontSize;
        }        
    }
    
    if (wargs.publish) topic = wargs.publish;       
    
    //create the logger with cfg as config
    this.wrapper = new YAHOO.widget.LogReader(wargs.uuid,cfg); 
   
} //end of widget

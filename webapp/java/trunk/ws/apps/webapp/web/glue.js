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

/*
 * These are some predefined glue listeners that you can
 *  modify to fit your application.
 *
 * This file should not placed in the /resources directory of your application
 * as that directory is for jmaki specific resources.
 */

// turn on the logger
jmaki.debug = false;
// uncomment to show publish/subscribe messages
jmaki.debugGlue = false;

// map topic dojo/fisheye to fisheye handler
jmaki.subscribe("/dojo/fisheye*", function(args) {
    jmaki.log("glue.js : fisheye event");
 });


// map topics ending with  /onSave to the handler
jmaki.subscribe("*onSave", function(args) {
    jmaki.log("glue.js : onSave request from: " + args.id + " value=" + args.value);
});

// map topics ending with  /onSave to the handler
jmaki.subscribe("*onSelect", function(args) {
    jmaki.log("glue.js : onSelect request from: " + args.widgetId);
});

// map topics ending with  /onSave to the handler
jmaki.subscribe("*onClick", function(args) {
    jmaki.log("glue.js : onClick request from: " + args.widgetId);
});


*/
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
jmaki.namespace("jmaki.widgets.blueprints.container");


jmaki.widgets.blueprints.container.Widget = function(wargs) {

    var self = this;    
    var container;
    var url;
    
    if (wargs) {
        init(wargs);
    }

    function init(_val) {
       self.container = document.getElementById(_val.containerid);
       self.url = _val.service;
       
       // Set the reference if specified
       if (typeof _val.setRef != 'undefined' &&
            _val.setRef == 'true' && typeof containerRef != 'undefined') {
            containerRef = this;
        }
       render();
    }
    
    function render() {
       // Set the content in the container
       if (self.url) {
        jmaki.doAjax({url: self.url, callback: function(req) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    self.container.innerHTML = req.responseText;
                }
            }
        }});
       }
       else {
          alert ("Service attribute not defined");
       }
    }
}


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


var container;
var list;
var data;

function renderOutgoingInvitations() {
    list.innerHTML = "";
    if (typeof context == 'undefined')
        context = "";
    for (var _i=0; _i < data.length; _i++) {
        var val = unescape(data[_i].username);
                
        val += "<p>" + unescape(data[_i].fullname) + "</p>";
        
        var li = document.createElement("li");
        li.setAttribute("class", "outgoing_list");
        li.innerHTML = val;
        list.appendChild(li);
    }
}

function sendRequest(url, callback) {
    http.open("GET", url, true);
    http.onreadystatechange = callback;
    http.send(null);
}


function handleRevokeInvite() {
    if (http.readyState == 4) {
        if (http.status == 200) {
            var _in = eval('(' + http.responseText + ')');
            if (_in) {
                data = _in.outgoingInvitations;
            }
            list = document.getElementById("outgoing_list");

            renderOutgoingInvitations();
        }
    }
}


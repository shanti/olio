
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
function renderAttendeeList() {
      list.innerHTML = "";
      if (typeof context == 'undefined')
          context = "";
      var li;
      for (var _i=0; _i < data.attendees.length; _i++) {
          var val = "<a href=\"";
          val += context + "/webapp/person/detail?user_name=";
          val += data.attendees[_i].userName;
          val += "\">";
          val += data.attendees[_i].userName;
          val += "</a>"
          li = document.createElement("li");
          li.innerHTML = val;
          list.appendChild(li);
      }
 }

 function initAttendees() {
      var attendButton;
      if (data.status == "added" || data.status == "attending") {
          attendButton = "<input name=\"unattend\" type=\"button\" value=\"Unattend\" onclick=\"deleteAttendee();\"/>"; 
      }
      else if (data.status == "deleted" || data.status == "not_attending") {
          attendButton = "<input name=\"attend\" type=\"button\" value=\"Attend\" onclick=\"addAttendee();\"/>"; 
      }
      else {
          attendButton = "";
      }

      var elem = document.getElementById("attendees_h2");
      elem.innerHTML = "" + data.attendees.length + "Attendees:"
      elem = document.getElementById("attending_div");
      elem.innerHTML = attendButton;

      list = document.getElementById("attending_list");
      renderAttendeeList();
  }

  function sendAttendeeRequest(url, callback) {
      http.open("GET", url, true);
      http.onreadystatechange = callback;
      http.send(null);
  }
  
  function addAttendee() {
      sendAttendeeRequest (addAttendeeLink, handleAttendee);
  }

  function deleteAttendee() {
      sendAttendeeRequest (deleteAttendeeLink, handleAttendee);
  }

  function handleAttendee() {
      if (http.readyState == 4) {
          if (http.status == 200) {
              var _in = eval('(' + http.responseText + ')');
              if (_in) {
                  data = _in.result;
              }
              initAttendees();
          }
          else {
              alert ("response failed: " + http.status);
          }
      }
  }
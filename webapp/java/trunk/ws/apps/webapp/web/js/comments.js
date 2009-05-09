

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

function renderComments() {
    list.innerHTML = "";
    if (typeof context == 'undefined')
        context = "";
    for (var _i=0; _i < data.length; _i++) {
        var val = unescape(data[_i].username) + "(";
        val += unescape(data[_i].creationTime) + ")";
        val += "<span id=\"rating\"> ";
        var rating = data[_i].rating;
        for (var j=0; j<rating; j++) {
          val += " <img src=\"" + context + "/images/star_on.png\"/>";
        }
        for (var k=rating; k<5; k++) {
          val += " <img src=\"" + context + "/images/star_off.png\"/>";
        }
        val += "<p>" + unescape(data[_i].comments) + "</p>";
        if (typeof loggedInPerson != 'undefined' && loggedInPerson != "" && loggedInPerson == data[_i].username) {  
          val += "<a href=\"#edit\" id=\"commentsRatingToggle\" class='edit_comment' style='color:#999;' onclick=\"return ShowHideLayer('commentsRatingBox');\">Edit</a>";
          val += " or ";
          val += "<a href=\"#delete\" onclick=\"commentWidget.deleteComment(" + data[_i].commentsRatingId + ");\" class='edit_comment' style='color:#999;' >Delete</a>";
        }
        var li = document.createElement("li");
        li.setAttribute("class", "event_comment");
        li.innerHTML = val;
        list.appendChild(li);
    }
}

function sendRequest(url, callback) {
    http.open("GET", url, true);
    http.onreadystatechange = callback;
    http.send(null);
}

function initComments() {
    list = document.getElementById("comment_list");
    renderComments();
}

function updateCommentList(comment) {
    var link = updateCommentLink + comment;
    sendRequest (link, handleComment);
}

function deleteComment(commentId) {
    var link = deleteCommentLink + commentId;
    sendRequest (link, handleComment);
}

function handleComment() {
    if (http.readyState == 4) {
        if (http.status == 200) {
            var _in = eval('(' + http.responseText + ')');
            if (_in) {
                data = _in.commentsratings;
            }
            initComments();
        }
    }
}




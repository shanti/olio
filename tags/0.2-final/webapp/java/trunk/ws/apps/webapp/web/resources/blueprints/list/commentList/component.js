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
jmaki.namespace("jmaki.widgets.blueprints.list.commentList");


jmaki.widgets.blueprints.list.commentList.Widget = function(wargs) {

    var self = this;    
    var container;
    var list;
    var data;
    commentWidget = this;
    
    if (wargs.args) {
        if (wargs.args.count) {
            count = Number(wargs.args.count);
        }
    }

    this.addItem = function(text) {
        var li = document.createElement("li");
        li.setAttribute("class", "event_comment");
        li.innerHTML = text;
        list.appendChild(li);
    }
    
    this.destroy = function() {
        delete list;
        delete container;
    }
    
    this.render = function() {
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
            self.addItem(val);
        }
    }
    
    this.applyTemplate = function(name, value, _t) {
         _t = _t.replace("@{" + name + "}", value);
        return _t;
    }   
    
    if (wargs.service) {
        jmaki.doAjax({url: wargs.service, callback: function(req) {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var _in = eval('(' + req.responseText + ')');
                    if (_in) {
                        data = _in.commentsratings;
                    }
                    init();
                }
            }
        }});
    } else {
        alert ("Use service attribute");
    }

    function init() {
        container = document.getElementById(wargs.uuid);
        list = document.getElementById(wargs.uuid + "_list");
        self.render();
    }
    
    this.updateComment = function(comment) {
        var link = updateCommentLink + comment;
        jmaki.doAjax({url: link, callback:self.handleComment});
    }

    this.deleteComment = function(commentId) {
        var link = deleteCommentLink + commentId;
        jmaki.doAjax({url: link, callback:self.handleComment});
    }
    
    this.handleComment = function(req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var _in = eval('(' + req.responseText + ')');
                if (_in) {
                    data = _in.commentsratings;
                }
                init();
            }
        }
    }
}

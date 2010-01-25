
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


/* =============================================================================
   Blueprints Common JavaScript Functions
   ========================================================================== */  



/**
 * Alias for the top-level global object in the host environment
 * (the "window" object in a browser).
 */
var bpui_global = this; // typeof window == 'undefined' ? this : window;


/**
 * Return true if the specified name is undefined in the specified scope.
 *
 * @param name Variable name to be checked
 * @param scope Object within which to check for this name (if not specified,
 *  "bpui_global" is checked)
 */
function bpui_undefined(name, scope) {
  if (!scope) {
    scope = bpui_global;
  }
  return typeof scope[name] == "undefined";
}


/**
 * The root variable of nearly all of our public API.
 */
var bpui;
if (bpui_undefined("bpui")) {
  bpui = { };
}


/**
 * The version number of this script.
 */
bpui.version = {
  major: 0,
  minor: 1,
  patch: 0,
  flag: "",
  toString: function() {
    with (bpui.version) {
      return major + "." + minor + "." + patch + flag;
    }
  }
}


/**
 * Create a <script> element to load the specified JavaScript
 * resource dynamically.
 *
 * WARNING - this method can ONLY be successfully called from
 * top level JavaScript code.  Calling it from within a method
 * will not be effective.
 *
 * @param url URL of the JavaScript resource to be loaded
 */
bpui.load = function(url) {
  document.write('<script type="text/javascript" src="' + url + '"></script>');
}

/**
 * function that returns the context root of the page's URL
 */
bpui.getContextRoot = function() {
    var urlArray=window.location.toString().split("/", 4);
    return "/" + urlArray[3];
}

/**
 * variable that holds the context root of the page's URL
 */
bpui.contextRoot = bpui.getContextRoot();

/**
 * helper function to get a URI that has the context root.
 */
bpui.attachRootToPath = function(baseString) {
    return bpui.contextRoot + "/" +  baseString;
} 


/**
 * set value for the timezone select list
 */
bpui.setTimezone = function (defaultTimezone, newTimezone) {    
    // if no default and no new timezone then return
    if(defaultTimezone == "" && newTimezone == "") {
        // no timezone to set
        return;
    }
    
    // set defaultTimezone
    var timezone=defaultTimezone;
    if(newTimezone != "") {
        // set to newTimezone
        timezone=newTimezone;
    }

    // loop through timezones and select the right one
    timezones=document.getElementById("timezone");
    for(ii=0; ii < timezones.length; ii++) {
        if(timezones[ii].text == timezone) {
            // found timezone
            timezones[ii].selected=true;
        }
    }
}

bpui.getWindowHeight=function() {
    var height=0;
	if(typeof(window.innerWidth) == 'number' ) {
	    height=window.innerHeight;
	} else if(document.documentElement && document.documentElement.clientHeight) {
	    height=document.documentElement.clientHeight;
	} else if(document.body && document.body.clientHeight) {
	    height=document.body.clientHeight;
	}
    return height;
}    


bpui.getWindowWidth=function() {
    var width=0;
	if(typeof(window.innerWidth ) == 'number') {
	    width=window.innerWidth;
	} else if(document.documentElement && document.documentElement.clientWidth) {
	    width=document.documentElement.clientWidth;
	} else if(document.body && document.body.clientWidth) {
	    width=document.body.clientWidth;
	}
    return width;
}    


bpui.ajaxBindError=function(type, errObj) {
    // can't use the error page, because unless and exception in the internal servlet container
    // nullpointer exceptions will be thrown
    //window.location="./systemerror.jsp?message=" + errObj.message;

    alert("An Exception has been encountered on the server side during an Ajax request.  Please see the server logs for more information " + errObj.message);
}


bpui.debugProperties=function(namex) {
    var listx="";
    var ob=namex;
    for(xx in ob) {
        listx += xx + " = " + ob[xx] + "<br/>"
    }
    //document.write(listx);
    alert(listx);
}




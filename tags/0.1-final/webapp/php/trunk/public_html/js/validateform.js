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
                function checkEventFields() {
                        var title = document.addEvent.title.value.length;
                        var street1 = document.addEvent.street1.value.length;
                        var city    = document.addEvent.city.value.length;
                        var state   = document.addEvent.state.value.length;
                        var zip     = document.addEvent.zip.value.length;
                        var tags    = document.addEvent.tags.value.length;
                        var returnval;

                        if (title > 0 && street1 > 0 && city > 0 && state > 0 && zip > 0 && tags > 0  ){
                            returnval = true;
                        }else if (title == 0) { alert("Please enter Event Title");returnval=false; }
                        else if (street1 == 0) { alert("please enter event address "); returnval=false; }
                        else if (city == 0) { alert("please enter event city name"); returnval=false; }
                        else if (state == 0) { alert("please enter event state name"); returnval=false; }
                        else if (zip == 0) { alert("please enter event zip code"); returnval=false; }
                        else if (tags == 0) { alert("please enter a tag"); returnval=false; }		
                        return returnval;
                }
                
                function checkUserFields() {
                        var loginid = document.addperson.add_user_name.value.length;
                        var pwd     = document.addperson.psword.value.length;
                        var pwdx    = document.addperson.passwordx.value.length;
                        var fn      = document.addperson.first_name.value.length;
                        var ln      = document.addperson.last_name.value.length;
                        var email   = document.addperson.email.value.length;
                        var street1 = document.addperson.street1.value.length;
                        var city    = document.addperson.city.value.length;
                        var state   = document.addperson.state.value.length;
                        var zip     = document.addperson.zip.value.length;
                        var returnval;

                        if (loginid > 0 && pwd > 0 && pwdx > 0 && fn > 0 && ln > 0 && email > 0 && street1 > 0 && city > 0 && state > 0 && zip > 0 ){
                                        returnval = true;
                        }
                        else if (loginid == 0) { alert("Please enter login id");returnval=false; }
                        else if (pwd == 0) { alert("please enter password"); returnval=false; }
                        else if (pwdx == 0) { alert("please re-enter password"); returnval=false; }
                        else if (fn == 0) { alert("please enter your first name"); returnval=false; }
                        else if (ln == 0) { alert("please enter your last name"); returnval=false; }
                        else if (email == 0) { alert("please enter your email address"); returnval=false; }
                        else if (street1 == 0) { alert("please enter street address "); returnval=false; }
                        else if (city == 0) { alert("please enter city name"); returnval=false; }
                        else if (state == 0) { alert("please enter state name"); returnval=false; }
                        else if (zip == 0) { alert("please enter zip code"); returnval=false; }

                        return returnval;
                }
		
                function isValidEmail() {
                        var str = document.getElementById('email').value;
                        var emailmsg = "<span style='color:red'><small> Please enter email id in the format example@email.com.</small></span></br> ";
                        if (!( (str.indexOf(".") > 2) && (str.indexOf("@") > 0) )) {
                                document.getElementById("isvalidemail").innerHTML = emailmsg;
                        }else{
                                document.getElementById("isvalidemail").innerHTML = "";
                        }
                }
		function isNumeric(sText) {
                	var ValidChars = "0123456789";
                	var IsNumber=true;
                	var Char;
                	for (i = 0; i < sText.length && IsNumber == true; i++) {
                        	Char = sText.charAt(i);
                        	if (ValidChars.indexOf(Char) == -1) {
                                	IsNumber = false;
                        	}
                	}
                	return IsNumber;
        	}
        	function isTeleNumeric(sText) {
                	var ValidChars = "0123456789-";
                	var IsNumber=true;
                	var Char;
                	for (i = 0; i < sText.length && IsNumber == true; i++) {
                        	Char = sText.charAt(i);
                        	if (ValidChars.indexOf(Char) == -1) {
                                	IsNumber = false;
                        	}
                	}
                	return IsNumber;
        	}
        	function isValidZip() {
                	var zipcode = document.getElementById('zip').value;
                	var zipmsg = "<span style='color:red'><small> Please enter a valid 5 digit numeric zipcode.</small></span></br> ";
                	if (!isNumeric(zipcode) ) {
                        	document.getElementById("isvalidzip").innerHTML = zipmsg;
                	}else{
                        	document.getElementById("isvalidzip").innerHTML = "";
                	}
        	}
        	function isValidTelephone() {
                	var phone = document.getElementById('telephone').value;
                	var phonemsg = "<span style='color:red'><small> Please enter phone number with digits and '-' in the format 123-123-1234. </small></span></br> ";
                	if (!isTeleNumeric(phone) ) {
                        	document.getElementById("isvalidtelephone").innerHTML = phonemsg;
                	}else{
                        	document.getElementById("isvalidtelephone").innerHTML = "";
                	}
        	}
                function checkPwdMatch() {
                        var pwdValue = document.getElementById('psword').value;
                        var pwdxValue = document.getElementById('passwordx').value;
                        var pwdmsg = "<span style='color:red'><small> Password does not match. Please try again!</small></span></br> ";
                        if (!(pwdValue==pwdxValue)) {
                                //alert("Password does not match. Please try again!");
                                document.getElementById("checkpwdmatch").innerHTML =  pwdmsg;
                        }else{
                                document.getElementById("checkpwdmatch").innerHTML =  "";
                        }
                }
		var citystate = "requestCityState.php?param=";
        	function handleHttpResponse() {
                	if (http.readyState == 4) {
                        	// Split the comma delimited response into an array
                        	results = http.responseText.split(",");
                        	document.getElementById('city').value = results[0];
                        	document.getElementById('state').value = results[1];
                	}
        	}
        	function fillCityState() {
                	var zipValue = document.getElementById("zip").value;
                	http.open("GET", citystate + escape(zipValue), true);
                	http.onreadystatechange = handleHttpResponse;
                	http.send(null);
        	}
                
                var checkuser = "checkUser.php?user=";
                function handleUserCheck() {
                        if (http.readyState == 4) {
                                result = http.responseText;
                                document.getElementById("usercheck").innerHTML=result;
                        }
                }
                function checkUser() {
                        var userValue = document.getElementById('add_user_name').value;
                        http.open("GET", checkuser + escape(userValue), true);
                        http.onreadystatechange = handleUserCheck;
                        http.send(null);
                }

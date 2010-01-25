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
package org.apache.olio.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;

/**
 * A map that helps find
 * @author Inderjeet Singh
 */
public class ActionMap {
    
    private static class ActionUnit {
        String actionUri;
        Action action;
        public ActionUnit(String uri, Action action) {
            this.actionUri = uri;
            this.action = action;
        }
    }
    private Collection<ActionUnit> table = new ArrayList<ActionUnit>();
    public ActionMap() {
    }

    public Action get(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        int index = requestURI.indexOf(contextPath) + contextPath.length();
        String actionKey = requestURI.substring(index);
        return get(actionKey);
    }
    
    public Action get(String uri) {
        ActionUnit au = getActionUnit(uri);
        return au == null ? null : au.action;
    }
    
    private ActionUnit getActionUnit(String uri) {
        for (ActionUnit au : table) {
            if (uri.startsWith(au.actionUri)) {
                return au;
            }
        }
        return null;
    }
    public void put(String uri, Action action) {
        ActionUnit au = getActionUnit(uri);
        if (au == null) {
            table.add(new ActionUnit(uri, action));
        } else {
            au.action = action;
        }
    }
}

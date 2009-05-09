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

import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.model.SocialEvent;
import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.util.WebappUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.olio.webapp.controller.WebConstants.*;

/**
 *
 * @author Binu John
 */
public class TagAction implements Action {
    
    private ServletContext context;
    
    /** Creates a new instance of EventAction */
    public TagAction(ServletContext con) {
        this.context = con;
    }

    public String process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        ModelFacade mf= (ModelFacade) context.getAttribute(MF_KEY);
        if (path.equals("/display")) {
            String tag = request.getParameter("tag");
            if (tag == null)
                return null;
            int index = WebappUtil.getIntProperty(request.getParameter("index"));
            List<SocialEvent> list = mf.getSocialEventsByTag(tag);
            if (list != null) {
                List<SocialEvent> slist = WebappUtil.getPagedList(list, index);
                request.setAttribute("numPages", WebappUtil.getNumPages(list));
                request.setAttribute("pageUrl", request.getContextPath() + "/tag/display?tag="+tag);
                request.setAttribute("itemList", slist);
                request.setAttribute("index", index);
            }
            return "/site.jsp?page=taggedEvents.jsp&tag=" + tag + "&index=" + index;
        }
        return null;
    }

}

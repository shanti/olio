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
/* 
 * GeocoderServlet.java
 *
 * This is the main servlet that handles the geocoder request.
 */

package org.apache.olio.geocoder;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jasper.tagplugins.jstl.core.Out;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Prashant Srinivasan
 * @version
 */
public class GeocoderServlet extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        GeocoderBean geoBean = getGeocoderBean(request);
        PrintWriter out = response.getWriter();
//        if (!aGoodRequest(geoBean)) {
//            response.setStatus(400);
//            out.println("<Error xmlns=\"urn:yahoo:api\"> " +
//                    "The following errors were detected:<Message>" +
//                    "Input Parameters passed to the service did not" +
//                    "match as was expected</Message></Error>");
//            return;
//        }
        Geocoder geocoder = new Geocoder();
        LatLngDegrees point = geocoder.returnCodes(geoBean);
        
        
        response.setContentType("text/html;charset=UTF-8");
        
        //out.println("It works!<HR/>");
        Document doc = buildDom(geoBean);
        //out.println(printContents(doc));
        System.out.println("===going to printDOMTree===");
        displayDOM(doc, out);
        System.out.println("====finished printDOMTree===");
        //out.println("<hr/>DONE!");
        out.close();
    }
    
    public GeocoderBean getGeocoderBean(HttpServletRequest request) {
        GeocoderBean geoBean = new GeocoderBean();
        geoBean.setAppid(request.getParameter("appid"));
        geoBean.setCity(request.getParameter("city"));
        geoBean.setLocation(request.getParameter("location"));
        geoBean.setOutput(request.getParameter("output"));
        geoBean.setState(request.getParameter("state"));
        geoBean.setState(request.getParameter("state"));
        geoBean.setStreet(request.getParameter("street"));
        geoBean.setZip(request.getParameter("zip"));
        
        return geoBean;
    }
    
    public String getXmlResponseStream(LatLngDegrees latLng, GeocoderBean geoBean) {
        return null;
    }
    
    public Document buildDom(GeocoderBean geoBean) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            
            
            
            Element root =
                    (Element) document.createElementNS("http://www.w3.org/2001/XMLSchema-instance","ResultSet");
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xmlns", "urn:yahoo:maps");
            root.setAttribute("xsi:schemaLocation", "urn:yahoo:maps http://api.local.yahoo.com/MapsService/V1/GeocodeResponse.xsd");
            document.appendChild(root);
            Element result = (Element) document.createElement("Result");
            result.setAttribute("precision", "address");
            root.appendChild(result);
            
            Element latitude = (Element) document.createElement("Latitude");
            latitude.appendChild(document.createTextNode("33.0000"));
            result.appendChild(latitude);
            
            Element longitude = (Element) document.createElement("Longitude");
            longitude.appendChild(document.createTextNode("-177.0000"));
            result.appendChild(longitude);
            
            Element address = (Element) document.createElement("Address");
            //address.appendChild(document.createTextNode("1600 Amphitheater Parkway"));
            address.appendChild(document.createTextNode(geoBean.getStreet()));
            result.appendChild(address);
            
            Element city = (Element) document.createElement("City");
            //city.appendChild(document.createTextNode("Mountain View"));
            city.appendChild(document.createTextNode(geoBean.getCity()));
            result.appendChild(city);
            
            Element state = (Element) document.createElement("State");
            //state.appendChild(document.createTextNode("CA"));
            state.appendChild(document.createTextNode(geoBean.getState()));
            result.appendChild(state);
            
            Element zip = (Element) document.createElement("zip");
            //zip.appendChild(document.createTextNode("94025"));
            zip.appendChild(document.createTextNode(geoBean.getZip()));
            result.appendChild(zip);
            
            Element country = (Element) document.createElement("Country");
            country.appendChild(document.createTextNode("USA"));
            result.appendChild(country);
            
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        }
        return document;
    }
    
    public String printContents(Node element) {
        String stream = "";
        //print start
        //if child nodes call printContents on the child nodes.
        //else print contents
        //print end
        stream += "<" + element.getNodeValue() + ">";
        int numChildren = element.getChildNodes().getLength();
        if ( numChildren !=0 ) {
            //call printContents on the child nodes one-by-one
            for (int j = 0; j < numChildren; j ++) {
                stream += printContents(element.getChildNodes().item(j));
            }
        }
        stream += "</" + element.getNodeName() + ">";
        return stream;
    }
    
    
    public void displayDOM(Node node, PrintWriter out) {
        
        int type = node.getNodeType();
        switch (type) {
            // print the document element
            case Node.DOCUMENT_NODE:
            {
                out.println("<?xml version=\"1.0\" ?>");
                displayDOM(((Document)node).getDocumentElement(), out);
                break;
            }
            
            // print element with attributes
            case Node.ELEMENT_NODE:
            {
                out.print("<");
                out.print(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    out.print(" " + attr.getNodeName() +
                            "=\"" + attr.getNodeValue() +
                            "\"");
                }
                out.println(">");
                
                NodeList children = node.getChildNodes();
                if (children != null) {
                    int len = children.getLength();
                    for (int i = 0; i < len; i++)
                        displayDOM(children.item(i), out);
                }
                
                break;
            }
            
            // handle entity reference nodes
            case Node.ENTITY_REFERENCE_NODE:
            {
                out.print("&");
                out.print(node.getNodeName());
                out.print(";");
                break;
            }
            
            // print cdata sections
            case Node.CDATA_SECTION_NODE:
            {
                out.print("<![CDATA[");
                out.print(node.getNodeValue());
                out.print("]]>");
                break;
            }
            
            // print text
            case Node.TEXT_NODE:
            {
                out.print(node.getNodeValue());
                break;
            }
            
            // print processing instruction
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                out.print("<?");
                out.print(node.getNodeName());
                String data = node.getNodeValue();
                {
                    out.print(" ");
                    out.print(data);
                }
                out.print("?>");
                break;
            }
        }
        
        if (type == Node.ELEMENT_NODE) {
            out.println();
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
        }
    }
    
    public boolean aGoodRequest(GeocoderBean geoBean) {
        if ((geoBean.getAppid() == null) || (geoBean.getCity() == null)
        || (geoBean.getLocation() == null) || (geoBean.getStreet() == null)
        || (geoBean.getState() == null) || (geoBean.getZip() == null)) {
            return false;
        }
        return true;
    }
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
    
}

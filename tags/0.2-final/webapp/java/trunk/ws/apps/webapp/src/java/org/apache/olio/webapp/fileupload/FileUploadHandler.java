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
 * FileUpload.java
 *
 * Created on March 7, 2006, 1:49 PM
 *
 * 
 * @author Mark Basler
 * @author Binu John
 */
package org.apache.olio.webapp.fileupload;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.olio.webapp.controller.WebConstants;
import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.util.WebappUtil;
import org.apache.olio.webapp.util.fs.FileSystem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class uses Apache Commons FileUpload to parse the multi-part mime that is sent via the HttpServletRequest InputStream.
 * This class also relies on Faces to populate the fileUploadStatus in this Backing Bean on each request.
 *
 * @author basler
 * @author Binu John
 */
public class FileUploadHandler {

    private static Logger logger = Logger.getLogger(FileUploadHandler.class.getName());
    private FileUploadStatus fileUploadStatus = null;
    private FileItemIterator itemIter;
    private FileItemStream item;
    private Hashtable<String, String> requestParams;

    /**
     * Default location of upload directory is /domain_dir/lib/upload, unless the Sun Appserver system property exits, then it will
     * use the domain's lib/upload directory instead
     */

    /** Creates a new instance of FileUpload */
    public FileUploadHandler() {
    }

    /**
     * Write status of current/last upload to the response stream.  This method assumes that the fileUploadStatus will
     * be set using the managed property functionality of a backing bean.  This relationship is specified in the 
     * faces-config.xml file.  This method is accessed through the Shale-remoting dynamic framework.
     */
    public static void handleFileStatus(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/xml;charset=UTF-8");

        // use managed bean session object to allow the monitoring of the fileupload based on componentName
        //FileUploadStatus status=getFileUploadStatus();     
        HttpSession session = request.getSession();
        FileUploadStatus status = (FileUploadStatus) session.getAttribute(FileUploadUtil.FILE_UPLOAD_STATUS);
        try {
            // write out response based on fileupload status.
            PrintWriter writer = response.getWriter();
            writer.println("<response>");
            if (status != null) {
                writer.println("<message>");
                writer.println(status.getMessage());
                writer.println("</message>");

                writer.println("<status>");
                writer.println(status.getStatus());
                writer.println("</status>");

                writer.println("<current_item>");
                writer.println(status.getCurrentItem());
                writer.println("</current_item>");

                writer.println("<percent_complete>");
                writer.println(String.valueOf(status.getPercentageComplete()));
                writer.println("</percent_complete>");
            } else {
                // no status in session yet
                writer.println("<message>");
                writer.println("No Status");
                writer.println("</message>");

                writer.println("<status>");
                writer.println("No Status");
                writer.println("</status>");

                writer.println("<current_item>");
                writer.println("None");
                writer.println("</current_item>");

                writer.println("<percent_complete>");
                writer.println("0");
                writer.println("</percent_complete>");
            }
            writer.println("</response>");
            writer.flush();

        } catch (IOException iox) {
            //logger.severe("FileUploadPhaseListener error writting AJAX response : " + iox);
            logger.log(Level.SEVERE, "FileUploadHandler Error writing AJAX response: ", iox);
        }
        logger.log(Level.FINE, "STATUS RETURN = " + status);
    }

    /**
     * Write status of current/last upload to the response stream.  This method assumes that the fileUploadStatus will
     * be set using the managed property functionality of a backing bean.  This relationship is specified in the 
     * faces-config.xml file.  This method is accessed through the Shale-remoting dynamic framework.
     */
    public static void handleFileUploadFinal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.log(Level.FINE, "** In Final upload for STATUS RETURN = ");

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/xml;charset=UTF-8");

        // use managed bean session object to allow the monitoring of the fileupload based on componentName
        //FileUploadStatus status=getFileUploadStatus();
        HttpSession session = request.getSession();
        FileUploadStatus status = (FileUploadStatus) session.getAttribute(FileUploadUtil.FILE_UPLOAD_STATUS);

        writeUploadResponse(response.getWriter(), status);
    }

    /**
     * Handles the initial fields up to the first upload field. This will
     * allow creating the database entry and obtaining the auto-generated
     * ids.
     * @return A hash table with the initial field values
     */
    public Hashtable<String, String> getInitialParams(HttpServletRequest request, HttpServletResponse response) {

        // print out header for
        Enumeration enumx = request.getHeaderNames();
        String key = "";
        String listx = "";
        while (enumx.hasMoreElements()) {
            key = (String) enumx.nextElement();
            listx += "\n" + key + ":" + request.getHeader(key);
        }
        logger.fine("Incoming Header Item:" + listx);
        // enable progress bar (this managed bean that is in the session could be comp specific, but I can't create the component specific
        // session object until I have the components name.  For now use static key through backing bean).
        // Use session to allow the monitoring of the fileupload based
        HttpSession session = request.getSession();

        FileUploadStatus status = new FileUploadStatus();
        session.setAttribute(FileUploadUtil.FILE_UPLOAD_STATUS, status);
        setFileUploadStatus(status);

        // Create hashtable to hold uploaded data so it can be used by custom post extension
        Hashtable<String, String> htUpload = new Hashtable<String, String>();
        // set to set hashtable for final retrieval
        status.setUploadItems(htUpload);

        // get size of upload and set status
        long totalSizeOfUpload = request.getContentLength();
        status.setTotalUploadSize(totalSizeOfUpload);

        // Check that we have a proper file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {

            // Streaming API typically provide better performance for file uploads.
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            try {
                // Now we should have the componentsName and upload directory to setup remaining upload of file items
                String compName = htUpload.get(FileUploadUtil.COMPONENT_NAME);
                status.setName(compName);

                // Parse the request and return list of "FileItem" whle updating status
                FileItemIterator iter = upload.getItemIterator(request);

                status.setReadingComplete();

                while (iter.hasNext()) {
                    item = iter.next();
                    if (item.isFormField()) {
                        // handle a form item being uploaded
                        String itemName = item.getFieldName();

                        // process form(non-file) item200002
                        int size = formItemFound(item, htUpload);
                        updateSessionStatus(itemName, size);

                        logger.fine("Form field item:" + itemName);

                    } else {
                        // At the first find of an uploaded file, stop.
                        // We need to insert our record first in order
                        // to find the id.
                        break;
                    }
                }
                itemIter = iter;
            } catch (Exception e) {
                status.setUploadError("FileUpload didn't complete successfully.  Exception received:" + e.toString());
                logger.log(Level.SEVERE, "file.upload.exception", e);
            }
        }
        fileUploadStatus = status;
        requestParams = htUpload;
        return htUpload;
    }



    /**
     * Invoke the fileupload process that reads the input from the HttpServletRequest inputStream.  Since this 
     * component accesses the HttpServletRequest directly, this component currently may not work from within a portlet.  
     * This method assumes that the fileUploadStatus will be set using the managed property functionality of a backing bean.  
     * This relationship is specified in the faces-config.xml file. This method is accessed through the Shale-remoting dynamic framework.  
     */
    public Hashtable<String, String> handleFileUpload(String id, HttpServletRequest request, HttpServletResponse response) {
        File fileDir = null;
        String compName = requestParams.get(FileUploadUtil.COMPONENT_NAME);
        String serverLocationDir = requestParams.get(compName + "_" + FileUploadUtil.SERVER_LOCATION_DIR);
        logger.finest("\n*** locationDir=" + serverLocationDir);
        if (serverLocationDir == null) {
                serverLocationDir = WebappConstants.WEBAPP_IMAGE_DIRECTORY;
                fileDir = new File(serverLocationDir);
                fileDir.mkdirs();            
        } else {
            // make sure directory exists, don't create automatically for security reasons
            fileDir = new File(serverLocationDir);
        }
        logger.finest("serverLocationDir = " + serverLocationDir);
        // make sure dir exists and is writable
        if (fileDir == null || !fileDir.isDirectory() || !fileDir.canWrite()) {
            // error, directory doesn't exist or isn't writable
            fileUploadStatus.setUploadError("Directory \"" + fileDir.toString() + "\" doesn't exist!");
            logger.log(Level.SEVERE, "directory.inaccessable:", fileDir.toString());
            return null;
        }
        try {
            for (;;) {
                if (item.isFormField()) {
                    // handle a form item being uploaded
                    String itemName = item.getFieldName();

                    // process form(non-file) item
                    int size = formItemFound(item, requestParams);
                    updateSessionStatus(itemName, size);

                    logger.fine("Form field item:" + itemName);

                } else {
                    fileItemFound(item, requestParams, serverLocationDir, id);
                }
                if (itemIter.hasNext())
                    item = itemIter.next();
                else
                    break;
            }

            // put upload to 100% to handle rounding errors in status calc
            fileUploadStatus.setUploadComplete();
            logger.fine("Final session status - " + fileUploadStatus);

        } catch (Exception e) {
            fileUploadStatus.setUploadError("FileUpload didn't complete successfully.  Exception received:" + e.toString());
            logger.log(Level.SEVERE, "file.upload.exception:", e);
        }
        return requestParams;
    }


    /**
     * Handle upload of a standard form item
     *
     * @param item The Commons Fileupload item being loaded
     * @param htUpload The Status Hashtable that contains the items that have been uploaded for post-processing use
     */
    protected int formItemFound(FileItemStream item, Hashtable<String, String> htUpload) {
        int size = 0;
        InputStream is = null;
        try {
            String key = item.getFieldName();
            StringBuilder strb = new StringBuilder();
            byte[] buf = new byte[128];
            is = item.openStream();

            // Read from the stream
            int i = 0;
            while ((i = is.read(buf)) != -1) {
                strb.append(new String(buf, 0, i));
                size += i;
            }
            String value = strb.toString();

            // put in Hashtable for later access
            logger.fine("Inserting form item in map " + key + " = " + value);
            htUpload.put(key, value);
        } catch (IOException ex) {
            Logger.getLogger(FileUploadHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUploadHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return size;
    }

    /**
     * Handle upload of a "file" form item
     *
     * @param item The Commons Fileupload item being loaded
     * @param htUpload The Status Hashtable that contains the items that have been uploaded for post-processing use
     * @param serverLocationDir The Status Hashtable that contains the items that have been uploaded for post-processing use
     */
    protected void fileItemFound(FileItemStream item, Hashtable<String, String> htUpload,
            String serverLocationDir, String id) throws Exception {

        String fileLocation = null;
        String fileName = item.getName();

        if (fileName != null && !fileName.equals("")) {
            // see if IE on windows which send full path with item, but just filename
            // check for both separators because client OS my be different that server OS
            // check for unix separator
            logger.fine("Have item - " + fileName + " - in name= " + item.getFieldName());

            int iPos = fileName.lastIndexOf("/");
            if (iPos > -1) {
                fileName = fileName.substring(iPos + 1);
                logger.fine("Have full path, need to truncate \n" + item.getName() + "\n" + fileName);
            }
            // check for windows separator
            iPos = fileName.lastIndexOf("\\");
            if (iPos > -1) {
                fileName = fileName.substring(iPos + 1);
                logger.fine("Have full path, need to truncate \n" + item.getName() + "\n" + fileName);
            }
            fileLocation = serverLocationDir + File.separator + fileName;
            String thumbnailName;
            String ext = WebappUtil.getFileExtension(fileName);

            if (item.getFieldName().equals(WebConstants.UPLOAD_PERSON_IMAGE_PARAM)) {
                fileName = "P" + id;
                thumbnailName = fileName + 'T' + ext;
                // Append the extension
                fileName += ext;
                writeWithThumbnail(item, fileName, thumbnailName);
                htUpload.put(WebConstants.UPLOAD_PERSON_IMAGE_PARAM, fileName);
                htUpload.put(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM, thumbnailName);
            } else if (item.getFieldName().equals(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM)) {
                fileName = "P" + id + "T" + ext;
                write(item, fileName);
                htUpload.put(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM, fileName);
            } else if (item.getFieldName().equals(WebConstants.UPLOAD_EVENT_IMAGE_PARAM)) {
                //String submitter = htUpload.get(WebConstants.SUBMITTER_USER_NAME_PARAM);
                fileName = "E" + id;
                thumbnailName = fileName + 'T' + ext;
                fileName += ext;
                writeWithThumbnail(item, fileName, thumbnailName);
                htUpload.put(WebConstants.UPLOAD_EVENT_IMAGE_PARAM, fileName);
                htUpload.put(WebConstants.UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM, thumbnailName);
            } else if (item.getFieldName().equals("eventThumbnail")) {
                fileName = "E" + id + 'T' + ext;
                write(item, fileName);
                htUpload.put(WebConstants.UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM, fileName);
            } else if (item.getFieldName().equals("upload_event_literature")) {
                fileName = "E" + id + 'L' + ext;
                fileLocation = serverLocationDir + "/" + fileName;
                write(item, fileLocation);
                htUpload.put(WebConstants.UPLOAD_LITERATURE_PARAM, fileName);
            } else {
                logger.warning("******** what is this?  item is " + item.getFieldName());
            }
            logger.fine("Writing item to " + fileLocation);
            // store image location in Hashtable for later access

            String fieldName = item.getFieldName();
            logger.fine("Inserting form item in map " + fieldName + " = " + fileName);

            htUpload.put(fieldName, fileName);
            // insert location
            String key = fieldName + FileUploadUtil.FILE_LOCATION_KEY;
            logger.fine("Inserting form item in map " + key + " = " + fileLocation);

            htUpload.put(key, fileLocation);
        }
    }

    /**
     * Write response of fileupload.  This method can also call a DeferredMethod that is entered by using the 
     * "postProcessingMethod" attribute of the FileUploadTag.  This post processing method must have a signature of 
     * "javax.faces.context.FacesContext, java.util.Hashtable, com.sun.javaee.blueprints.components.ui.fileupload.FileUploadStatus"
     * If the post processing method wants to provide a custom response, the method can call the "enableCustomReturn" method on the
     * FileUploadStatus object so that the default fileupload response will not be sent.
     *
     * @param writer The writer to write the output
     * @param status The Status object that contains the items that have been uploaded for post-processing use
     */
    public static void writeUploadResponse(PrintWriter writer, FileUploadStatus status) {
        Hashtable<String, String> htUpload = status.getUploadItems();
        try {
            logger.log(Level.FINE, "\n***In writeUploadResponse");
            //  make sure session data exists

            if (htUpload != null) {
                String compName = htUpload.get(FileUploadUtil.COMPONENT_NAME);
                //FileUploadStatus status=getFileUploadStatus();
                // check to see if user has custom post-processing method
                String postProcessingMethod = htUpload.get(compName + "_" + FileUploadUtil.POST_PROCESSING_METHOD);
                logger.log(Level.FINE, "\n*** postProcessingMethod = " + postProcessingMethod);

                if (postProcessingMethod != null) {
                    // call post-processing method
                    // extension mechanism
                }

                // see if custom response set
                if (!status.isCustomReturnEnabled()) {
                    // return default response
                    logger.log(Level.FINE, "FINAL STATUS - " + status);

                    writer.println("<response>");
                    writer.println("<message>");
                    writer.println(status.getMessage());
                    writer.println("</message>");

                    writer.println("<status>");
                    writer.println(status.getStatus());
                    writer.println("</status>");

                    writer.println("<duration>");
                    writer.println(String.valueOf(status.getUploadTime()));
                    writer.println("</duration>");

                    writer.println("<duration_string>");
                    writer.println(status.getUploadTimeString());
                    writer.println("</duration_string>");

                    writer.println("<start_date>");
                    writer.println(status.getStartUploadDate().toString());
                    writer.println("</start_date>");

                    writer.println("<end_date>");
                    writer.println(status.getEndUploadDate().toString());
                    writer.println("</end_date>");

                    writer.println("<upload_size>");
                    writer.println(String.valueOf(status.getTotalUploadSize()));
                    writer.println("</upload_size>");
                    writer.println("</response>");
                    writer.flush();
                }
            } else {
                // error no data in session
                writer.println("<response>");
                writer.println("<message>");
                writer.println("Error, no session data available!");
                writer.println("</message>");
                writer.println("<status>");
                writer.println(FileUploadStatus.UPLOAD_ERROR);
                writer.println("</status>");
                writer.println("</response>");
                writer.flush();
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "FileUploadPhaseListener error writting AJAX response : " + e);
            logger.log(Level.SEVERE, "response.exeception", e);
        }
    }

    /**
     * write out file item to a file using the Apache Commons FileUpload FileItem.write method as a guide,
     * so the output could be monitored
     *
     * @param item The Commons Fileupload item being loaded
     * @param fileName File name to write uploaded data.
     * @throws Exception Exceptions propagated from Apache Commons Fileupload classes
     */
    public void write(FileItemStream item, String fileName) throws Exception {
        // use name for update of session
        String itemName = item.getName();

        ServiceLocator locator = ServiceLocator.getInstance();

        FileSystem fs = locator.getFileSystem();

        logger.fine("Getting fileItem from memory - " + itemName);

        OutputStream fout = fs.create(fileName);

        // It would have been more efficient to use NIO if we are writing to 
        // the local filesystem. However, since we need to support DFS, 
        // a simple solution is provided. 
        // TO DO: Optimize write if required.

        try {
            byte[] buf = new byte[8192];
            int count, size = 0;
            InputStream is = item.openStream();
            while ((count = is.read(buf)) != -1) {
                fout.write(buf, 0, count);
                size += count;
            }
            updateSessionStatus("", size);
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }

    /**
     * 
     * @param item FileItemStream from the file upload handler
     * @param imageName name to save the image.
     * @param thumbnailName name to save the thumbnail. Extension is appended.
     * @throws java.lang.Exception
     */
    public void writeWithThumbnail(FileItemStream item,
            String imageName, String thumbnailName) throws Exception {
        // use name for update of session
        String itemName = item.getName();

        ServiceLocator locator = ServiceLocator.getInstance();

        FileSystem fs = locator.getFileSystem();

        logger.fine("Getting fileItem from memory - " + itemName);

        OutputStream imgOut = null;
        OutputStream thumbOut = null;
        InputStream is = null;
        try {
            imgOut = fs.create(imageName);
            thumbOut = fs.create(thumbnailName);
        // It would have been more efficient to use NIO if we are writing to
        // the local filesystem. However, since we need to support DFS,
        // a simple solution is provided.
        // TO DO: Optimize write if required.

            is = item.openStream();
            FileUploadStatus status = getFileUploadStatus();
            status.setCurrentItem(itemName);

            WebappUtil.saveImageWithThumbnail(is, imgOut, thumbOut, status);
        } finally {
            if (imgOut != null)
                try {
                    imgOut.close();
                } catch (IOException e) {
                }
            if (thumbOut != null)
                try {
                    thumbOut.close();
                } catch (IOException e) {
                }
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                }
        }
    }

    public void updateSessionStatus(String itemName, long incrementAmount) {
        FileUploadStatus status = getFileUploadStatus();
        status.setCurrentItem(itemName);
        status.incrementCurrentSizeWritten(incrementAmount);
    }

    public void setFileUploadStatus(FileUploadStatus status) {
        fileUploadStatus = status;
    }

    public FileUploadStatus getFileUploadStatus() {
        return fileUploadStatus;
    }
}

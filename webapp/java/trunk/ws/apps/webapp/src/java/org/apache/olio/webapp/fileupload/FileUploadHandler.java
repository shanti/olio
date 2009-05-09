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

import org.apache.olio.webapp.util.fs.FileSystem;
import org.apache.olio.webapp.util.ServiceLocator;
import org.apache.olio.webapp.util.WebappConstants;
import org.apache.olio.webapp.controller.WebConstants;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.security.SecurityHandler;
import org.apache.olio.webapp.util.WebappUtil;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This class uses Apache Commons FileUpload to parse the multi-part mime that is sent via the HttpServletRequest InputStream.
 * This class also relies on Faces to populate the fileUploadStatus in this Backing Bean on each request.
 *
 * @author basler
 * @author Binu John
 */
public class FileUploadHandler {

    private static Logger _logger = null;
    private FileUploadStatus fileUploadStatus = null;
    private static final String FILE_UPLOAD_LOGGER = "com.sun.javaee.blueprints.components.ui.fileupload";
    private static final String FILE_UPLOAD_LOG_STRINGS = "com.sun.javaee.blueprints.webapp.fileupload.LogStrings";
    
    /**
     * Default location of upload directory is /domain_dir/lib/upload, unless the Sun Appserver system property exits, then it will
     * use the domain's lib/upload directory instead
     */
    public static final boolean bDebug = false;

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
            System.out.println("FileUploadPhaseListener error writting AJAX response : " + iox);
            getLogger().log(Level.SEVERE, "response.exeception", iox);
        }
        if (bDebug) {
            System.out.println("STATUS RETURN = " + status);
        }
    }

    /**
     * Write status of current/last upload to the response stream.  This method assumes that the fileUploadStatus will
     * be set using the managed property functionality of a backing bean.  This relationship is specified in the 
     * faces-config.xml file.  This method is accessed through the Shale-remoting dynamic framework.
     */
    public static void handleFileUploadFinal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (bDebug) {
            System.out.println("** In Final upload for STATUS RETURN = ");
        }
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/xml;charset=UTF-8");

        // use managed bean session object to allow the monitoring of the fileupload based on componentName
        //FileUploadStatus status=getFileUploadStatus();
        HttpSession session = request.getSession();
        FileUploadStatus status = (FileUploadStatus) session.getAttribute(FileUploadUtil.FILE_UPLOAD_STATUS);

        writeUploadResponse(response.getWriter(), status);
    }

    /**
     * Invoke the fileupload process that reads the input from the HttpServletRequest inputStream.  Since this 
     * component accesses the HttpServletRequest directly, this component currently may not work from within a portlet.  
     * This method assumes that the fileUploadStatus will be set using the managed property functionality of a backing bean.  
     * This relationship is specified in the faces-config.xml file. This method is accessed through the Shale-remoting dynamic framework.  
     */
    public Hashtable<String, String> handleFileUpload(HttpServletRequest request, HttpServletResponse response) {
        if (bDebug) {
            // print out header for 
            Enumeration enumx = request.getHeaderNames();
            String key = "";
            String listx = "";
            while (enumx.hasMoreElements()) {
                key = (String) enumx.nextElement();
                listx += "\n" + key + ":" + request.getHeader(key);
            }
            System.out.println("Incoming Header Item:" + listx);
        }

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
                // get directory to dump file into
                String serverLocationDir = htUpload.get(compName + "_" + FileUploadUtil.SERVER_LOCATION_DIR);
                if (bDebug) {
                    System.out.println("\n*** locationDir=" + serverLocationDir);
                }
                File fileDir = null;
                if (serverLocationDir == null) {
                    // ??? need to fix incase other than glassfish
                    // set to default dir location for glassfish
                    serverLocationDir = System.getProperty("com.sun.aas.instanceRoot");
                    if (serverLocationDir != null) {
                        serverLocationDir = WebappConstants.WEBAPP_IMAGE_DIRECTORY;
                        fileDir = new File(serverLocationDir);
                        fileDir.mkdirs();
                    } else {
                        // use the standard tmp directory
                        if (bDebug) {
                            System.out.println("\n*** other default locationDir=" + serverLocationDir);
                        }
                        fileDir = (File) request.getAttribute("javax.servlet.context.tempdir");
                        serverLocationDir = fileDir.toString();
                    }
                } else {
                    // make sure directory exists, don't create automatically for security reasons
                    fileDir = new File(serverLocationDir);
                }

                // make sure dir exists and is writable
                if (fileDir == null || !fileDir.isDirectory() || !fileDir.canWrite()) {
                    // error, directory doesn't exist or isn't writable
                    status.setUploadError("Directory \"" + fileDir.toString() + "\" doesn't exist!");
                    getLogger().log(Level.SEVERE, "directory.inaccessable", fileDir.toString());
                    return null;
                }

                // Parse the request and return list of "FileItem" whle updating status
                FileItemIterator iter = upload.getItemIterator(request);

                status.setReadingComplete();

                FileItemStream item = null;
                String itemName = null;

                while (iter.hasNext()) {
                    item = iter.next();
                    if (item.isFormField()) {
                        // handle a form item being uploaded
                        itemName = item.getFieldName();

                        // process form(non-file) item
                        int size = formItemFound(item, htUpload);
                        updateSessionStatus(itemName, size);

                        if (bDebug) {
                            System.out.println("Form field item:" + itemName);
                        }
                    } else {
                        String username = htUpload.get(WebConstants.SUBMITTER_USER_NAME_PARAM);
                        if (username == null) {
                            Person person = SecurityHandler.getInstance().getLoggedInPerson(request);
                            if (person != null) {
                                username = person.getUserName();
                            }
                        }

                        fileItemFound(item, htUpload, serverLocationDir, username);
                    }
                }

                // put upload to 100% to handle rounding errors in status calc
                status.setUploadComplete();
                if (bDebug) {
                    System.out.println("Final session status - " + status);
                }
            } catch (Exception e) {
                status.setUploadError("FileUpload didn't complete successfully.  Exception received:" + e.toString());
                getLogger().log(Level.SEVERE, "file.upload.exception", e);
            }
        }

        return htUpload;
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
            if (bDebug) {
                System.out.println("Inserting form item in map " + key + " = " + value);
            }
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
            String serverLocationDir, String user) throws Exception {
        if (user == null) {
            user = "guest";
        }
        String fileLocation = null;
        String fileName = item.getName();
        //issue is that the same filename on client machine is being used on mastermachine.  this could lead to conflicts.
        //use the username since is unique for image name.  retrieve this from the Hashtable.
        String username = htUpload.get(WebConstants.USER_NAME_PARAM);

        if (fileName != null && !fileName.equals("")) {
            // see if IE on windows which send full path with item, but just filename
            // check for both separators because client OS my be different that server OS
            // check for unix separator
            if (bDebug) {
                System.out.println("Have item - " + fileName + " - in name= " + item.getFieldName());
            }
            int iPos = fileName.lastIndexOf("/");
            if (iPos > -1) {
                fileName = fileName.substring(iPos + 1);
                if (bDebug) {
                    System.out.println("Have full path, need to truncate \n" + item.getName() + "\n" + fileName);
                }
            }
            // check for windows separator
            iPos = fileName.lastIndexOf("\\");
            if (iPos > -1) {
                fileName = fileName.substring(iPos + 1);
                if (bDebug) {
                    System.out.println("Have full path, need to truncate \n" + item.getName() + "\n" + fileName);
                }
            }

            fileLocation = serverLocationDir + File.separator + fileName;

            DateFormat dateFormat = new SimpleDateFormat("MMMMddyyyy_hh_mm_ss");
            Date date = null;
            String thumbnailName;
            String thumbnailLocation;
            String ext = WebappUtil.getFileExtension(fileName);
                
            if (item.getFieldName().equals(WebConstants.UPLOAD_PERSON_IMAGE_PARAM)) {
                fileName = "p" + username;
                thumbnailName = fileName + "_thumb" + ext;
                thumbnailLocation = serverLocationDir + "/" + thumbnailName;
                // Append the extension
                fileName += ext;
                fileLocation = serverLocationDir + "/" + fileName;
                writeWithThumbnail(item, fileLocation, thumbnailLocation);
                htUpload.put(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM, thumbnailName);
            } else if (item.getFieldName().equals(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM)) {
                fileName = "p" + username + "_t" + ext;
                fileLocation = serverLocationDir + "/" + fileName;
                write(item, fileLocation);
                htUpload.put(WebConstants.UPLOAD_PERSON_IMAGE_THUMBNAIL_PARAM, fileName);
            } else if (item.getFieldName().equals(WebConstants.UPLOAD_EVENT_IMAGE_PARAM)) {
                date = new Date();
                //String submitter = htUpload.get(WebConstants.SUBMITTER_USER_NAME_PARAM);
                fileName = "e" + user + dateFormat.format(date);
                thumbnailName = fileName + "_thumb" + ext;
                thumbnailLocation = serverLocationDir + "/" + thumbnailName;
                fileName += ext;
                fileLocation = serverLocationDir + "/" + fileName;
                writeWithThumbnail(item, fileLocation, thumbnailLocation);
                htUpload.put(WebConstants.UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM, thumbnailName);
            } else if (item.getFieldName().equals("eventThumbnail")) {
                date = new Date();
                fileName = "e" + user + dateFormat.format(date) + "_t" + ext;
                fileLocation = serverLocationDir + "/" + fileName;
                write(item, fileLocation);
                htUpload.put(WebConstants.UPLOAD_EVENT_IMAGE_THUMBNAIL_PARAM, fileName);
            } else if (item.getFieldName().equals("upload_event_literature")) {
                date = new Date();
                fileName = "e" + user + dateFormat.format(date) + ext;
                fileLocation = serverLocationDir + "/" + fileName;
                write(item, fileLocation);
                htUpload.put(WebConstants.UPLOAD_LITERATURE_PARAM, fileName);
            } else {
                System.out.println("******** what is this?  item is " + item.getFieldName());
            }
            if (bDebug) {
                System.out.println("Writing item to " + fileLocation);
            // store image location in Hashtable for later access
            }
            String fieldName = item.getFieldName();
            if (bDebug) {
                System.out.println("Inserting form item in map " + fieldName + " = " + fileName);
            }
            htUpload.put(fieldName, fileName);
            // insert location
            String key = fieldName + FileUploadUtil.FILE_LOCATION_KEY;
            if (bDebug) {
                System.out.println("Inserting form item in map " + key + " = " + fileLocation);
            }
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
     * @param htUpload The Status Hashtable that contains the items that have been uploaded for post-processing use
     */
    public static void writeUploadResponse(PrintWriter writer, FileUploadStatus status) {
        Hashtable<String, String> htUpload = status.getUploadItems();
        try {
            if (bDebug) {
                System.out.println("\n***In writeUploadResponse");
            //  make sure session data exists
            }
            if (htUpload != null) {
                String compName = htUpload.get(FileUploadUtil.COMPONENT_NAME);
                //FileUploadStatus status=getFileUploadStatus();
                // check to see if user has custom post-processing method
                String postProcessingMethod = htUpload.get(compName + "_" + FileUploadUtil.POST_PROCESSING_METHOD);
                if (bDebug) {
                    System.out.println("\n*** postProcessingMethod = " + postProcessingMethod);
                }
                if (postProcessingMethod != null) {
                    // call post-processing method
                    // extension mechanism
                }

                // see if custom response set
                if (!status.isCustomReturnEnabled()) {
                    // return default response
                    if (bDebug) {
                        System.out.println("FINAL STATUS - " + status);
                    }
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
            if (bDebug) {
                System.out.println("FileUploadPhaseListener error writting AJAX response : " + e);
            }
            getLogger().log(Level.SEVERE, "response.exeception", e);
        }

    }

    /**
     * write out file item to a file using the Apache Commons FileUpload FileItem.write method as a guide,
     * so the output could be monitored
     *
     * @param item The Commons Fileupload item being loaded
     * @param file File path to write uploaded data.
     * @throws Exception Exceptions propagated from Apache Commons Fileupload classes
     */
    public void write(FileItemStream item, String filePath) throws Exception {
        // use name for update of session
        String itemName = item.getName();
        
        ServiceLocator locator = ServiceLocator.getInstance();

        FileSystem fs = locator.getFileSystem();

        if (bDebug) {
            System.out.println("Getting fileItem from memory - " + itemName);
        }
        OutputStream fout = fs.create(filePath);

        // It would have been more efficient to use NIO if we are writing to 
        // the local filesystem. However, since we need to support DFS, 
        // a simple solution is provided. 
        // TO DO: Optimize write if required.

        try {
            byte[] buf = new byte[2048];
            int count,  size = 0;
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
     * @param imagePath path to the save the image. 
     * @param thumbnailPath path prefix to the thumbnail. Extension is appended.
     * @throws java.lang.Exception
     */
    public void writeWithThumbnail(FileItemStream item,
            String imagePath, String thumbnailPath) throws Exception {
        WebappUtil.saveImageWithThumbnail(item.openStream(), imagePath, thumbnailPath);
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

    /**
     * Method getLogger
     *
     * @return Logger - logger for the NodeAgent
     */
    public static Logger getLogger() {
        if (_logger == null) {
            _logger = Logger.getLogger(FILE_UPLOAD_LOGGER, FILE_UPLOAD_LOG_STRINGS);
        }
        return _logger;
    }
}

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
package org.apache.olio.webapp.rest;
//for Jersey - JSR311 actions
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.BodyPartEntity;

import java.awt.image.BufferedImage;
import java.util.List;
import java.net.URI;
//olio imports
import org.apache.olio.webapp.model.ModelFacade;
import org.apache.olio.webapp.model.Person;
import org.apache.olio.webapp.util.WebappUtil;
import org.apache.olio.webapp.model.Address;
import org.apache.olio.webapp.controller.WebConstants;
import javax.servlet.ServletContext;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
//utx
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.transaction.UserTransaction;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Logger;


/**
 *
 * @author klichong
 */
@Path("/users")
public class UsersResource {

  @Context UriInfo uriInfo;
  @Context ServletContext context;
  ModelFacade mf;

  /* move this to ModelFacade
  @PersistenceUnit(unitName = "BPWebappPu")
  EntityManagerFactory emf;
  @Resource
   */

  private static final String USERNAME = "user_name";
  private static final String FIRSTNAME = "first_name";
  private static final String LASTNAME = "last_name";
  private static final String IMAGEFILE = "upload_person_image";
  private static final String PASSWORD = "password";
  private static final String SUMMARY = "summary";
  private static final String TELEPHONE = "telephone";
  private static final String EMAIL = "email";
  private static final String STREET1 = "street1";
  private static final String STREET2 = "street2";
  private static final String CITY = "city";
  private static final String ZIP = "zip";
  private static final String STATE = "state";
  private static final String TIMEZONE = "timezone";
  private static final String COUNTRY = "country";

  private static final String FILE_LOCATION = "image_location";
  private static final String THUMBNAIL_LOCATION = "thumbnail_location";
  private Logger logger = Logger.getLogger(UsersResource.class.getName());

    public UsersResource() {
                 
    }

    private ServletContext getServletContext() {
        return context;
    }


  @GET
  @Produces("application/json")
    //public JSONObject getUser(@PathParam("userid") String userid) throws JSONException, Exception {
       public JSONArray getUsers() throws JSONException, Exception {
      logger.finer(" get USERs is here !");
      mf = (ModelFacade)getServletContext().getAttribute(WebConstants.MF_KEY);
      List<Person> allUsers = mf.getAllPersons();
       JSONArray uriArray = new JSONArray();
        for (Person userEntity : allUsers) {
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI userUri = ub.
                    path(userEntity.getUserName()).
                    build();
            uriArray.put(userUri.toASCIIString());
        }
        return uriArray;

  }

  
  @GET
  @Path("{userid}/")
  @Produces("application/json")
  //@Produces("text/plain")

    public JSONObject getUser(@PathParam("userid") String userid) throws JSONException, Exception {

        
        mf = (ModelFacade)getServletContext().getAttribute(WebConstants.MF_KEY);
        Person personEntity = mf.findPerson(userid);        
        if (personEntity==null) throw new Exception ("User Not Found");

        return new JSONObject()
                .put("username", personEntity.getUserName())
                .put("password", personEntity.getPassword())
                .put("firstName",personEntity.getFirstName())
                .put("lastName", personEntity.getLastName())
                .put("summary", personEntity.getSummary())
                .put("telephone", personEntity.getTelephone())
                .put("email", personEntity.getEmail())
                .put("imageURL", personEntity.getImageURL())
                .put("address", personEntity.getAddress())
                ;
  }

    @DELETE
    @Path("{userid}/")
    public void deleteUser(@PathParam("userid") String userid) throws JSONException, Exception {       
        if (null == userid) {
            throw new Exception("userid " + userid + "does not exist!");
        }
        mf = (ModelFacade)getServletContext().getAttribute(WebConstants.MF_KEY);
        mf.deletePerson(userid);
    }

    @PUT
    //@POST
    @Consumes("multipart/form-data")
    public Response addUpdateUser(FormDataMultiPart multiPart) throws JSONException, Exception {
        Person personEntity=null;
        String firstName=null, lastName=null, userName=null, password=null, summary=null, telephone=null, email=null;
        //address
        String street1=null, street2=null, city=null, state=null, zip=null, timezone=null, country=null;
        String contentHeader=null, filename=null;
        InputStream imageStream = null;
        boolean doesExist = false;

        BodyPartEntity bpe = null;
        MultivaluedMap <String, String> mvMap = null;
        List<String> headerList = null;

         Map<String,List<FormDataBodyPart>> bodyList = multiPart.getFields();
         for (String key:bodyList.keySet()){
             logger.finer("key is "+ key);
         }
         //if(multiPart.getField("first_name")!=null) firstName = multiPart.getField("first_name").getValue();
         userName = getFieldValue(multiPart, USERNAME, true);
         //determine if this is an update or create
         personEntity = mf.getPerson(userName);
         doesExist =  personEntity == null ? false : true;

         firstName = getFieldValue(multiPart, FIRSTNAME, true);
         lastName = getFieldValue(multiPart, LASTNAME, true);
         password = getFieldValue(multiPart, PASSWORD, true);
         summary = getFieldValue(multiPart, SUMMARY, false);
         telephone = getFieldValue(multiPart,TELEPHONE, false);
         email = getFieldValue(multiPart, EMAIL, false);
         timezone = getFieldValue(multiPart, TIMEZONE, false);
         logger.finer("in POST - multipart first name is "+ firstName);

         //address
         street1 = getFieldValue(multiPart, STREET1, false);
         street2 = getFieldValue(multiPart, STREET2, false);
         city = getFieldValue(multiPart, CITY, false);
         state = getFieldValue(multiPart, STATE, false);
         zip =  getFieldValue(multiPart, ZIP, false);
         country = getFieldValue(multiPart, COUNTRY, false);
         Address address=WebappUtil.handleAddress(null, street1, street2, city, state, zip, country);

        
         //image processing
         if(multiPart.getField(IMAGEFILE)!=null){
            bpe = ((BodyPartEntity) multiPart.getField(IMAGEFILE).getEntity());
            logger.finer("the value of the imageFile body part is "+ multiPart.getField(IMAGEFILE).getHeaders());
            mvMap = multiPart.getField(IMAGEFILE).getHeaders();
            headerList  =  (List)mvMap.get("Content-Disposition");
            contentHeader = headerList.get(0);
            filename = WebappUtil.parseValueFromHeader(contentHeader, "filename");
            imageStream = bpe.getInputStream();
         } else {
             logger.finer("parameter " + IMAGEFILE + " is missing");
         }

         /* temporary - testing multipart post
         File tempFile = new File("/tmp/copyImagefile.jpg");
         FileOutputStream fos = new FileOutputStream(tempFile);
         BufferedImage bi = ImageIO.read(stream);
         ImageIO.write(bi, "jpg", tempFile);
         fos.flush();
         fos.close();
         bpe.cleanup();
          */
          
         HashMap<String,String> fileInfo = saveImageWithThumbnail(userName, imageStream, filename);
           
         //create Person
         Person person = new Person(userName, password, firstName, lastName,
                 summary, email, telephone, fileInfo.get(FILE_LOCATION), fileInfo.get(THUMBNAIL_LOCATION), timezone, address);
          mf = (ModelFacade)getServletContext().getAttribute(WebConstants.MF_KEY);
         if (!doesExist)
            userName = mf.addPerson(person);
         else
            mf.updatePerson(person);
         logger.finer("after adding/updating person " + userName);

         //cleanup
         imageStream.close();
         //delete the temporary file from multipart
         bpe.cleanup();


         return Response.ok().build();

    }

    private HashMap saveImageWithThumbnail(String userName, InputStream imageInputStream, String filename)
            throws IOException {
            /*
            DateFormat dateFormat = new SimpleDateFormat("MMMMddyyyy_hh_mm_ss");
            Date date = null;
             */
            String thumbnailName;
            String thumbnailLocation;
            String fileName;
            String fileLocation;
            String ext=null;

             ext = WebappUtil.getFileExtension(filename);
            logger.finer(" ext is "+ ext);
            WebappUtil.setArtifactPath();
            String serverLocationDir = WebappUtil.getArtifactLocalionDir();
            logger.finer("serverLocationDir is "+ serverLocationDir);
            
            fileName = "p" + userName;
                thumbnailName = fileName + "_thumb" + ext;
                thumbnailLocation = serverLocationDir + "/" + thumbnailName;
                // Append the extension
                fileName += ext;
                fileLocation = serverLocationDir + "/" + fileName;
                /*** Shanti - commenting as not used and incorrect call
                WebappUtil.saveImageWithThumbnail(imageInputStream, fileLocation, thumbnailLocation);
                 ****/
            HashMap imageInfo = new HashMap(2);
            imageInfo.put(FILE_LOCATION, fileLocation);
            imageInfo.put(THUMBNAIL_LOCATION, thumbnailLocation);

            return imageInfo;
    }

    private String getFieldValue(FormDataMultiPart multiPart, String fieldName, boolean isRequired) throws Exception {
        String returnString = null;
        if (multiPart.getField(fieldName) == null) {
            if (isRequired) {
                throw new Exception("missing parameter " + fieldName);
            } else {
                returnString = "";
            }

        } else {
            returnString = multiPart.getField(fieldName).getValue();
        }
        return returnString;
    }

    


}

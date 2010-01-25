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
* $Id: FileUploadStatus.java,v 1.1 2007/03/20 02:14:38 basler Exp $  
 */


/*
 * FileUploadStatus.java
 *
 * Created on January 18, 2006, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.apache.olio.webapp.fileupload;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

/**
 * Managed session object that is used to track status of the current/last fileupload
 *
 * @author basler
 */
public class FileUploadStatus implements Serializable {
    
    public static final String UPLOAD_ERROR="Error";
    public static final String NOT_RUNNING="Not Running";
    public static final String WRITING_DATA="Writing upload data";
    public static final String READING_DATA="Reading upload data";
    public static final String UPLOAD_COMPLETED="Upload Completed";
    
    
    private String name="", currentItem="", status=READING_DATA, message="Currently Uploading File(s)";
    private long totalUploadSize=0, currentSizeRead=0, currentSizeWritten=0;
    private Date startUploadDate=new Date();
    private Date endUploadDate=startUploadDate;
    private Hashtable<String, String> htUpload=new Hashtable<String, String>(); 
    boolean customReturnEnabled=false;

    
    public FileUploadStatus() {
    }
    
    /** Creates a new instance of FileUploadStatus */
    public FileUploadStatus(String compName) {
        name=compName;
    }
    
    public FileUploadStatus(String compName, long totSize, long currLoadedSize, Date startDate) {
        name=compName;
        totalUploadSize=totSize; 
        currentSizeRead=currLoadedSize;
        startUploadDate=startDate;
        status=READING_DATA;
    }
    
    public void reset() {
        name="";
        currentItem="";
        status=READING_DATA;
        message="Currently Uploading File(s)";
        totalUploadSize=0;
        currentSizeRead=0;
        currentSizeWritten=0;
        startUploadDate=new Date();
        endUploadDate=null;
        customReturnEnabled=false;
    }
    
    public void enableCustomReturn(){
        customReturnEnabled=true;
    }
    
    public void setCustomReturnEnabled(boolean customReturnx) {
        customReturnEnabled=customReturnx;
    }
    public boolean getCustomReturnEnabled() {
        return customReturnEnabled;
    }
    public boolean isCustomReturnEnabled() {
        return customReturnEnabled;
    }

    public void setName(String namex) {
        name=namex;
    }
    public String getName() {
        return name;
    }

    public void setMessage(String mess) {
        message=mess;
    }
    public String getMessage() {
        return message;
    }
    public void setStatus(String stat) {
        status=stat;
    }
    public String getStatus() {
        return status;
    }
    
    public void setStartUploadDate(Date start) {
        startUploadDate=start;
    }
    public Date getStartUploadDate() {
        return startUploadDate;
    }
    
    public void setEndUploadDate(Date end) {
        endUploadDate=end;
    }
    public Date getEndUploadDate() {
        return endUploadDate;
    }
    
    public void setCurrentItem(String item) {
        currentItem=item;
    }
    public String getCurrentItem() {
        return currentItem;
    }
    
    public void setTotalUploadSize(long size) {
        totalUploadSize=size;
    }
    public long getTotalUploadSize() {
        return totalUploadSize;
    }

    public void setUploadItems(Hashtable<String, String> ht) {
        htUpload=ht;
    }
    public Hashtable<String, String> getUploadItems() {
        return htUpload;
    }
    
    
    //
    // these accessor and mutators are for uploading bits from web page
    public void incrementCurrentSizeRead(int size) {
        currentSizeRead += size;
    }
    public void incrementCurrentSizeRead(long size) {
        currentSizeRead += size;
    }
    public long getCurrentSizeRead() {
        return currentSizeRead;
    }
    public int getPercentageRead() {
        float perc=(new Long(currentSizeRead).floatValue() / new Long(totalUploadSize).floatValue()) * 100f;
        return new Float(perc).intValue();
    }
    
    public void setReadingComplete() {
        status=WRITING_DATA;
       currentSizeRead=totalUploadSize;
    }


    //
    // these accessor and mutators are for writing out bits to disk
    public void incrementCurrentSizeWritten(int size) {
        currentSizeWritten += size;
    }
    public void incrementCurrentSizeWritten(long size) {
        currentSizeWritten += size;
    }
    public long getCurrentSizeWritten() {
        return currentSizeWritten;
    }
    public int getPercentageWritten() {
        float perc=(new Long(currentSizeWritten).floatValue() / new Long(totalUploadSize).floatValue()) * 100f;
        return new Float(perc).intValue();
    }

    
    public long getUploadTime() {
        long endTime;
        if(endUploadDate == null) {
            // upload should still be running or has ended incorrectly
            endTime=new Date().getTime();
        } else {
            endTime=endUploadDate.getTime();
        }
        return endTime - startUploadDate.getTime();
    }

    public String getUploadTimeString() {
        long duration=getUploadTime();
        return formatTimeAsString(duration);
    }
    
    public int getPercentageComplete() {
        return (getPercentageRead() + getPercentageWritten()) / 2;
    }
    
    public long getEstimatedFinishTime() {
        // simple calc (current time / ((percent uploaded + percent written)/2)) * (200 - percent complete)
        float perc=new Integer((getPercentageRead() + getPercentageWritten()) / 2).floatValue();
        float timeLeft=0;
        if(perc > 0) {
            timeLeft=(new Long(getUploadTime()).floatValue() / perc) * (100f - perc);
        }
        
        return new Float(timeLeft).longValue();
    }
    
    public String getEstimatedFinishTimeString() {
        long duration=getEstimatedFinishTime();
        return formatTimeAsString(duration);
    }
    
    public String formatTimeAsString(long duration) {
        StringBuffer sb=new StringBuffer();
        // calc hours
        long hours=duration / 3600000;
        long remainder=duration % 3600000;
        // calc minutes
        long minutes=remainder / 60000;
        // calc seconds
        float seconds=(new Long(remainder).floatValue() % 60000f) / 1000f;
        if(hours == 1) {
            sb.append(String.valueOf(hours));
            sb.append(" hour ");
        } else if(hours > 1) {
            sb.append(String.valueOf(hours));
            sb.append(" hours ");
        }
        if(minutes == 1) {
            sb.append(String.valueOf(minutes));
            sb.append(" minute ");
        } else if(minutes > 1) {
            sb.append(String.valueOf(minutes));
            sb.append(" minutes ");
        }
        sb.append(String.valueOf(seconds));
        sb.append(" seconds ");
        
        return sb.toString();
    }
    
    public void setUploadComplete() {
       currentSizeRead=totalUploadSize;
       currentSizeWritten=totalUploadSize;
       currentItem="";
       status=UPLOAD_COMPLETED;
       message="File Upload has Completed Successfully!";
       endUploadDate=new Date();
    }

    public void setUploadError(String error) {
       currentItem="";
       status=UPLOAD_ERROR;
       message=error;
       endUploadDate=new Date();
    }
    
    public String toString() {
        StringBuffer sb=new StringBuffer();
        sb.append("\nComponent Name:");
        sb.append(name);
        sb.append(" - Status:");
        sb.append(status);
        sb.append(" - Message:");
        sb.append(message);
        sb.append(" - CurrentUploadItem:");
        sb.append(currentItem);
        sb.append(" - TotalUploadSize:");
        sb.append(totalUploadSize);
        sb.append(" - CurrentSizeUploaded:");
        sb.append(currentSizeRead);
        sb.append(" - CurrentSizeWritten:");
        sb.append(currentSizeWritten);
        sb.append(" - StartDate:");
        sb.append(startUploadDate);
        sb.append(" - EndDate:");
        sb.append(endUploadDate);
        sb.append(" - PercentageRead:");
        sb.append(getPercentageRead());
        sb.append(" - PercentageWritten:");
        sb.append(getPercentageWritten());
        sb.append(" - PercentageCompleted:");
        sb.append(getPercentageComplete());
        sb.append(" - UploadTime:");
        sb.append(getUploadTimeString());
        sb.append(" - EstimatedFinishTime:");
        sb.append(getEstimatedFinishTimeString());
        return sb.toString();
    }
    
}

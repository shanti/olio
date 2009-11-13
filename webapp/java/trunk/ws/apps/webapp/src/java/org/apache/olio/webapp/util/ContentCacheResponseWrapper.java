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


package org.apache.olio.webapp.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Utility class to wrap the Servlet Response so that it can be stored
 * 
 * @author Binu John
 */
public class ContentCacheResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream bos;
    private int contentLength;
    private String contentType;
    private ServletOutputStream stream;

    /** Creates a new instance of ContentCacheResponseWrapper */
    public ContentCacheResponseWrapper(HttpServletResponse response) {
        super (response);
        bos = new ByteArrayOutputStream();
        stream = new CacheContentServletOutputStream(bos);
    }

    public byte[] getData(){
        try {
            stream.flush();
        }
        catch(Exception exception) { }
        return bos.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream(){
        return stream;
    }

    @Override
    public PrintWriter getWriter(){
        return new PrintWriter(getOutputStream(), true);
    }

    @Override
    public void setContentLength(int i) {
        contentLength = i;
        super.setContentLength(i);
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentType(String s) {
        contentType = s;
        super.setContentType(s);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

   

    @Override public void setBufferSize(int i) {
        //System.err.println("setBufferSize(int i) called.");
    }

    @Override public void flushBuffer() throws IOException {
        //System.err.println("flushBuffer() called.");
        stream.flush();
    }
  
}

class CacheContentServletOutputStream extends ServletOutputStream {
    private DataOutputStream stream;
    
    public CacheContentServletOutputStream (OutputStream out) {
        stream = new DataOutputStream (out);
    }

    public void write(int b) throws IOException {
        stream.write(b);
    }
    
    public void write(byte data[]) throws IOException {
        stream.write(data);
        stream.flush();
    }

    public void write(byte data[], int i, int j) throws IOException {
        stream.write(data, i, j);
    }

    public void flush() throws IOException {
        stream.flush();
    }

}

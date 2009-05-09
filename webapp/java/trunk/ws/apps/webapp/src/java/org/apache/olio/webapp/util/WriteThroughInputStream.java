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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class writes the data that is read into an output stream.
 * Useful for reading the image from the input stream and writing to the file stystem.
 *
 * @author Binu John
 */
public class WriteThroughInputStream extends InputStream {

    private OutputStream os;
    private InputStream is;
    
    public WriteThroughInputStream (InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }
    
    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        return read (b);
    }

    @Override
    public int read (byte[] buf, int offset, int length) throws IOException {
        int rCount = is.read(buf, offset, length);
        if (os != null && rCount != -1)
            os.write(buf, offset, rCount);
        return rCount;
    }
    
    @Override
    public int read (byte[] buf) throws IOException {
        return read (buf, 0, buf.length);
    }
    
    public void closeInputStream() throws IOException {
        is.close();
    }
    
    public void closeOutputStream() throws IOException {
        os.close();
        os = null;
    }
}

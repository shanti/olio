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
package com.sun.web20.fsloader;

import org.apache.olio.workload.util.ScaleFactors;

import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileLoader {

    public static void main(String[] args) throws Exception {
        String srcDir = args[0];
        ScaleFactors.setActiveUsers(Integer.parseInt(args[1]));
        srcDir += File.separator;
        FileChannel img = new FileInputStream(
                                    srcDir + "person.jpg").getChannel();
        FileChannel thumb = new FileInputStream(
                                    srcDir + "person_thumb.jpg").getChannel();
        long imgSize = img.size();
        long thumbSize = thumb.size();

        for (int i = 1; i <= ScaleFactors.users; i++) {
            System.out.println("Loading files for user " + i);
            copyTo(img, imgSize, "p" + i + ".jpg");
            copyTo(thumb, thumbSize, "p" + i + "_thumb.jpg");
        }

        img.close();
        thumb.close();


        img = new FileInputStream(srcDir + "event.jpg").getChannel();
        thumb = new FileInputStream(srcDir + "event_thumb.jpg").getChannel();
        FileChannel lit = new FileInputStream(
                                    srcDir + "event.pdf").getChannel();

        imgSize = img.size();
        thumbSize = thumb.size();
        long litSize = lit.size();

        for (int i = 1; i <= ScaleFactors.events; i++) {
            System.out.println("Loading files for event " + i);
            copyTo(img, imgSize, "e" + i + ".jpg");
            copyTo(thumb, thumbSize, "e" + i + "_thumb.jpg");
            copyTo(lit, litSize, "e" + i + ".pdf");
        }

        img.close();
        thumb.close();
        lit.close();
        System.exit(0);
    }

    private static void copyTo(FileChannel src, long size, String destFile)
            throws IOException {
        FileChannel dest = (new FileOutputStream(destFile)).getChannel();
        src.transferTo(0, size, dest);
        dest.close();
    }
}

package com.sun.web20.fsloader;

import com.sun.web20.util.ScaleFactors;

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
            copyTo(thumb, thumbSize, "p" + i + "t.jpg");
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
            copyTo(thumb, thumbSize, "e" + i + "t.jpg");
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

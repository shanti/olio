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

import org.apache.olio.webapp.util.fs.FileSystem;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

/**
 * Utility to scale the image to create the thumbnail.
 * 
 * @author Binu John
 */
public class ImageScaler {
    
    private int thumbWidth = 133;
    private int thumbHeight = 100;
    private String format = "jpg";
    BufferedImage image = null;
    
    public static int IMAGEIO_DECODER = 1;
    public static int SUN_JPEG_DECODER = 2;
    
    private static int decoder = IMAGEIO_DECODER;
    
    static {
        String ds = System.getProperty("webapp.image.decoder");
        if (ds != null && ds.equalsIgnoreCase("JpegDecoder"))
            decoder = SUN_JPEG_DECODER;
    }
    
    public ImageScaler() {
        
    }
    /** Creates a new instance of ImageScaler */
    public ImageScaler(String imagePath) throws IOException {
        // Addded for DFS support
        // if we use Hadoop or Mogile, we need to get the image from the remote store
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();
        InputStream iStream = fs.open(imagePath);
        this.image = ImageIO.read(iStream);
        try {
            iStream.close();
        }
        catch (Exception e) {}
    }
    
    /** Create ImageScaler from InputStream */
    public void load (InputStream is) throws IOException {
        this.image = ImageIO.read(is);
    }

    public void customLoad (RandomAccessFile raf) throws Exception {
    image = ImageIO.read(new FileImageInputStream(raf));
    }
    
    public void customLoad (InputStream is) throws IOException {
        // Use memory cached rather than file cached.
        // This will cause problems if the image sizes are large
        // Using ImageIO.read(InputStream) leads to the use of FileCacheImageInputStream.
        // FileCacheIIS uses random access file for writing to cache.
        // I haven't checked into making the final image file as the cache file.
        // The concern is whether all the data will be flushed to that file.
        
        if (decoder == IMAGEIO_DECODER) {
            MemoryCacheImageInputStream mcis = new MemoryCacheImageInputStream(is);
            image = ImageIO.read(mcis);
        }
        else {
            com.sun.image.codec.jpeg.JPEGImageDecoder idecoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGDecoder(is);
            this.image = idecoder.decodeAsBufferedImage();
        }
    }
    
    public void write (String path) throws IOException {
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();
        OutputStream oStream = fs.create(path);
        ImageIO.write(image, format, oStream);
        try {
            oStream.close();
        }
        catch (Exception e) {}
    }
    
    /* constructor with the target image size
     * @param width Width of the target image
     * @param height Height of the target image
     */
    public ImageScaler(int width, int height, String imagePath) throws IOException {
        this.thumbWidth = width;
        this.thumbHeight = height;
        
        // Added for for DFS support
        // if we use Hadoop or Mogile, we need to get the image from the remote store
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();
        InputStream iStream = fs.open(imagePath);
        this.image = ImageIO.read(iStream);
        try {
            iStream.close();
        }
        catch (Exception e) {}
    }
    
    /* must be called before resize method
     * when it is necessary to keep the aspect ratio
     */
    public void keepAspectWithWidth() {
        this.thumbHeight = this.image.getHeight() * this.thumbWidth / this.image.getWidth();
    }
    public void keepAspectWithHeight() {
        this.thumbWidth = this.image.getWidth() * this.thumbHeight / this.image.getHeight();
    }
    /* Using getScaledInstance
     * good quality, but very slow
     * @param from the path of the original image
     * @param to the path of the target thumbnail
     */
    public void resizeWithScaledInstance(String to) throws IOException {
        BufferedImage bThumb = new BufferedImage(thumbWidth, thumbHeight, image.getType());
    bThumb.getGraphics().drawImage(image.getScaledInstance(thumbWidth, thumbHeight,
                Image.SCALE_FAST), 0, 0, thumbWidth, thumbHeight, null);
        
    // Store image based on local of distributed files systems
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();
        OutputStream oStream = fs.create(to);
        ImageIO.write(bThumb, format, oStream);
    
        try {
            oStream.close();
        }
        catch (Exception e) {}
    }
    
    /* Using Graphics2D
     * medium quality, fast
     * @param from the path of the original image
     * @param to the path of the target thumbnail
     */
    public void resizeWithGraphics(OutputStream to) throws IOException {
        BufferedImage th = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = th.createGraphics();
        /* Simplify - trade off performance for quality
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         * */
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        
        // Added for for distributed file system support
        // Store image based on local of distributed files systems
        ImageIO.write(th, format, to);
    }
    
    /* Using Affine transform
     * for transform with power(0.5, etc.). fastest.
     * @param from the path of the original image
     * @param to the path of the target thumbnail
     * @param power to rescale(0.25, 0.5...)
     */
    public void resizeWithAffineTransform(String to, double  power) throws IOException {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage th = new BufferedImage((int)(w*power), (int)(h*power), image.getType());
        double powerW = ((double) thumbWidth) / w;
        double powerH = ((double) thumbHeight) / h;
        AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(powerW, powerH),
                AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, th);

        // Added for distributed file system support
        // Store image based on local of distributed files systems
        FileSystem fs = ServiceLocator.getInstance().getFileSystem();
        OutputStream oStream = fs.create(to);
        ImageIO.write(th, format, oStream);

        try {
            oStream.close();
        }
        catch (Exception e) {}
    }
    
    /* setting the target file format
     * @param format specifying the image format, such as "jpg"
     */
    public void setFileFormat(String format) {
        this.format = format;
    }
}

package com.moonkitty.Util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GifLoader {
    public static BufferedImage[] loadGif(InputStream gifStream) throws Exception {
        ImageInputStream imagestream = ImageIO.createImageInputStream(gifStream);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");

        if (!readers.hasNext()) {
            throw new IllegalStateException("No Gif Reader!");
        }

        if (gifStream == null) {
            throw new IllegalArgumentException("GIF stream is null");
        }

        ImageReader reader = readers.next();

        try {

            reader.setInput(imagestream, false);

            int frameCount = reader.getNumImages(true);

            List<BufferedImage> frames = new ArrayList<>();

            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = reader.read(i);
                frames.add(frame);
            }

            return frames.toArray(new BufferedImage[0]);

        } finally {
            reader.dispose();
            imagestream.close();
        }

    }
}
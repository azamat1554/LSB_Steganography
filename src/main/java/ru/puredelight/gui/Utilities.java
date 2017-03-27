package ru.puredelight.gui;


import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Класс содержащий служебные методы.
 *
 * @author Azamat Abidokov
 */
public class Utilities {
    /* Устанавливает указанные ограничения*/
    static GridBagConstraints setConstraints(GridBagConstraints gbc, int gridx, int gridy, int gridwidth,
                                             int gridheight, double weightx, double weighty) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return gbc;
    }

    public static BufferedImage getImage(File image) {
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getImage(InputStream image) {
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getImage(byte[] imageData) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytes(File image, int length) {
        byte[] bytesOfImage = new byte[length];
        try (FileInputStream fin = new FileInputStream(image)) {
            fin.read(bytesOfImage, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesOfImage;
    }

    private final static String BMP_MIME = "image/x-ms-bmp";
    private final static String PNG_MIME = "image/png";
    private final static String GIF_MIME = "image/gif";
    private final static String JPG_MIME = "image/jpeg";

    public static String getExtension(File image) {
        try {
            return MimeTypes
                    .getDefaultMimeTypes()
                    .forName(new Tika().detect(image))
                    .getExtension();
        } catch (IOException | MimeTypeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getExtension(byte[] image) {
        try {
            return MimeTypes
                    .getDefaultMimeTypes()
                    .forName(new Tika().detect(image))
                    .getExtension();
        } catch (MimeTypeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean supportImageAsContainer(File image) {
        String mimeType = null;
        try {
            Tika tika = new Tika();
            mimeType = tika.detect(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkImageType(mimeType, false);
    }

    public static boolean supportImageType(File image) {
        String mimeType = null;
        try {
            Tika tika = new Tika();
            mimeType = tika.detect(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkImageType(mimeType, true);
    }

    public static boolean supportImageType(byte[] image) {
        Tika tika = new Tika();
        String mimeType = tika.detect(image);

        return checkImageType(mimeType, true);
    }

    private static boolean checkImageType(String mimeType, boolean withJPG) {
        if (mimeType != null
                && (mimeType.equals(BMP_MIME)
                || mimeType.equals(PNG_MIME)
                || mimeType.equals(GIF_MIME)
                || (withJPG && mimeType.equals(JPG_MIME)))) {
            return true;
        }
        return false;
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     *
     * @param component - swing component
     * @param srcImg    - source image to scale
     * @return - the new resized image
     */
    public static Icon getScaledImage(JComponent component, BufferedImage srcImg) {
        int imageWidth = srcImg.getWidth();
        int imageHeight = srcImg.getHeight();

        //отношения между размерами компонента и размерами изображения
        float wRatio = (float) component.getWidth() / imageWidth;
        float hRatio = (float) (component.getHeight() - 20) / imageHeight;

        //коэффициент увеличения изображения равен меньшему из отношений
        float k = wRatio < hRatio ? wRatio : hRatio;

        int width = (int) (imageWidth * k);
        int height = (int) (imageHeight * k);


        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, width, height, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }
}

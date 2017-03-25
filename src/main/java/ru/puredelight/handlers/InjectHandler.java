package ru.puredelight.handlers;

import ru.puredelight.gui.Utilities;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * Created by azamat on 3/18/17.
 */
public class InjectHandler {
    //указатель на текущую позицию в сообщении
    private int pointer;
    //биты встраиваемого сообщения
    private BitSet imageBits;
    //длина сообщения в битах
    private int length;
    private int numberOfBit;

    public BufferedImage inject(BufferedImage originalImage, File secretImage, int numberOfBit) {
        pointer = 0;
        this.numberOfBit = 1;
        int mask = getMask(this.numberOfBit);

        //длина заголовка в битах
        int headerLength = Config.HEADER_LENGTH * 8;
        //длина заголовка + длина файла, в битах
        int dataLength = (int) (headerLength + secretImage.length() * 8);
        length = headerLength;

        imageBits = getBits(secretImage, numberOfBit, dataLength);
        BufferedImage fillImage = copyImage(originalImage);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int r, g, b, pixel, newPixel;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = fillImage.getRGB(x, y);

                r = (pixel >> 16) & mask | nextBits();
                g = (pixel >> 8) & mask | nextBits();
                b = pixel & mask | nextBits();

                newPixel = (0xFF000000 | r << 16 | g << 8 | b);
                fillImage.setRGB(x, y, newPixel);

                if (pointer == headerLength) {
                    length = dataLength;
                    this.numberOfBit = numberOfBit;
                    mask = getMask(numberOfBit);
                } else if (pointer >= dataLength) {
                    return fillImage;
                }
            }
        }
        System.out.println(pointer);
        System.out.println(length);

        return fillImage;
    }

    private int nextBits() {
        if (pointer == length) return 0;

        int bits = 0;
        for (int i = 0; i < numberOfBit; i++) {
            bits = (bits << 1) | (imageBits.get(pointer++) ? 1 : 0);
        }
        return bits;
    }

    /**
     * Структура заголовка при встраивании сообщения в контейнер,
     * с первого пикселя изображения:
     * <p>
     * [секретное слово (48 бит)]
     * [кол-во бит встраивания (3 бита)]
     * [длина встроенного сообщения в битах (29 бит)]
     * [сообщение]
     *
     * @return Биты сообщения
     */
    private BitSet getBits(File secretImage, int numberOfbit, int length) {
        ByteBuffer imgBytes = ByteBuffer.allocate(length);

        //добавляет тэг, который указывает на наличие сообщения в изображении
        imgBytes.put(Config.SECRET_TAG);
        //добавляет кол-во бит для встраивания и длину сообщения
        imgBytes.putInt(length & 0x1FFFFFFF | numberOfbit << 29);
        //добавляет байты файла
        imgBytes.put(Utilities.getBytes(secretImage, (int) secretImage.length()));
        imgBytes.position(0);
        return BitSet.valueOf(imgBytes);

//        byte[] bytes = Utilities.getBytes(secretImage, (int) secretImage.length());
//        imageBits = BitSet.valueOf(bytes);
    }

    private int getMask(int numberOfbit) {
        int mask = 0xFF;
        switch (numberOfbit) {
            case 1:
                mask = 0xFE;
                break;
            case 2:
                mask = 0xFC;
                break;
            case 3:
                mask = 0xF8;
                break;
            case 4:
                mask = 0xF0;
                break;
            case 5:
                mask = 0xE0;
                break;
            case 6:
                mask = 0xC0;
                break;
            case 7:
                mask = 0x80;
        }
        return mask;
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}

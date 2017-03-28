package ru.puredelight.handlers;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Класс для обработки извлечения секретного содержимого
 * из файла изображения.
 *
 * @author Azamat Abidokov
 */
public class ExtractHandler {
    //указатель на текущую позицию в извлекаемом сообщении
    private int pointer;
    //биты извлекаемого сообщения
    private BitSet imageBits;
    //длина сообщения в битах
    private int length;
    //кол-во замененных бит
    private int numberOfBit;

    /**
     * Этот метод извлекает секретное содержимое из файла изображения
     *
     * @param fillImage Изображение с секретным содержимым
     * @return Байты секретного файла
     */
    public byte[] eject(BufferedImage fillImage) {
        pointer = 0;
        numberOfBit = 1;
        imageBits = new BitSet();

        //длина заголовка в битах
        int headerLength = Config.HEADER_LENGTH * 8;
        length = headerLength;

        int width = fillImage.getWidth();
        int height = fillImage.getHeight();

        int pixel;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = fillImage.getRGB(x, y);

                extractBits(pixel >> 16 & 0xFF);
                extractBits(pixel >> 8 & 0xFF);
                extractBits(pixel & 0xFF);

                if (pointer == headerLength) { //если считали заголовок
                    //выйти, если изображение не содержит секретного слова
                    if (!isContainsSecret()) return null;

                    numberOfBit = getNumberOfBit();
                    length = getLength();
                } else if (pointer >= length) { //если считали все содержимое
                    return getFileData();
                }
            }
        }
        return null;
    }

    /**
     * Извлекает содержимое из переданной составляющей,
     * и записывает извлеченные биты в imageBits.
     *
     * @param colorPart Составляющая пикселя. Например: Red, Green или Blue.
     */
    private void extractBits(int colorPart) {
        if (pointer == length) return;

        for (int i = numberOfBit - 1; i >= 0; i--) {
            imageBits.set(pointer++, (colorPart >> i & 0x01) != 0);
        }
    }

    private int getNumberOfBit() {
        return ((ByteBuffer) ByteBuffer.wrap(imageBits.get(48, 80).toByteArray()).position(0))
                .getInt() >> 29 & 0x07;
    }

    private int getLength() {
        return ((ByteBuffer) ByteBuffer.wrap(imageBits.get(48, 80).toByteArray()).position(0))
                .getInt() & 0x1FFFFFFF;
    }

    private byte[] getFileData() {
        return imageBits.get(Config.HEADER_LENGTH * 8, length).toByteArray();
    }

    private boolean isContainsSecret() {
        return Arrays.equals(Config.SECRET_TAG, imageBits.get(0, 48).toByteArray());
    }
}

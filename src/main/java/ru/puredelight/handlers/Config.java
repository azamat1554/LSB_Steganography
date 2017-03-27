package ru.puredelight.handlers;

/**
 * В этом классе находятся конфигурационные параметры
 */
public class Config {
    /**
     * Секретная метка, которая довавляется в изображение, чтобы указать на секретное содержимое в файле
     */
    public static final byte[] SECRET_TAG = "secret".getBytes();
    /**
     * Длина заголовка в байтах
     */
    public static final int HEADER_LENGTH = 10;
}

package ru.puredelight.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFilter extends FileFilter {
    //Accept all directories and all gif, bmp, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else if (Utilities.supportImageAsContainer(f)) {
            return true;
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "BMP, PNG, GIF";
    }
}
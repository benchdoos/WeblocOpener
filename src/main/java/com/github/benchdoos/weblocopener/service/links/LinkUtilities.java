package com.github.benchdoos.weblocopener.service.links;

import com.github.benchdoos.linksupport.links.Link;
import com.github.benchdoos.weblocopener.utils.FileUtils;

import java.io.File;

public class LinkUtilities {

    public static boolean isFileSupported(String extension) {
        final Link byExtension = Link.getByExtension(extension);
        return byExtension != null;
    }

    public static Link getByFilePath(String filepath) {
        final String fileExtension = FileUtils.getFileExtension(new File(filepath));
        return Link.getByExtension(fileExtension);
    }
}

package com.github.benchdoos.weblocopener.base.utils;

import java.net.URL;

public class FileUtils {

    public URL getResourceURI(final String resourcePath) {
        final ClassLoader classLoader = FileUtils.class.getClassLoader();
        return classLoader.getResource(resourcePath);
    }

}

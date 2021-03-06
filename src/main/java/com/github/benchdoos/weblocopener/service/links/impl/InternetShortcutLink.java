/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.service.links.impl;

import com.github.benchdoos.weblocopener.service.links.Link;
import com.github.benchdoos.weblocopener.service.links.LinkUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Link for Windows {@code .url} file
 */
public class InternetShortcutLink implements Link {
    /**
     * Create an Internet shortcut
     *
     * @param file location of the shortcut
     * @param url  URL
     * @throws IOException if can not write a file
     */
    public void createLink(final File file, final URL url) throws IOException {
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("[InternetShortcut]\n");
        fileWriter.write("URL=" + url.toString() + "\n");
        fileWriter.flush();
        fileWriter.close();
    }

    public URL getUrl(final File file) throws IOException {
        return LinkUtils.getUrl(file);
    }

    @Override
    public String getName() {
        return ".url";
    }

    @Override
    public String getExtension() {
        return "url";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InternetShortcutLink;
    }
}

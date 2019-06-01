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

package com.github.benchdoos.weblocopener.service.links;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Link for Linux {@code .desktop} file
 */
public class DesktopEntryLink implements Link {
    @Override
    public void createLink(File file, URL url) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("[Desktop Entry]\n");
        writer.write("Encoding=" + ApplicationConstants.DEFAULT_APPLICATION_CHARSET + "\n");
        writer.write("Name=" + file.getName() + "\n");
        writer.write("URL=" + url.toString() + "\n");
        writer.write("Type=Link" + "\n");
        writer.write("Icon=text-html" + "\n");
        writer.flush();
        writer.close();
    }

    @Override
    public URL getUrl(File file) throws IOException {
        return LinkUtils.getUrl(file);
    }

    @Override
    public String getName() {
        return ".desktop";
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof DesktopEntryLink;
    }
}

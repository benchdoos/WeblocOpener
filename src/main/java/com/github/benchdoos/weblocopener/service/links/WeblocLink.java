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

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

/**
 * Link for MacOS {@code .webloc} file
 */
public class WeblocLink implements Link {
    @Override
    public void createLink(File file, URL url) throws IOException {
        NSDictionary root = new NSDictionary();
        root.put("URL", url.toString());
        PropertyListParser.saveAsXML(root, file);
    }

    @Override
    public URL getUrl(File file) throws IOException {
        try {
            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
            return new URL(rootDict.objectForKey("URL").toString());
        } catch (PropertyListFormatException | ParseException | ParserConfigurationException | SAXException e) {
            throw new IOException("Could not parse file: " + file, e);
        }
    }
}

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
import com.github.benchdoos.weblocopener.core.Translation;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

public class BinaryWeblocLink implements Link {
    @Override
    public void createLink(final File file, final URL url) throws IOException {
        final NSDictionary root = new NSDictionary();
        root.put("URL", url.toString());
        PropertyListParser.saveAsBinary(root, file);
    }

    @Override
    public URL getUrl(final File file) throws IOException {
        try {
            final NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
            return new URL(rootDict.objectForKey("URL").toString());
        } catch (PropertyListFormatException | ParseException | ParserConfigurationException | SAXException e) {
            throw new IOException("Could not parse file: " + file, e);
        }
    }

    @Override
    public String getName() {
        return Translation.getTranslatedString("CommonsBundle", "binaryWeblocLinkName");
    }

    @Override
    public String getExtension() {
        return "webloc";
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof BinaryWeblocLink;
    }

}

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
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.service.links.impl.BinaryWeblocLink;
import com.github.benchdoos.weblocopener.service.links.impl.DesktopEntryLink;
import com.github.benchdoos.weblocopener.service.links.impl.InternetShortcutLink;
import com.github.benchdoos.weblocopener.service.links.impl.WeblocLink;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkFactory {
    public static Link getLinkByName(final String name) {
        switch (LinkType.valueOf(name)) {
            case webloc:
                return new WeblocLink();
            case url:
                return new InternetShortcutLink();
            case desktop:
                return new DesktopEntryLink();
            case binary_webloc:
                return new BinaryWeblocLink();
            default:
                return getLinkByName(SettingsConstants.URL_PROCESSOR.toString());
        }
    }

    public static String getNameByLink(final Link link) {
        final Class<? extends Link> aClass = link.getClass();
        if (aClass == WeblocLink.class) {
            return LinkType.webloc.toString();
        } else if (aClass == BinaryWeblocLink.class) {
            return LinkType.binary_webloc.toString();
        } else if (aClass == InternetShortcutLink.class) {
            return LinkType.url.toString();
        } else if (aClass == DesktopEntryLink.class) {
            return LinkType.desktop.toString();
        } else
            throw new IllegalArgumentException(
                    "Provided link [" + aClass + "] does not much to supported: " + Arrays.toString(LinkType.values()));

    }

    public static List<Link> getSupportedLinks() {
        final ArrayList<Link> result = new ArrayList<>();
        for (LinkType type : LinkType.values()) {
            final Link linkByName = getLinkByName(type.toString());
            result.add(linkByName);
        }
        return result;
    }

    public Link getLink(final File file) {
        final String originalExtension = FilenameUtils.getExtension(file.getName());
        return getAbstractLink(originalExtension);
    }

    private Link getAbstractLink(final String originalExtension) {
        switch (originalExtension) {
            case ApplicationConstants.WEBLOC_FILE_EXTENSION:
                return new WeblocLink();
            case ApplicationConstants.URL_FILE_EXTENSION:
                return new InternetShortcutLink();
            case ApplicationConstants.DESKTOP_FILE_EXTENSION:
                return new DesktopEntryLink();
            default:
                return null;
        }
    }

    public Link getLink(final String extension) {
        return getAbstractLink(extension);
    }

    public enum LinkType {binary_webloc, webloc, url, desktop}
}

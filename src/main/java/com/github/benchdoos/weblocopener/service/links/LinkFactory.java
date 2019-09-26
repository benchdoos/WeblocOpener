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

import com.github.benchdoos.linksupport.links.Link;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LinkFactory {
    public static Link getLinkByName(final String name) {
        switch (LinkType.valueOf(name)) {
            case webloc:
            case binary_webloc:
                return com.github.benchdoos.linksupport.links.Link.WEBLOC_LINK;
            case url:
                return com.github.benchdoos.linksupport.links.Link.INTERNET_SHORTCUT_LINK;
            case desktop:
                return com.github.benchdoos.linksupport.links.Link.DESKTOP_LINK;
            default:
                return getLinkByName(SettingsConstants.URL_PROCESSOR.toString());
        }
    }

    public static String getNameByLink(final Link link) {
        switch (link) {
            case WEBLOC_LINK:
                return LinkType.binary_webloc.toString();
            case DESKTOP_LINK:
                return LinkType.desktop.toString();
            case INTERNET_SHORTCUT_LINK:
                return LinkType.url.toString();
            default:
                throw new IllegalArgumentException(
                        "Provided link [" + link + "] does not much to supported: " + Arrays.toString(Link.values()));

        }
    }

    public static List<Link> getSupportedLinks() {
        return Arrays.stream(Link.values()).collect(Collectors.toList());
    }

    public enum LinkType {binary_webloc, webloc, url, desktop}
}

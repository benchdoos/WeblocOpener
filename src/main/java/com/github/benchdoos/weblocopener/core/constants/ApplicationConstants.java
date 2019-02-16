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

package com.github.benchdoos.weblocopener.core.constants;

import com.github.benchdoos.jcolorful.beans.Theme;
import com.github.benchdoos.jcolorful.core.JColorful;

public interface ApplicationConstants {
    String WEBLOCOPENER_APPLICATION_NAME = "WeblocOpener";
    String UPDATER_APPLICATION_NAME = "Updater";

    String WEBLOC_FILE_EXTENSION = "webloc";

    String URL_FILE_EXTENSION = "url";

    String DESKTOP_FILE_EXTENSION = "desktop";

    String DEFAULT_APPLICATION_CHARSET = "UTF-8";

    Theme DARK_MODE_THEME = JColorful.EXTREMELY_BLACK;
}

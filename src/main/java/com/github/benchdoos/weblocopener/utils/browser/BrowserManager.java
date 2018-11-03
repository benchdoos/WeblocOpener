/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
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

package com.github.benchdoos.weblocopener.utils.browser;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.core.constants.SettingsConstants;
import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class BrowserManager {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());


    private static ArrayList<Browser> browserList = new ArrayList<>();

    private static String defaultBrowserName = "Default";


    public static void loadBrowserList() {
        initTranslation();
        loadBrowsersFromDefault(generateDefaultBrowserArrayList());
    }

    private static void loadBrowsersFromDefault(ArrayList<Browser> list) {

        browserList = list;
        browserList.add(0, new Browser(defaultBrowserName, SettingsConstants.BROWSER_DEFAULT_VALUE));
        log.debug("Browsers count: " + browserList.size() + " " + browserList);

    }

    public static ArrayList<Browser> getBrowserList() {
        return browserList;
    }


    @SuppressWarnings("SpellCheckingInspection")
    private static ArrayList<Browser> generateDefaultBrowserArrayList() {
        ArrayList<Browser> result = new ArrayList<>();

        Browser chrome = new Browser();
        chrome.setName("Google Chrome");
        final String call = "start chrome " + "\"" + "%site" + "\"";
        chrome.setCall(call);
        chrome.setIncognitoCall(call + " --incognito");
        result.add(chrome);

        Browser firefox = new Browser();
        firefox.setName("Firefox");
        firefox.setCall("start firefox " + "\"" + "%site" + "\"");
        firefox.setIncognitoCall("start firefox -private-window " + "\"" + "%site" + "\"");
        result.add(firefox);

        Browser edge = new Browser();
        edge.setName("Microsoft Edge");
        edge.setCall("start microsoft-edge:" + "\"" + "%site" + "\"");
        edge.setIncognitoCall("start shell:AppsFolder\\Microsoft.MicrosoftEdge_8wekyb3d8bbwe!MicrosoftEdge -private " + "%site");
        result.add(edge);

        Browser iexplorer = new Browser();
        iexplorer.setName("Internet Explorer");
        iexplorer.setCall("start iexplore " + "\"" + "%site" + "\"");
        iexplorer.setIncognitoCall("start iexplore " + "\"" + "%site" + "\"" + " -private");
        result.add(iexplorer);

        Browser opera = new Browser();
        opera.setName("Opera");
        opera.setCall("start opera " + "\"" + "%site" + "\"");
        opera.setIncognitoCall("start opera --private " + "\"" + "%site" + "\"");
        result.add(opera);

        Browser yandex = new Browser();
        yandex.setName("Yandex Browser");
        yandex.setCall("start browser " + "\"" + "%site" + "\"");
        yandex.setIncognitoCall("start browser -incognito " + "\"" + "%site" + "\"");
        result.add(yandex);

        Browser vivaldi = new Browser();
        vivaldi.setName("Vivaldi");
        vivaldi.setCall("start vivaldi " + "\"" + "%site" + "\"");
        vivaldi.setIncognitoCall("start vivaldi -incognito " + "\"" + "%site" + "\"");
        result.add(vivaldi);


        return result;
    }

    private static void initTranslation() {
        Translation translation = new Translation("translations/CommonsBundle") {
            @Override
            public void initTranslations() {
                defaultBrowserName = messages.getString("defaultBrowserName");
            }
        };
        translation.initTranslations();
    }
}

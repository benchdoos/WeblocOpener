package com.doos.commons.utils.browser;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.doos.commons.core.ApplicationConstants;
import com.doos.commons.core.Translation;
import com.doos.commons.utils.Logging;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class BrowserManager {
    private static final Logger log = Logger.getLogger(Logging.getCurrentClassName());

    private static final String FILE_LIST_NAME = "browser_list.plist";
    private static final String DEFAULT_LIST_LOCATION = System.getProperty("java.io.tmpdir")
            + ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME + File.separator + FILE_LIST_NAME;
    private static final String FIELD_NAME_LIST = "list";
    private static final String FIELD_NAME_BROWSER = "browser";
    private static final String FIELD_NAME_CALL = "call";
    private static final String FIELD_NAME_PRIVATE_CALL = "private-call";
    private static NSArray plist = new NSArray();
    private static ArrayList<Browser> browserList = new ArrayList<>();

    private static ArrayList<Browser> DEFAULT_BROWSERS_LIST = new ArrayList<>();
    private static String defaultBrowserName = "Default";
    private static Translation translation;


    public static void loadBrowserList() {
        initTranslation();
        File file = new File(DEFAULT_LIST_LOCATION);
        if (file.exists()) {
            parsePlist(file);
            browserList = plistToArrayList(plist);
            browserList.add(0, new Browser(defaultBrowserName, ApplicationConstants.BROWSER_DEFAULT_VALUE));
            System.out.println("count: " + browserList.size() + " " + browserList);
        } else {
            reloadBrowserList(generateDefaultBrowserArrayList());
        }
    }

    public static ArrayList<Browser> getBrowserList() {
        return browserList;
    }

    public static boolean isDefaultBrowser(String call) {
        boolean result = false;
        ArrayList<Browser> defaultBrowserList = generateDefaultBrowserArrayList();
        for (Browser browser : defaultBrowserList) {

            if (browser.getCall() != null) {
                if (browser.getCall().equals(call)) {
                    result = true;
                }
            }
            if (browser.getIncognitoCall() != null) {
                if (browser.getIncognitoCall().equals(call)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private static ArrayList<Browser> plistToArrayList(NSArray plist) {
        ArrayList<Browser> result = new ArrayList<>();
        for (int i = 0; i < plist.count(); i++) {
            try {
                NSDictionary dictionary = (NSDictionary) plist.objectAtIndex(i);
                final String name = dictionary.objectForKey(FIELD_NAME_BROWSER).toString();
                final String call = dictionary.objectForKey(FIELD_NAME_CALL).toString();
                Browser browser = new Browser();
                browser.setName(name);
                browser.setCall(call);

                try {
                    final String incognito = dictionary.objectForKey(FIELD_NAME_PRIVATE_CALL).toString();
                    browser.setIncognitoCall(incognito);
                } catch (NullPointerException e) {/*NOP*/}


                result.add(browser);
            } catch (NullPointerException e) {
                log.warn("Can not read browser, index:" + i);
            }
        }
        return result;
    }

    private static void parsePlist(File file) {
        try {
            plist = (NSArray) PropertyListParser.parse(file);
        } catch (IOException e) {
            log.warn("Can not read file to parse: " + file, e);
        } catch (PropertyListFormatException | ParseException | SAXException | ParserConfigurationException e) {
            log.warn("Can not parse file: " + file, e);
        }
    }

    static void reloadBrowserList(ArrayList<Browser> browserList) {

        NSArray root = new NSArray(browserList.size());
        for (int i = 0; i < browserList.size(); i++) {
            NSDictionary browser = new NSDictionary();
            browser.put(FIELD_NAME_BROWSER, browserList.get(i).getName());
            browser.put(FIELD_NAME_CALL, browserList.get(i).getCall());
            browser.put(FIELD_NAME_PRIVATE_CALL, browserList.get(i).getIncognitoCall());
            root.setValue(i, browser);
        }

        try {
            System.out.println("FL_: " + DEFAULT_LIST_LOCATION);
            File file = new File(DEFAULT_LIST_LOCATION);
            PropertyListParser.saveAsXML(root, file);
        } catch (IOException e) {
            log.warn("Can not create .webloc file", e);
        }
    }

    static ArrayList<Browser> generateDefaultBrowserArrayList() {
        ArrayList<Browser> result = new ArrayList<>();

        //Chrome
        //HKEY_LOCAL_MACHINE\SOFTWARE\Classes\ChromeHTML\shell\open\command
        //https://stackoverflow.com/questions/14348840/opening-chrome-from-command-line //start chrome "site1.com"

        //HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall
        Browser chrome = new Browser();
        chrome.setName("Google Chrome");
        final String call = "start chrome " + "\"" + "%site" + "\"";
        chrome.setCall(call);
        chrome.setIncognitoCall(call + " --incognito");
        result.add(chrome);

        Browser opera = new Browser();
        opera.setName("Opera");
        opera.setCall("start opera " + "\"" + "%site" + "\"");
        opera.setIncognitoCall("start opera --private " + "\"" + "%site" + "\"");
        result.add(opera);

        Browser edge = new Browser();
        edge.setName("Microsoft Edge");
        edge.setCall("start microsoft-edge:" + "\"" + "%site" + "\"");
        result.add(edge);

        Browser firefox = new Browser();
        firefox.setName("Firefox");
        firefox.setCall("start firefox " + "\"" + "%site" + "\"");
        firefox.setIncognitoCall("start firefox -private-window " + "\"" + "%site" + "\"");
        result.add(firefox);

        return result;
    }

    private static void initTranslation() {
        translation = new Translation("translations/CommonsBundle") {
            @Override
            public void initTranslations() {
                defaultBrowserName = messages.getString("defaultBrowserName");
            }
        };
        translation.initTranslations();
    }
}

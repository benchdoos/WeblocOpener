package com.doos;

import com.doos.Service.Analyzer;
import com.doos.Service.Logging;
import com.doos.Service.UrlsProceed;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        new Logging();
        ArrayList<String> urls = new Analyzer(args).getUrls();
        UrlsProceed.openUrls(urls);
        UrlsProceed.shutdownLogout();
    }


}

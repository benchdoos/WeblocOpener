package com.doos.service;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.doos.gui.EditDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.doos.service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Analyzer {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    ArrayList<String> urls = new ArrayList<>();

    public Analyzer(String[] args) {
        log.debug("Starting analyze");
        log.debug("Got arguments: " + Arrays.toString(args));

        ArrayList<File> file = getFile(args);
        if (args.length > 1) {
            urls = takeUrls(file);
        } else if (args.length == 1) {
            String url = "";
            try {
                url = takeUrl(file.get(0));
                if (url.equals("")) {
                    throw new NullPointerException("Url is empty, just editing");
                }
                ArrayList<String> ss = new ArrayList<>();
                ss.add(url);
                urls = ss;
            } catch (NullPointerException e) {
                log.warn("URL in file [" + file.get(0) + "] has empty link.", e);
                new EditDialog(file.get(0).getAbsolutePath()).setVisible(true);
            } catch (Exception e) {
                log.warn("URL in file [" + file.get(0) + "] is corrupt.", e);
                JOptionPane.showMessageDialog(new Frame(), "URL in file [" + file.get(0) + "] is corrupt.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                new EditDialog(file.get(0).getAbsolutePath()).setVisible(true);
            }
        }

    }

    public static String takeUrl(File file) throws Exception {

        log.debug("Got file: " + file.getAbsolutePath());
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
        String url = rootDict.objectForKey("URL").toString();
        log.info("Got url: " + url);

        return url;
    }

    private ArrayList<File> getFile(String[] args) {
        ArrayList<File> list = new ArrayList<>();
        if (args.length > 0) {
            log.debug("Arguments count: " + args.length);
            for (String arg : args) {
                File currentFile = new File(arg);
                if (currentFile.isFile() && currentFile.exists()) {
                    if (getFileExtension(currentFile).equals("webloc")) {
                        list.add(currentFile);
                        log.info("File added to proceed: " + currentFile.getAbsolutePath());
                    } else {
                        log.warn("Wrong argument. File extension is not webloc: " + currentFile.getAbsolutePath());
                    }
                } else {
                    log.warn("Wrong argument. Invalid file path or file not exist: " + currentFile.getAbsolutePath());
                }
            }
        }
        log.info("Files added to proceed: " + list.size());
        return list;
    }

    private ArrayList<String> takeUrls(ArrayList<File> files) {
        ArrayList<String> urls = new ArrayList<>();
        for (File file : files) {
            try {
                log.debug("Got file: " + file.getAbsolutePath());
                NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(file);
                String url = rootDict.objectForKey("URL").toString();
                log.debug("Got url: " + url);
                urls.add(url);
            } catch (Exception ex) {
                log.warn("Can not take url from: " + file.getAbsolutePath());
                new EditDialog(file.getAbsolutePath()).setVisible(true);
            }
        }
        log.info("Total url count: " + urls.size());
        return urls;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}

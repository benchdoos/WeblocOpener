package com.doos.Service;

import com.dd.plist.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

import static com.doos.Service.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 30.10.2016.
 */
public class Analyzer {
    private static final Logger log = Logger.getLogger(getCurrentClassName());

    ArrayList<String> urls = new ArrayList<>();

    public Analyzer(String[] args) {
        log.debug("Starting analyze");

        ArrayList<File> file = getFile(args);

        urls = takeUrls(file);
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
                ex.printStackTrace();
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

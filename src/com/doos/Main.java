package com.doos;

import com.doos.gui.EditDialog;
import com.doos.service.Analyzer;
import com.doos.service.Logging;
import com.doos.service.UrlsProceed;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {
    public static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        new Logging();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (args.length > 0) {
            if (!args[0].isEmpty()) {
                switch (args[0]) {
                    case "-edit":
                        if (args.length > 1) {
                            new EditDialog(args[1]).setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(new Frame(), "Argument '-edit' should have " +
                                            "location path parameter.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(-1);
                        }
                        break;
                    default:
                        ArrayList<String> urls = new Analyzer(args).getUrls();
                        UrlsProceed.openUrls(urls);
                        UrlsProceed.shutdownLogout();
                        break;
                }
            }
        }
    }


}

package com.doos;

import com.doos.gui.EditDialog;
import com.doos.service.Analyzer;
import com.doos.service.Logging;
import com.doos.service.UrlsProceed;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        new Logging();

        enableLookAndFeel();

        manageArguments(args);
    }

    /**
     * Manages incoming arguments
     *
     * @param args
     */
    private static void manageArguments(String[] args) {
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
                        String url = new Analyzer(args[0]).getUrl();
                        UrlsProceed.openUrl(url);
                        UrlsProceed.shutdownLogout();
                        break;
                }
            }
        }
    }

    /**
     * Enables LookAndFeel for current OS.
     *
     * @see javax.swing.UIManager.LookAndFeelInfo
     */
    private static void enableLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

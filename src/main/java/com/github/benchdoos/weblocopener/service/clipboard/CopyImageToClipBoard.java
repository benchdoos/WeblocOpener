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

package com.github.benchdoos.weblocopener.service.clipboard;

import com.github.benchdoos.weblocopener.utils.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;

public class CopyImageToClipBoard implements ClipboardOwner {
    private static final Logger log = LogManager.getLogger(Logging.getCurrentClassName());

    CopyImageToClipBoard() {
        try {
            Robot robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screen = new Rectangle(screenSize);
            BufferedImage i = robot.createScreenCapture(screen);
            TransferableImage trans = new TransferableImage(i);
            java.awt.datatransfer.Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents(trans, this);
        } catch (AWTException e) {
            log.warn("Can not copy image to clipboard", e);
        }
    }

    void copyImage(BufferedImage bi) {
        TransferableImage trans = new TransferableImage(bi);
        java.awt.datatransfer.Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(trans, this);
    }

    public void lostOwnership(Clipboard clip, Transferable trans) {
        log.warn("Lost Clipboard Ownership");
    }

    private class TransferableImage implements Transferable {

        Image i;

        TransferableImage(Image i) {
            this.i = i;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor flavor1 : flavors) {
                if (flavor.equals(flavor1)) {
                    return true;
                }
            }
            return false;
        }
    }
}

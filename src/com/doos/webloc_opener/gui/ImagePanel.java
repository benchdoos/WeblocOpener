package com.doos.webloc_opener.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Eugene Zrazhevsky on 04.12.2016.
 */

public class ImagePanel extends JPanel {

    private java.awt.Image img;

    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
    }

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

}
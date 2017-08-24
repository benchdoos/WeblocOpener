package com.doos.commons.utils.browser;

import org.junit.Test;

/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class BrowserManagerTest {
    @Test
    public void loadBrowserList() throws Exception {
        BrowserManager.loadBrowserList();
    }

    @Test
    public void plistToArrayList() throws Exception {
    }

    @Test
    public void reloadBrowserList() throws Exception {
        BrowserManager.reloadBrowserList(BrowserManager.generateDefaultBrowserArrayList());
    }

}
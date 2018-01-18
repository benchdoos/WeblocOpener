/*
 * Copyright 2018 Eugeny Zrazhevsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.benchdoos.weblocopener.commons.utils.browser;

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
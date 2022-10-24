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

package com.github.benchdoos.weblocopener.update;

import com.github.benchdoos.weblocopenercore.domain.version.ApplicationVersion;

import java.io.IOException;

public interface Updater {
    String WINDOWS_SETUP_DEFAULT_NAME = "WeblocOpenerSetup.exe";
    String DEBIAN_SETUP_DEFAULT_NAME = "WeblocOpener.deb";
    int CONNECTION_TIMEOUT = 5000;

    /**
     * @return latest {@link ApplicationVersion}, giving Beta if
     * {@link com.github.benchdoos.weblocopenercore.service.settings.impl.InstallBetaUpdateSettings} is {@code true}
     * and Release is older then Beta version,
     * otherwise  will return Release version.
     */
    ApplicationVersion getLatestAppVersion();

    /**
     * @return latest Release {@link ApplicationVersion}
     */
    ApplicationVersion getLatestReleaseAppVersion();

    /**
     * @return latest Beta version {@link ApplicationVersion}
     */
    ApplicationVersion getLatestBetaAppVersion();

    void startUpdate(ApplicationVersion applicationVersion) throws IOException;
}

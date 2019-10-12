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

package com.github.benchdoos.weblocopener.nongui.notify.impl;

import com.github.benchdoos.weblocopener.core.Translation;
import com.github.benchdoos.weblocopener.nongui.notify.Notifier;
import com.github.benchdoos.weblocopener.utils.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.utils.version.ApplicationVersion;

public class UnixNotifier implements Notifier {
    @Override
    public void notifyUser(ApplicationVersion serverVersion) {
        Translation translation = new Translation("UpdateDialogBundle");
        final String windowTitle = translation.getTranslatedString("windowTitle");
        final String windowMessage = translation.getTranslatedString("newVersionAvailableTrayNotification")
                + ": " + serverVersion.getVersion();

        NotificationManager.getNotificationForCurrentOS().showInfoNotification(windowTitle, windowMessage);
    }
}

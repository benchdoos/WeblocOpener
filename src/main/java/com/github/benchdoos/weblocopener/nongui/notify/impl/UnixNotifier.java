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

import com.github.benchdoos.weblocopener.nongui.notify.Notifier;
import com.github.benchdoos.weblocopenercore.domain.version.AppVersion;
import com.github.benchdoos.weblocopenercore.service.notification.NotificationManager;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;

public class UnixNotifier implements Notifier {
    @Override
    public void notifyUser(AppVersion serverVersion) {
        Translation translation = new Translation("UpdateDialogBundle");
        final String windowTitle = translation.get("windowTitle");
        final String windowMessage = translation.get("newVersionAvailableTrayNotification")
                + ": " + serverVersion.version();

        NotificationManager.getNotificationForCurrentOS().showInfoNotification(windowTitle, windowMessage);
    }
}

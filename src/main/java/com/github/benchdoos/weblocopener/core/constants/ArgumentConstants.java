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

package com.github.benchdoos.weblocopener.core.constants;

public interface ArgumentConstants {
    String OPENER_OPEN_ARGUMENT = "-open"; //don't use it, this is for unix

    String OPENER_CREATE_ARGUMENT = "-create";
    String OPENER_EDIT_ARGUMENT = "-edit";
    String OPENER_SETTINGS_ARGUMENT = "-settings";
    String OPENER_UPDATE_ARGUMENT = "-update";
    String OPENER_ABOUT_ARGUMENT = "-about";
    String OPENER_HELP_ARGUMENT_HYPHEN = "-help";
    String OPENER_QR_ARGUMENT = "-qr";
    String OPENER_COPY_LINK_ARGUMENT = "-copy";
    String OPENER_COPY_QR_ARGUMENT = "-copy-qr";

    String UPDATE_SILENT_ARGUMENT = "-update-silent";

    String INSTALLER_SILENT_KEY = " /VERYSILENT";

}

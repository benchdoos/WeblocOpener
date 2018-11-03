/*
 * (C) Copyright 2018.  Eugene Zrazhevsky and others.
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

public interface ApplicationConstants {
    String WEBLOC_OPENER_APPLICATION_NAME = "WeblocOpener";
    String UPDATER_APPLICATION_NAME = "Updater";

    String WINDOWS_WEBLOC_OPENER_SETUP_NAME = "WeblocOpenerSetup";

    String WEBLOC_FILE_EXTENSION = "webloc";


    /*todo move away*/
    int UPDATE_CODE_SUCCESS = 0; //NORMAL state, app updated.
    int UPDATE_CODE_CANCEL = 1; //Install was cancelled or Incorrect function or corrupt file.
    int UPDATE_CODE_NO_FILE = 2; //The system cannot find the file specified. OR! User gave no permissions.
    int UPDATE_CODE_CORRUPT = 193; //Installation file is corrupt.
    int UPDATE_CODE_INTERRUPT = -999; //Downloading/installation was interrupted by user.
}

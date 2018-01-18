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

package com.github.benchdoos.weblocopener.commons.utils;

import java.awt.*;

/**
 * Created by Eugene Zrazhevsky on 07.12.2016.
 */
public interface MessagePushable {
    int DEFAULT_TIMER_DELAY = 7 * 1000;

    int ERROR_MESSAGE = -1; //red
    int INFO_MESSAGE = 0; //blue
    int SUCCESS_MESSAGE = 1; //green
    int WARNING_MESSAGE = -2; //orange

    Color INFO_MESSAGE_COLOR = Color.decode("#0059E8");
    Color ERROR_MESSAGE_COLOR = Color.decode("#FF0000");
    Color WARNING_MESSAGE_COLOR = Color.decode("#FF8D00");
    Color SUCCESS_MESSAGE_COLOR = Color.decode("#00D900");

    static Color getMessageColor(int messageValue) {
        switch (messageValue) {
            case INFO_MESSAGE:
                return INFO_MESSAGE_COLOR;
            case ERROR_MESSAGE:
                return ERROR_MESSAGE_COLOR;
            case WARNING_MESSAGE:
                return WARNING_MESSAGE_COLOR;
            case SUCCESS_MESSAGE:
                return SUCCESS_MESSAGE_COLOR;
            default:
                return INFO_MESSAGE_COLOR;
        }
    }

    void showMessage(String message, int messageValue);
}

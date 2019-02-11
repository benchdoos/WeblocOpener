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

package com.github.benchdoos.weblocopener.utils;

import com.github.benchdoos.weblocopener.core.constants.ApplicationConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConnectionUtils {
    public static HttpsURLConnection getNeededHttpsURLConnection(HttpsURLConnection connection) {
        if (!connection.getDoOutput()) {
            connection.setDoOutput(true);
        }
        if (!connection.getDoInput()) {
            connection.setDoInput(true);
        }
        return connection;
    }

    public static JsonElement getJsonRootElementFromConnection(HttpsURLConnection connection) throws IOException {
        String input;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), ApplicationConstants.DEFAULT_APPLICATION_CHARSET));

        input = bufferedReader.readLine();

        JsonParser parser = new JsonParser();
        return parser.parse(input);
    }
}

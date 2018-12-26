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

package com.github.benchdoos.weblocopener.utils.browser;

import com.google.gson.*;

import java.lang.reflect.Type;

class BrowserDeserializer implements JsonDeserializer<Browser> {
    @Override
    public Browser deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        final JsonObject browserObject = object.getAsJsonObject("browser");
        Browser browser = new Browser();
        browser.setName(browserObject.get("name").getAsString());
        browser.setCall(browserObject.get("call").getAsString());
        final JsonElement incognito = browserObject.get("incognito");
        if (incognito != null) {
            browser.setIncognitoCall(incognito.getAsString());
        }
        return browser;
    }
}

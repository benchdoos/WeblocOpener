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


/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class Browser {
    private String name;
    private String call;
    private String incognitoCall;

    public Browser(String name, String call) {
        this.name = name;
        this.call = call;
    }

    Browser() {/*NOP*/}

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getIncognitoCall() {
        return incognitoCall;
    }

    void setIncognitoCall(String incognitoCall) {
        this.incognitoCall = incognitoCall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Browser{" + "name='" + name + '\'' + '}';
    }
}

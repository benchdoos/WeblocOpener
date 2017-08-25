package com.doos.commons.utils.browser;

import com.sun.istack.internal.NotNull;

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

    public Browser() {/*NOP*/}

    public String getBrowserInfo() {
        return "Browser: " + name + " calls:[" + call + "]";
    }

    @NotNull
    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getIncognitoCall() {
        return incognitoCall;
    }

    public void setIncognitoCall(String incognitoCall) {
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
        return name;
    }
}

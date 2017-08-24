package com.doos.commons.utils.browser;

/**
 * Created by Eugene Zrazhevsky on 24.08.2017.
 */
public class Browser {
    private String name;
    private String call;

    public Browser(String name, String call) {
        this.name = name;
        this.call = call;
    }

    public Browser() {/*NOP*/}

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Browser: " + name + " calls:[" + call + "]";
    }
}

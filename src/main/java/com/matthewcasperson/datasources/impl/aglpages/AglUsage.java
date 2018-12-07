package com.matthewcasperson.datasources.impl.aglpages;

import com.matthewcasperson.AutomatedBrowser;

public class AglUsage {
    private static final String NET_POSITION = "/html/body/div[2]/aside/div[1]/table/tfoot/tr[2]/td[2]";
    private final AutomatedBrowser automatedBrowser;

    public AglUsage(final AutomatedBrowser automatedBrowser) {
        this.automatedBrowser = automatedBrowser;
    }

    public String getNetPosition() {
        return automatedBrowser.getTextFromElement(NET_POSITION);
    }
}

package com.matthewcasperson.datasources.impl.aglpages;

import com.matthewcasperson.AutomatedBrowser;

public class AglDashboard {
    private static final String USAGE_TAB = "/html/body/agl-apps/agl-account-app/agl-app-header/header/agl-desktop-menu/div/div/div/div[2]/ul/li[2]/a/span/agl-menu-item/span";
    private static final String USAGE_BUTTON = "/html/body/agl-apps/agl-account-app/div[3]/agl-account-usage/div/div[2]/div[1]/div/mat-card/div[6]/button";
    private final AutomatedBrowser automatedBrowser;

    public AglDashboard(final AutomatedBrowser automatedBrowser) {
        this.automatedBrowser = automatedBrowser;
    }

    public AglUsage openUsage() {
        automatedBrowser.clickElement(USAGE_TAB);
        automatedBrowser.clickElement(USAGE_BUTTON);
        return new AglUsage(automatedBrowser);
    }
}

package com.matthewcasperson.datasources.impl.aglpages;

import com.matthewcasperson.AutomatedBrowser;
import com.matthewcasperson.utils.ConfigurationUtils;
import com.matthewcasperson.utils.impl.ConfigurationUtilsImpl;

public class AglLogin {
    private static final ConfigurationUtils CONFIGURATION_UTILS = new ConfigurationUtilsImpl();
    private static final String AGL_EMAIL = "AGL_EMAIL";
    private static final String AGL_PASSWORD = "AGL_PASSWORD";
    private static final String URL = "https://www.agl.com.au/help/managing-my-account";
    private static final String LOGIN_LINK = "/html/body/div[1]/main/div[1]/div/div[2]/div[4]/a[1]";
    private static final String EMAIL_FIELD = "/html/body/login-app/login-component/div/div/div[2]/login-identifier/form/div[2]/input";
    private static final String NEXT_BUTTON = "/html/body/login-app/login-component/div/div/div[2]/login-identifier/form/div[3]/button";
    private static final String PASSWORD_FIELD = "/html/body/login-app/login-component/div/div/div[2]/login-password/form/div[2]/input";
    private static final String LOGIN_BUTTON = "/html/body/login-app/login-component/div/div/div[2]/login-password/form/div[3]/button";

    private final AutomatedBrowser automatedBrowser;

    public AglLogin(final AutomatedBrowser automatedBrowser) {
        this.automatedBrowser = automatedBrowser;
    }

    public AglDashboard login() {
        automatedBrowser.setDefaultExplicitWaitTime(120);
        automatedBrowser.goTo(URL);
        automatedBrowser.clickElement(LOGIN_LINK);
        automatedBrowser.populateElement(EMAIL_FIELD, CONFIGURATION_UTILS.getConfigValue(AGL_EMAIL));
        automatedBrowser.clickElement(NEXT_BUTTON);
        automatedBrowser.populateElement(PASSWORD_FIELD, CONFIGURATION_UTILS.getConfigValue(AGL_PASSWORD));
        automatedBrowser.clickElement(LOGIN_BUTTON);
        return new AglDashboard(automatedBrowser);
    }
}

package com.matthewcasperson.displays;

import com.matthewcasperson.AutomatedBrowser;
import com.matthewcasperson.AutomatedBrowserFactory;
import com.matthewcasperson.datasources.impl.aglpages.AglLogin;
import org.junit.Assert;
import org.junit.Test;

public class AglLoginTest {
    private static final AutomatedBrowserFactory AUTOMATED_BROWSER_FACTORY = new AutomatedBrowserFactory();

    @Test
    public void getPosition() {
        final AutomatedBrowser automatedBrowser = AUTOMATED_BROWSER_FACTORY.getAutomatedBrowser("ChromeNoImplicitWait");
        automatedBrowser.init();
        final AglLogin aglLogin = new AglLogin(automatedBrowser);
        final String position = aglLogin
                .login()
                .openUsage()
                .getNetPosition();
        Assert.assertTrue(position != null);
    }
}

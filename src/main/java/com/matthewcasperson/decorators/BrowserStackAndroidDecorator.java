package com.matthewcasperson.decorators;

import com.matthewcasperson.AutomatedBrowser;
import com.matthewcasperson.decoratorbase.AutomatedBrowserBase;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BrowserStackAndroidDecorator extends AutomatedBrowserBase {
    public BrowserStackAndroidDecorator(final AutomatedBrowser automatedBrowser) {

        super(automatedBrowser);

    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {

        final DesiredCapabilities caps = getAutomatedBrowser().getDesiredCapabilities();

        caps.setCapability("os_version", "7.1");
        caps.setCapability("device", "Samsung Galaxy Note 8");
        caps.setCapability("real_mobile", "true");
        caps.setCapability("browserstack.local", "false");

        return caps;
    }

    @Override
    public void maximizeWindow() {
        // do nothing
    }
}
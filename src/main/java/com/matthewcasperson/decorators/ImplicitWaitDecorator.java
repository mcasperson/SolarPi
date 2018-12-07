package com.matthewcasperson.decorators;

import com.matthewcasperson.AutomatedBrowser;
import com.matthewcasperson.decoratorbase.AutomatedBrowserBase;
import java.util.concurrent.TimeUnit;

public class ImplicitWaitDecorator extends AutomatedBrowserBase {

    private final int waitTime;

    public ImplicitWaitDecorator(final int waitTime, final AutomatedBrowser automatedBrowser) {

        super(automatedBrowser);
        this.waitTime = waitTime;
    }

    @Override
    public void init() {
        getAutomatedBrowser()
                .getWebDriver()
                .manage()
                .timeouts()
                .implicitlyWait(waitTime, TimeUnit.SECONDS);

        getAutomatedBrowser().init();
    }
}
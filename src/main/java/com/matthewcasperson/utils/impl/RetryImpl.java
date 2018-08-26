package com.matthewcasperson.utils.impl;

import com.matthewcasperson.utils.Retry;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

public class RetryImpl<T, E extends Exception> implements Retry<T, E> {
    @Override
    public T retry(RetryCallback<T, E> retryCallback) throws Throwable {
        final RetryTemplate retryTemplate = new RetryTemplate();
        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate.execute(retryCallback);
    }
}

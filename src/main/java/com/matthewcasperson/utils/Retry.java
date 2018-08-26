package com.matthewcasperson.utils;

import org.springframework.retry.RetryCallback;

public interface Retry<T, E extends Throwable> {
     T retry(RetryCallback<T, E> retryCallback) throws Throwable;
}

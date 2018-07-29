package com.matthewcasperson.utils;

public interface WebUtils {
    String HttpGet(final String Url);
    String HttpGet(final String Url, final String username, final String password);
}

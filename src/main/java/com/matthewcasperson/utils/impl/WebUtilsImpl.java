package com.matthewcasperson.utils.impl;

import com.matthewcasperson.utils.WebUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class WebUtilsImpl implements WebUtils {
    public static final int TIMEOUT = 5;
    private final RequestConfig GLOBAL_CONFIG = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD)
            .build();
    private final CookieStore COOKIE_STORE = new BasicCookieStore();

    @Override
    public String HttpGet(final String url) {
        return HttpGet(url, null, null);
    }

    @Override
    public String HttpGet(final String url, final String username, final String password) {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        if (username != null && password != null) {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    username,
                    password);
            provider.setCredentials(AuthScope.ANY, credentials);
        }

        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT * 1000)
                .setConnectionRequestTimeout(TIMEOUT * 1000)
                .setSocketTimeout(TIMEOUT * 1000).build();

        try (final CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .setDefaultRequestConfig(GLOBAL_CONFIG)
                .setDefaultCookieStore(COOKIE_STORE)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setDefaultRequestConfig(config)
                .build()) {
            try (final CloseableHttpResponse response = client.execute(new HttpGet(url))) {
                final HttpEntity entity = response.getEntity();
                try {
                    return IOUtils.toString(entity.getContent(), Charset.forName("UTF-8"));
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

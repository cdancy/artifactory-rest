package com.cdancy.artifactory.rest.config;

import com.squareup.okhttp.OkHttpClient;
import org.jclouds.http.okhttp.OkHttpClientSupplier;

import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by dancc on 3/2/16.
 */
@Singleton
public class ArtifactoryOkHttpClientSupplier implements OkHttpClientSupplier {

    @Override
    public OkHttpClient get() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(300000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(300000, TimeUnit.MILLISECONDS);
        client.setFollowRedirects(true);
        client.setFollowSslRedirects(true);
        return client;
    }
}

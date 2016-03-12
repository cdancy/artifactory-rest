package com.cdancy.artifactory.rest.parsers;

import autovalue.shaded.com.google.common.common.collect.Lists;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dancc on 3/11/16.
 */
@Singleton
public class ArtifactDownloadURIToList implements Function<HttpResponse, List<String>> {

    private final static JsonParser parser = new JsonParser();

    public List<String> apply(HttpResponse response) {

        List<String> uris = Lists.newArrayList();

        try {
            JsonElement jsonElement = parser.parse(new BufferedReader(new InputStreamReader(response.getPayload().openStream(), "UTF-8")));
            Iterator<JsonElement> iter = jsonElement.getAsJsonObject().get("results").getAsJsonArray().iterator();
            while (iter.hasNext()) {
                uris.add(iter.next().getAsJsonObject().get("downloadUri").getAsString());
            }
        } catch (Exception e) {
            Throwables.propagate(e);
        } finally {
            try {
                response.getPayload().close();
            } catch (Exception e2) {
                Throwables.propagate(e2);
            }
        }

        return uris;
    }
}

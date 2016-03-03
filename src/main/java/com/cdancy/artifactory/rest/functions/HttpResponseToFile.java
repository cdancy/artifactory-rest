package com.cdancy.artifactory.rest.functions;

import com.cdancy.artifactory.rest.util.ArtifactoryUtils;
import com.google.common.base.Function;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;
import java.io.File;

/**
 * Created by dancc on 3/2/16.
 */
@Singleton
public class HttpResponseToFile implements Function<HttpResponse, File> {

    public File apply(HttpResponse from) {
        File httpResponseFile = null;
        String possibleLocation = from.getFirstHeaderOrNull(ArtifactoryUtils.LOCATION_HEADER);
        if (possibleLocation != null) {
            httpResponseFile = new File(possibleLocation);
        }
        return httpResponseFile;
    }
}

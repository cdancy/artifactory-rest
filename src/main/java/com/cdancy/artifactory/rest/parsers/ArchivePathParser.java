package com.cdancy.artifactory.rest.parsers;


import com.google.common.base.Function;

import javax.inject.Singleton;

@Singleton
public class ArchivePathParser implements Function<Object, String> {

    @Override
    public String apply(Object archivePath) {
        String path;
        if (((String) archivePath).endsWith("/")) {
            path = ((String) archivePath).replaceAll("/$", "");
        } else {
            path = (String) archivePath;
        }
        if (!path.endsWith("!")) {
            path = ((String) archivePath).concat("!");
            return path;
        }
        return path;
    }
}

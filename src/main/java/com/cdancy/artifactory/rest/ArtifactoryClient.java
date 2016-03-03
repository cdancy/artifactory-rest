/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.artifactory.rest;

import org.jclouds.ContextBuilder;

public class ArtifactoryClient {

    private String endPoint;
    private String credentials;
    private final ArtifactoryApi artifactoryApi;

    public ArtifactoryClient(final String endPoint, final String credentials) {
        this.endPoint = endPoint;
        this.credentials = credentials;

        configureParameters();

        this.artifactoryApi = ContextBuilder.newBuilder(new ArtifactoryApiMetadata.Builder().build()).endpoint(endPoint())
                .credentials("N/A", credentials()).buildApi(ArtifactoryApi.class);
    }

    private void configureParameters() {

        // query system for endPoint value
        if (endPoint == null) {
            if ((endPoint = retrivePropertyValue("artifactory.rest.endpoint")) == null) {
                if ((endPoint = retrivePropertyValue("artifactoryRestEndpoint")) == null) {
                    if ((endPoint = retrivePropertyValue("ARTIFACTORY_REST_ENDPOINT")) == null) {
                        endPoint = "http://127.0.0.1:8080/artifactory";
                        System.out.println("Artifactory REST endpoint was not found. Defaulting to: " + endPoint);
                    }
                }
            }
        }

        // query system for credentials value
        if (credentials == null) {
            if ((credentials = retrivePropertyValue("artifactory.rest.credentials")) == null) {
                if ((credentials = retrivePropertyValue("artifactoryRestCredentials")) == null) {
                    if ((credentials = retrivePropertyValue("ARTIFACTORY_REST_CREDENTIALS")) == null) {
                        credentials = "admin:password";
                        System.out.println("Artifactory REST credentials was not found. Defaulting to: " + credentials);
                    }
                }
            }
        }
    }

    private String retrivePropertyValue(String key) {
        String value = System.getProperty(key);
        return value != null ? value : System.getenv(key);
    }

    public String endPoint() {
        return endPoint;
    }

    public String credentials() {
        return credentials;
    }

    public ArtifactoryApi api() {
        return artifactoryApi;
    }

    public static class Builder {
        private String endPoint;
        private String credentials;

        public Builder() {
        }

        public Builder(final String endPoint, final String credentials) {
            this.endPoint = endPoint;
            this.credentials = credentials;
        }

        public Builder endPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public Builder credentials(String credentials) {
            this.credentials = credentials;
            return this;
        }

        public ArtifactoryClient build() {
            return new ArtifactoryClient(endPoint, credentials);
        }
    }
}

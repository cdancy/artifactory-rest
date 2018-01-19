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

import com.cdancy.artifactory.rest.auth.AuthenticationType;
import com.cdancy.artifactory.rest.config.ArtifactoryAuthenticationModule;
import com.google.common.collect.Lists;
import java.util.Properties;
import org.jclouds.ContextBuilder;
import org.jclouds.javax.annotation.Nullable;

public final class ArtifactoryClient {

    private final String endPoint;
    private final ArtifactoryAuthentication credentials;
    private final ArtifactoryApi artifactoryApi;
    private final Properties overrides;

    /**
     * Create a ArtifactoryClient inferring endpoint and authentication from
     * environment and system properties.
     */
    public ArtifactoryClient() {
        this(null, null, null);
    }

    /**
     * Create an ArtifactoryClient. If any of the passed in variables are null we
     * will query System Properties and Environment Variables, in order, to 
     * search for values that may be set in a devops/CI fashion. The only
     * difference is the `overrides` which gets merged, but takes precedence,
     * with those System Properties and Environment Variables found.
     *
     * @param endPoint URL of Artifactory instance.
     * @param authentication authentication used to connect to Artifactory instance.
     * @param overrides jclouds Properties to override defaults when creating a new ArtifactoryApi.
     */
    public ArtifactoryClient(@Nullable final String endPoint,
            @Nullable final ArtifactoryAuthentication authentication,
            @Nullable final Properties overrides) {
        this.endPoint = endPoint != null
                ? endPoint
                : ArtifactoryUtils.inferEndpoint();
        this.credentials = authentication != null
                ? authentication
                : ArtifactoryUtils.inferAuthentication();
        this.overrides = mergeOverrides(overrides);
        this.artifactoryApi = createApi(this.endPoint, this.credentials, this.overrides);
    }

    private ArtifactoryApi createApi(final String endPoint, final ArtifactoryAuthentication authentication, final Properties overrides) {
        return ContextBuilder
                .newBuilder(new ArtifactoryApiMetadata.Builder().build())
                .endpoint(endPoint)
                .modules(Lists.newArrayList(new ArtifactoryAuthenticationModule(authentication)))
                .overrides(overrides)
                .buildApi(ArtifactoryApi.class);
    }

    /**
     * Query System Properties and Environment Variables for overrides and merge
     * the potentially passed in overrides with those.
     * 
     * @param possibleOverrides Optional passed in overrides.
     * @return Properties object.
     */
    private Properties mergeOverrides(final Properties possibleOverrides) {
        final Properties inferOverrides = ArtifactoryUtils.inferOverrides();
        if (possibleOverrides != null) {
            inferOverrides.putAll(possibleOverrides);
        }
        return inferOverrides;
    }

    public String endPoint() {
        return this.endPoint;
    }

    @Deprecated
    public String credentials() {
        return this.authValue();
    }

    public Properties overrides() {
        return this.overrides;
    }

    public String authValue() {
        return this.credentials.authValue();
    }

    public AuthenticationType authType() {
        return this.credentials.authType();
    }

    public ArtifactoryApi api() {
        return this.artifactoryApi;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String endPoint;
        private ArtifactoryAuthentication.Builder authBuilder;
        private Properties overrides;

        /**
         * Define the base endpoint to connect to.
         * 
         * @param endPoint Artifactory base endpoint.
         * @return this Builder.
         */
        public Builder endPoint(final String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        /**
         * Optional credentials to use for authentication. Must take the form of
         * `username:password` or its base64 encoded version.
         * 
         * @param optionallyBase64EncodedCredentials authentication credentials.
         * @return this Builder.
         */
        public Builder credentials(final String optionallyBase64EncodedCredentials) {
            authBuilder = ArtifactoryAuthentication.builder()
                    .credentials(optionallyBase64EncodedCredentials);
            return this;
        }

        /**
         * Optional token to use for authentication. 
         *
         * @param token authentication token.
         * @return this Builder.
         */
        public Builder token(final String token) {
            authBuilder = ArtifactoryAuthentication.builder()
                    .token(token);
            return this;
        }

        /**
         * Optional jclouds Properties to override. What can be overridden can
         * be found here:
         * 
         * <p>https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/Constants.java
         *
         * @param overrides optional jclouds Properties to override.
         * @return this Builder.
         */
        public Builder overrides(final Properties overrides) {
            this.overrides = overrides;
            return this;
        }

        /**
         * Build an instance of ArtifactoryClient.
         * 
         * @return ArtifactoryClient
         */
        public ArtifactoryClient build() {

            // 1.) If user passed in some auth use/build that.
            final ArtifactoryAuthentication authentication = authBuilder != null
                    ? authBuilder.build()
                    : null;

            return new ArtifactoryClient(endPoint, authentication, overrides);
        } 
    }
}

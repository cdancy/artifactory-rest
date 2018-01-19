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

import static com.google.common.io.BaseEncoding.base64;

import com.cdancy.artifactory.rest.auth.AuthenticationType;

import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * Credentials instance for Artifactory authentication. 
 */
public class ArtifactoryAuthentication extends Credentials {

    private final AuthenticationType authType;

    /**
     * Create instance of ArtifactoryAuthentication
     * 
     * @param authValue value to use for authentication type HTTP header.
     * @param authType authentication type (e.g. Basic, Bearer, Anonymous).
     */
    private ArtifactoryAuthentication(final String authValue, final AuthenticationType authType) {
        super(null, authType == AuthenticationType.Basic && authValue.contains(":")
                ? base64().encode(authValue.getBytes())
                : authValue);
        this.authType = authType;    
    }

    @Nullable
    public String authValue() {
        return this.credential;
    }

    public AuthenticationType authType() {
        return authType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String authValue;
        private AuthenticationType authType;

        /**
         * Set 'Basic' credentials.
         * 
         * @param basicCredentials value to use for 'Basic' credentials.
         * @return this Builder.
         */
        public Builder credentials(final String basicCredentials) {
            this.authType = AuthenticationType.Basic;
            this.authValue = basicCredentials;
            return this;
        }

        /**
         * Set 'Bearer' credentials.
         * 
         * @param tokenCredentials value to use for 'Bearer' credentials.
         * @return this Builder.
         */
        public Builder token(final String tokenCredentials) {
            this.authType = AuthenticationType.Bearer;
            this.authValue = tokenCredentials;
            return this;
        }

        /**
         * Build and instance of ArtifactoryCredentials.
         * 
         * @return instance of ArtifactoryCredentials.
         */
        public ArtifactoryAuthentication build() {
            return new ArtifactoryAuthentication(authValue, authType != null
                    ? authType
                    : AuthenticationType.Anonymous);
        }
    }
}

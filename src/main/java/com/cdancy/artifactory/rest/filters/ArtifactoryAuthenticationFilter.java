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
package com.cdancy.artifactory.rest.filters;

import com.cdancy.artifactory.rest.ArtifactoryAuthentication;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import com.google.common.net.HttpHeaders;

@Singleton
public class ArtifactoryAuthenticationFilter implements HttpRequestFilter {
    private final ArtifactoryAuthentication authentication;

    @Inject
    ArtifactoryAuthenticationFilter(final ArtifactoryAuthentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public HttpRequest filter(HttpRequest request) throws HttpException {
        switch(authentication.authType()) {
            case Anonymous: 
                return request.toBuilder().build();
            case Basic:
                final String basicValue = authentication.authType() + " " + authentication.authValue();
                return request.toBuilder().addHeader(HttpHeaders.AUTHORIZATION, basicValue).build();
            case Bearer:
                return request.toBuilder().addHeader("X-JFrog-Art-Api", authentication.authValue()).build();
            default:
                return request.toBuilder().build();
        }
    }
}

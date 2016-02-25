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
package com.github.artifactory.rest.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.location.Provider;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Supplier;
import com.google.common.net.HttpHeaders;

/**
 * Documentation surrounding Artifactory REST authentication can be found here:
 * 
 * https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API#
 * ArtifactoryRESTAPI-Authentication
 */
@Singleton
public class ArtifactoryAuthentication implements HttpRequestFilter {
   private final Supplier<Credentials> creds;

   private final String REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

   @Inject
   ArtifactoryAuthentication(@Provider Supplier<Credentials> creds) {
      this.creds = creds;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      if (currentCreds.credential == null) {
         throw new AuthorizationException("Credentials credential can not be null");
      }

      /*
       * client can pass in credential string in 1 of 3 ways:
       * 
       * 1.) As colon delimited username and password: admin:password
       * 
       * 2.) As base64 encoded value of colon delimited username and password:
       * YWRtaW46cGFzc3dvcmQ=
       * 
       * 3.) As JFrog api key which can be obtained from Artifactory portal:
       * 
       * AKCp2TfiyqrqHmfzUzeQhJmQrDyEx1o2S25pcC2hLzCTu65rpVhEoL1G6ppHn4exmHYfCiyT4
       */
      String foundCredential = currentCreds.credential;
      boolean isbase64 = false;
      if (foundCredential.contains(":")) {
         foundCredential = base64().encode(foundCredential.getBytes());
         isbase64 = true;
      }

      boolean useBasicAuth = isbase64 ? true : isBase64Encoded(foundCredential);
      if (useBasicAuth) {
         return request.toBuilder().addHeader(HttpHeaders.AUTHORIZATION, "Basic " + foundCredential).build();
      } else {
         return request.toBuilder().addHeader("X-JFrog-Art-Api", foundCredential).build();
      }
   }

   private boolean isBase64Encoded(String possiblyEncodedString) {
      return possiblyEncodedString.matches(REGEX) ? true : false;
   }
}

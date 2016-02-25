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

package com.github.artifactory.rest;

import org.jclouds.ContextBuilder;

public class ArtifactoryClient {

   private final String endPoint;
   private final String credentials;
   private final ArtifactoryApi artifactoryApi;

   public ArtifactoryClient(final String endPoint, final String credentials) {
      this.endPoint = endPoint;
      this.credentials = credentials;

      this.artifactoryApi = ContextBuilder.newBuilder("artifactory").endpoint(endPoint())
            .credentials("N/A", credentials()).buildApi(ArtifactoryApi.class);
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

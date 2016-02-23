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

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;

@AutoService(ApiMetadata.class)
public class ArtifactoryApiMetadata extends BaseHttpApiMetadata<ArtifactoryApi> {

   public static final String API_VERSION = "4.5.1";
   public static final String BUILD_VERSION = "40117";

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ArtifactoryApiMetadata() {
      this(new Builder());
   }

   protected ArtifactoryApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ArtifactoryApi, Builder> {

      protected Builder() {
         super(ArtifactoryApi.class);
         id("artifactory").name("Artifactory API").identityName("Optional Username").credentialName("Optional Password")
               .defaultIdentity("admin").defaultCredential("password")
               .documentation(URI.create("https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API"))
               .version(API_VERSION).buildVersion(BUILD_VERSION).defaultEndpoint("http://127.0.0.1:8081/artifactory")
               .defaultProperties(ArtifactoryApiMetadata.defaultProperties());
      }

      @Override
      public ArtifactoryApiMetadata build() {
         return new ArtifactoryApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         return this;
      }
   }
}

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

package com.cdancy.artifactory.rest.domain.artifact;

import org.jclouds.json.SerializedNames;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Artifact {

   public abstract String uri();

   @Nullable
   public abstract String downloadUri();

   @Nullable
   public abstract String repo();

   @Nullable
   public abstract String path();

   @Nullable
   public abstract String created();

   @Nullable
   public abstract String createdBy();

   public abstract String size();

   @Nullable
   public abstract String lastModified();

   @Nullable
   public abstract String folder();

   @Nullable
   public abstract String mimeType();

   @Nullable
   public abstract CheckSum checksums();

   @Nullable
   public abstract CheckSum originalChecksums();

   Artifact() {
   }

   @SerializedNames({ "uri", "downloadUri", "repo", "path", "created", "createdBy",
           "size", "lastModified", "folder", "mimeType", "checksums", "originalChecksums" })
   public static Artifact create(String uri, String downloadUri, String repo,
                                 String path, String created, String createdBy,
                                 String size, String lastModified, String folder,
                                 String mimeType, CheckSum checksums, CheckSum originalChecksums) {
      return new AutoValue_Artifact(uri, downloadUri, repo, path,
              created, createdBy, size, lastModified,
              folder, mimeType, checksums, originalChecksums);
   }
}

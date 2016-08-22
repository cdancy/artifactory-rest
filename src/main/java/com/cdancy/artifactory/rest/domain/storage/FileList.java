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

package com.cdancy.artifactory.rest.domain.storage;

import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class FileList {

   public abstract String uri();

   public abstract String created();

   public abstract List<Artifact> files();

   FileList() {
   }

   @SerializedNames({ "uri", "created", "files" })
   public static FileList create(String uri, String created, List<Artifact> files) {
      return new AutoValue_FileList(uri, created,
              files != null ? ImmutableList.copyOf(files) : ImmutableList.<Artifact> of());
   }
}

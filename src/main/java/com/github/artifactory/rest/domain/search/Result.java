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

package com.github.artifactory.rest.domain.search;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Result {

   public abstract String repo();

   public abstract String path();

   public abstract String name();

   public abstract String type();

   public abstract String size();

   public abstract String created();

   public abstract String created_by();

   public abstract String modified();

   public abstract String modified_by();

   public abstract String updated();

   Result() {
   }

   @SerializedNames({ "repo", "path", "name", "type", "size", "created", "created_by", "modified", "modified_by",
         "updated" })
   public static Result create(String repo, String path, String name, String type, String size, String created,
         String created_by, String modified, String modified_by, String updated) {
      return new AutoValue_Result(repo, path, name, type, size, created, created_by, modified, modified_by, updated);
   }
}

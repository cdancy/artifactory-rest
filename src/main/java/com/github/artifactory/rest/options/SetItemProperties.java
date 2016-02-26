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
package com.github.artifactory.rest.options;

import static com.github.artifactory.rest.util.ArtifactoryUtils.collectionToString;
import static com.github.artifactory.rest.util.Preconditions2.checkNotEmpty;

import java.util.Collection;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class SetItemProperties extends BaseHttpRequestOptions {

   public SetItemProperties add(String key, String value) {
      checkNotEmpty(key, "`key` can not be empty");
      checkNotEmpty(value, "`value` can not be empty");
      String keyValuePair = key + "=" + value;
      Collection<String> existingProps = this.queryParameters.get("properties");

      // TODO: currently we can only pass in 1 key/value pair due to jclouds
      // incorrectly escaping the invalid way Artifactory wants you to supply
      // multiple properties
      existingProps.clear();

      existingProps.add(keyValuePair);
      String queryParams = collectionToString(existingProps);
      this.queryParameters.clear();
      queryParameters.put("properties", queryParams);
      return this;
   }

   public static class Builder {

      /**
       * @param key name of key to add
       * @param value value to set key to
       * @return instance of SetItemProperties
       */
      public static SetItemProperties add(String key, String value) {
         SetItemProperties options = new SetItemProperties();
         return options.add(key, value);
      }
   }
}

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

import static com.github.artifactory.rest.util.Preconditions2.checkNotEmpty;

import java.util.Collection;

import org.jclouds.http.options.BaseHttpRequestOptions;

public class DeleteItemProperties extends BaseHttpRequestOptions {

   public DeleteItemProperties add(String key) {
      checkNotEmpty(key, "`key` can not be empty");
      Collection<String> existingProps = this.queryParameters.get("properties");
      existingProps.add(key);
      return this;
   }

   public static class Builder {

      /**
       * @param key name of key to delete
       * @return instance of DeleteItemProperties
       */
      public static DeleteItemProperties add(String key) {
         DeleteItemProperties options = new DeleteItemProperties();
         return options.add(key);
      }
   }
}

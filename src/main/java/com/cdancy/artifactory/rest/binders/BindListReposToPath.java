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
package com.cdancy.artifactory.rest.binders;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import javax.inject.Singleton;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class BindListReposToPath implements Binder {
    @Override
    public <R extends HttpRequest> R bindToRequest(final R request, final Object repositories) {

        checkArgument(repositories instanceof List, "binder is only valid for List");
        List<String> repos = (List<String>) repositories;
        checkArgument(repos.size() > 0, "repositories List cannot be empty");

        StringBuilder propertiesProp = new StringBuilder();
        for (String prop : repos) {
            if (prop != null) {
                String potentialKey = prop.trim();
                if (potentialKey.length() > 0) {
                    propertiesProp.append(potentialKey).append(",");
                }
            }
        }

        if (propertiesProp.length() == 0) {
            throw new IllegalArgumentException("repositories did not have any valid Strings");
        }

        propertiesProp.setLength(propertiesProp.length() - 1);
        return (R) request.toBuilder().addQueryParam("repos", propertiesProp.toString()).build();
    }
}
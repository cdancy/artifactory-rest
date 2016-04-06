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

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.cdancy.artifactory.rest.util.ArtifactoryUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

@Singleton
public class BindMatrixPropertiesToPath implements Binder {
    @Override
    public <R extends HttpRequest> R bindToRequest(final R request, final Object properties) {

        checkArgument(properties instanceof Map, "binder is only valid for Map");
        StringBuilder configuredEndpoint = new StringBuilder(request.getEndpoint().toString());
        Map<String, List<String>> matrixProperties = (Map<String, List<String>>) properties;
        if (matrixProperties.size() > 0) {
            for (Map.Entry<String, List<String>> prop : matrixProperties.entrySet()) {
                String potentialKey = prop.getKey().trim();
                if (potentialKey.length() > 0) {
                    configuredEndpoint.append(";").append(potentialKey);
                    if (prop.getValue() != null) {
                        String potentialValue = ArtifactoryUtils.collectionToString(prop.getValue(), ",");
                        if (potentialValue.length() > 0) {
                            String encodedValue = "";
                            try {
                                if (potentialValue != null)
                                    encodedValue = potentialValue.replaceAll(" ", "%20");
                            } catch (Exception e) {
                                encodedValue =  potentialValue;
                            }
                            configuredEndpoint.append("=").append(encodedValue);
                        }
                    }
                }
            }
        }
        return (R) request.toBuilder().endpoint(configuredEndpoint.toString()).build();
    }
}
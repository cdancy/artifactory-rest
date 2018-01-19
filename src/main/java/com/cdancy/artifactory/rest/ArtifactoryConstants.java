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

package com.cdancy.artifactory.rest;

/**
 * Various constants that can be used in a global context.
 */
public class ArtifactoryConstants {

    public static final String ENDPOINT_SYSTEM_PROPERTY = "artifactory.rest.endpoint";
    public static final String ENDPOINT_ENVIRONMENT_VARIABLE = ENDPOINT_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    public static final String CREDENTIALS_SYSTEM_PROPERTY = "artifactory.rest.credentials";
    public static final String CREDENTIALS_ENVIRONMENT_VARIABLE = CREDENTIALS_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    public static final String TOKEN_SYSTEM_PROPERTY = "artifactory.rest.token";
    public static final String TOKEN_ENVIRONMENT_VARIABLE = TOKEN_SYSTEM_PROPERTY.replaceAll("\\.", "_").toUpperCase();

    public static final String DEFAULT_ENDPOINT = "http://127.0.0.1:8080/artifactory";

    public static final String JCLOUDS_PROPERTY_ID = "jclouds.";
    public static final String ARTIFACTORY_REST_PROPERTY_ID = "artifactory.rest." + JCLOUDS_PROPERTY_ID;

    public static final String JCLOUDS_VARIABLE_ID = "JCLOUDS_";
    public static final String ARTIFACTORY_REST_VARIABLE_ID = "ARTIFACTORY_REST_" + JCLOUDS_VARIABLE_ID;

    protected ArtifactoryConstants() {
        throw new UnsupportedOperationException("Purposefully not implemented");
    }
}

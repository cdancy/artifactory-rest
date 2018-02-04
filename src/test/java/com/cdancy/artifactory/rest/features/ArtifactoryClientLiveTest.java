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
package com.cdancy.artifactory.rest.features;

import static com.cdancy.artifactory.rest.ArtifactoryConstants.CREDENTIALS_ENVIRONMENT_VARIABLE;
import static com.cdancy.artifactory.rest.ArtifactoryConstants.CREDENTIALS_SYSTEM_PROPERTY;
import static com.cdancy.artifactory.rest.ArtifactoryConstants.ENDPOINT_ENVIRONMENT_VARIABLE;
import static com.cdancy.artifactory.rest.ArtifactoryConstants.ENDPOINT_SYSTEM_PROPERTY;
import static com.cdancy.artifactory.rest.ArtifactoryConstants.TOKEN_ENVIRONMENT_VARIABLE;
import static com.cdancy.artifactory.rest.ArtifactoryConstants.TOKEN_SYSTEM_PROPERTY;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryAuthentication;
import com.cdancy.artifactory.rest.ArtifactoryClient;
import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.TestUtilities;
import com.cdancy.artifactory.rest.auth.AuthenticationType;
import com.cdancy.artifactory.rest.domain.system.Version;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jclouds.http.HttpResponseException;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.AuthorizationException;

@Test(groups = "live", testName = "ArtifactoryClientLiveTest", singleThreaded = true)
public class ArtifactoryClientLiveTest extends BaseArtifactoryApiLiveTest {

    private static final String DUMMY_ENDPOINT = "http://some-non-existent-host:12345";
    private static final String SYSTEM_JCLOUDS_TIMEOUT = "artifactory.rest.jclouds.so-timeout";
    private static final String ENVIRONMENT_JCLOUDS_TIMEOUT = "ARTIFACTORY_REST_JCLOUDS_SO-TIMEOUT";

    @Test
    public void testCreateClient() {
        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, this.artifactoryAuthentication, null);
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
    }

    @Test
    public void testCreateClientWithBuilder() {
        final ArtifactoryClient.Builder builder = ArtifactoryClient.builder();
        switch (artifactoryAuthentication.authType()) {
            case Anonymous: break;
            case Basic:
                builder.credentials(artifactoryAuthentication.authValue());
                break;
            case Bearer:
                builder.token(artifactoryAuthentication.authValue());
                break;
            default: break;
        }
        final ArtifactoryClient client = builder.endPoint(this.endpoint).build();
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
    }

    @Test (expectedExceptions = AuthorizationException.class)
    public void testCreateClientWithWrongCredentials() {
        final ArtifactoryAuthentication auth = ArtifactoryAuthentication
                .builder()
                .credentials(TestUtilities.randomStringLettersOnly())
                .build();
        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, auth, null);
        client.api().systemApi().version();
    }

    @Test
    public void testCreateClientWithEndpointFromSystemProperties() {
        clearSystemProperties();

        System.setProperty(ENDPOINT_SYSTEM_PROPERTY, this.endpoint);
        final ArtifactoryClient client = new ArtifactoryClient(null, this.artifactoryAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(this.endpoint);
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithWrongEndpointFromSystemProperties() {
        clearSystemProperties();

        System.setProperty(ENDPOINT_SYSTEM_PROPERTY, DUMMY_ENDPOINT);
        final ArtifactoryClient client = new ArtifactoryClient(null, this.artifactoryAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(DUMMY_ENDPOINT);
        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final HttpResponseException e) {
            caughtException = true;
        }
        assertThat(caughtException).isTrue();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithAuthenticationFromSystemProperties() {
        clearSystemProperties();

        final AuthenticationType currentAuthType = this.artifactoryAuthentication.authType();
        final String correctAuth = this.artifactoryAuthentication.authValue();
        if (currentAuthType == AuthenticationType.Basic) {
            System.setProperty(CREDENTIALS_SYSTEM_PROPERTY, correctAuth);
        } else if (currentAuthType == AuthenticationType.Bearer) {
            System.setProperty(TOKEN_SYSTEM_PROPERTY, correctAuth);
        }

        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(correctAuth);
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithWrongAuthenticationFromSystemProperties() {
        clearSystemProperties();

        final AuthenticationType currentAuthType = this.artifactoryAuthentication.authType();
        final String wrongAuth = TestUtilities.randomStringLettersOnly();
        if (currentAuthType == AuthenticationType.Basic) {
            System.setProperty(CREDENTIALS_SYSTEM_PROPERTY, wrongAuth);
        } else if (currentAuthType == AuthenticationType.Bearer) {
            System.setProperty(TOKEN_SYSTEM_PROPERTY, wrongAuth);
        }

        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(wrongAuth);

        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final AuthorizationException e) {
            caughtException = true;
        }
        assertThat(caughtException).isTrue();
        clearSystemProperties();
    }

    @Test
    public void testCreateClientWithEndpointFromEnvironmentVariables() {
        clearEnvironmentVariables(null);
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENDPOINT_ENVIRONMENT_VARIABLE, this.endpoint);
        TestUtilities.addEnvironmentVariables(envVars);

        final ArtifactoryClient client = new ArtifactoryClient(null, this.artifactoryAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(this.endpoint);
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithWrongEndpointFromEnvironmentVariables() {
        clearEnvironmentVariables(null);
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENDPOINT_ENVIRONMENT_VARIABLE, DUMMY_ENDPOINT);
        TestUtilities.addEnvironmentVariables(envVars);

        final ArtifactoryClient client = new ArtifactoryClient(null, this.artifactoryAuthentication, null);
        assertThat(client.endPoint()).isEqualTo(DUMMY_ENDPOINT);
        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final HttpResponseException e) {
            caughtException = true;
        }
        assertThat(caughtException).isTrue();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithAuthenticationFromEnvironmentVariables() {
        clearEnvironmentVariables(null);

        final AuthenticationType currentAuthType = this.artifactoryAuthentication.authType();
        final String correctAuth = this.artifactoryAuthentication.authValue();
        final String correctAuthType;
        switch (currentAuthType) {
            case Basic:
                correctAuthType = CREDENTIALS_ENVIRONMENT_VARIABLE;
                break;
            case Bearer:
                correctAuthType = TOKEN_ENVIRONMENT_VARIABLE;
                break;
            default:
                correctAuthType = null;
                break;
        }

        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(correctAuthType, correctAuth);
        TestUtilities.addEnvironmentVariables(envVars);

        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(correctAuth);
        final Version version = client.api().systemApi().version();
        assertThat(version).isNotNull();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithWrongAuthenticationFromEnvironmentVariables() {
        clearEnvironmentVariables(null);

        final AuthenticationType currentAuthType = this.artifactoryAuthentication.authType();
        final String wrongAuth = TestUtilities.randomStringLettersOnly();
        final String correctAuthType;
        switch (currentAuthType) {
            case Basic:
                correctAuthType = CREDENTIALS_ENVIRONMENT_VARIABLE;
                break;
            case Bearer:
                correctAuthType = TOKEN_ENVIRONMENT_VARIABLE;
                break;
            default:
                correctAuthType = null;
                break;
        }

        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(correctAuthType, wrongAuth);
        TestUtilities.addEnvironmentVariables(envVars);

        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, null, null);
        assertThat(client.authType()).isEqualTo(currentAuthType);
        assertThat(client.authValue()).isEqualTo(wrongAuth);
        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final AuthorizationException e) {
            caughtException = true;
        }
        assertThat(caughtException).isTrue();
        clearEnvironmentVariables(null);
    }

    @Test
    public void testCreateClientWithOverridesAndFail() {
        final Properties properties = new Properties();
        properties.put("jclouds.so-timeout", -1);
        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, this.artifactoryAuthentication, properties);

        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().contains("timeouts can't be negative")) {
                caughtException = true;
            } else {
                throw new RuntimeException("Exception does not contain proper message: message='" + e.getMessage() + "'");
            }
        }
        assertThat(caughtException).isTrue();
    }

    @Test
    public void testCreateClientWithOverridesFromSystemPropertiesAndFail() {
        System.clearProperty(SYSTEM_JCLOUDS_TIMEOUT);
        System.setProperty(SYSTEM_JCLOUDS_TIMEOUT, "-1");
        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, this.artifactoryAuthentication, null);
        
        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().contains("timeouts can't be negative")) {
                caughtException = true;
            } else {
                throw new RuntimeException("Exception does not contain proper message: message='" + e.getMessage() + "'");
            }
        }
        assertThat(caughtException).isTrue();
        System.clearProperty(SYSTEM_JCLOUDS_TIMEOUT);
    }

    @Test
    public void testCreateClientWithOverridesFromEnvironmentVariablesAndFail() {
        final Map<String, String> envVars = Maps.newHashMap();
        envVars.put(ENVIRONMENT_JCLOUDS_TIMEOUT, "-1");
        TestUtilities.addEnvironmentVariables(envVars);

        final ArtifactoryClient client = new ArtifactoryClient(this.endpoint, this.artifactoryAuthentication, null);
        boolean caughtException = false;
        try {
            client.api().systemApi().version();
        } catch (final IllegalArgumentException e) {
            if (e.getMessage().contains("timeouts can't be negative")) {
                caughtException = true;
            } else {
                throw new RuntimeException("Exception does not contain proper message: message='" + e.getMessage() + "'");
            }
        }
        assertThat(caughtException).isTrue();
        clearEnvironmentVariables(envVars.keySet());
    }

    private void clearSystemProperties() {
        System.clearProperty(ENDPOINT_SYSTEM_PROPERTY);
        System.clearProperty(CREDENTIALS_SYSTEM_PROPERTY);
        System.clearProperty(TOKEN_SYSTEM_PROPERTY);
    }

    private void clearEnvironmentVariables(@Nullable final Collection optionalKeysToClear) {
        final List<String> envVars = Lists.newArrayList(ENDPOINT_SYSTEM_PROPERTY);
        envVars.add(CREDENTIALS_SYSTEM_PROPERTY);
        envVars.add(TOKEN_SYSTEM_PROPERTY);
        if (optionalKeysToClear != null) {
            envVars.addAll(optionalKeysToClear);
        }
        TestUtilities.removeEnvironmentVariables(envVars);
    }
}

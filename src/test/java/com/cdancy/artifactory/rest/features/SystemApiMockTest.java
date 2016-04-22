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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.system.Version;
import com.cdancy.artifactory.rest.features.SystemApi;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.ws.rs.core.MediaType;

/**
 * Mock tests for the {@link com.cdancy.artifactory.rest.features.SystemApi}
 * class.
 */
@Test(groups = "unit", testName = "SystemApiMockTest")
public class SystemApiMockTest extends BaseArtifactoryMockTest {

   private final String versionRegex = "^\\d+\\.\\d+\\.\\d+$";

   public void testGetVersion() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/version.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SystemApi api = jcloudsApi.systemApi();
      try {
         Version version = api.version();
         assertNotNull(version);
         assertTrue(version.version().matches(versionRegex));
         assertSent(server, "GET", "/api/system/version", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGetSystem() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/system.txt")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SystemApi api = jcloudsApi.systemApi();
      try {
         String system = api.system();
         assertNotNull(system);
         assertSent(server, "GET", "/api/system", MediaType.TEXT_PLAIN);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

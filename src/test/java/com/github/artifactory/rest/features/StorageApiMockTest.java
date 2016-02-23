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
package com.github.artifactory.rest.features;

import org.testng.annotations.Test;

import com.github.artifactory.rest.ArtifactoryApi;
import com.github.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link com.github.jclouds.artifactory.features.StorageApi}
 * class.
 */
@Test(groups = "unit", testName = "StorageApiMockTest")
public class StorageApiMockTest extends BaseArtifactoryMockTest {

   public void testSetItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/aql.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StoragApi api = jcloudsApi.storageApi();
      try {
         // Version version = api.version();
         // assertNotNull(version);
         // assertTrue(version.version().matches(versionRegex));
         // assertSent(server, "GET", "/api/system/version");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

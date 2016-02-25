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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.github.artifactory.rest.ArtifactoryApi;
import com.github.artifactory.rest.domain.artifact.Artifact;
import com.github.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link com.github.artifactory.rest.features.ArtifactApi}
 * class.
 */
@Test(groups = "unit", testName = "ArtifactApiMockTest")
public class ArtifactApiMockTest extends BaseArtifactoryMockTest {

   public void testDeployArtifact() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      String payload = payloadFromResource("/artifact.json");
      server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      ArtifactApi api = jcloudsApi.artifactApi();
      try {
         Artifact artifact = api.deployArtifact("libs-release-local", "my/jar/1.0/jar-1.0.jar",
               Payloads.newPayload(payload));

         assertNotNull(artifact);
         assertTrue(artifact.repo().equals("libs-release-local"));
         assertSent(server, "PUT", "/libs-release-local/my/jar/1.0/jar-1.0.jar");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteArtifact() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(204));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      ArtifactApi api = jcloudsApi.artifactApi();
      try {
         boolean deleted = api.deleteArtifact("libs-release-local", "my/jar/1.0/jar-1.0.jar");
         assertTrue(deleted);
         assertSent(server, "DELETE", "/libs-release-local/my/jar/1.0/jar-1.0.jar");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNonExistentArtifact() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      String payload = payloadFromResource("/artifact.json");
      server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      ArtifactApi api = jcloudsApi.artifactApi();
      try {
         boolean deleted = api.deleteArtifact("libs-release-local", "my/jar/1.0/jar-1.0.jar");
         assertFalse(deleted);
         assertSent(server, "DELETE", "/libs-release-local/my/jar/1.0/jar-1.0.jar");
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

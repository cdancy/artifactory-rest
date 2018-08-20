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

import static com.cdancy.artifactory.rest.TestUtilities.assertFalse;
import static com.cdancy.artifactory.rest.TestUtilities.assertNotNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertTrue;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.docker.PromoteImage;
import com.cdancy.artifactory.rest.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.util.List;

/**
 * Mock tests for the {@link DockerApi}
 * class.
 */
@Test(groups = "unit", testName = "DockerApiMockTest")
public class DockerApiMockTest extends BaseArtifactoryMockTest {

    public void testPromote() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      String payload = payloadFromResource("/docker-promote.json");
      server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      DockerApi api = jcloudsApi.dockerApi();
      try {
          PromoteImage dockerPromote = PromoteImage.create("docker-promoted", "library/artifactory", "latest", false);
          boolean success = api.promote("docker-local", dockerPromote);
          assertTrue(success);
          assertSent(server, "POST", "/api/docker/docker-local/v2/promote", MediaType.TEXT_PLAIN);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
    }

    public void testPromoteNonExistentImage() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = "Unable to find tag file at 'repositories/library/artifactory/latest/tag.json'";
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        DockerApi api = jcloudsApi.dockerApi();
        try {
            PromoteImage dockerPromote = PromoteImage.create("docker-promoted", "library/artifactory", "latest", false);
            boolean success = api.promote("docker-local", dockerPromote);
            assertFalse(success);
            assertSent(server, "POST", "/api/docker/docker-local/v2/promote", MediaType.TEXT_PLAIN);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testListRepositories() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/docker-list-repositories.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        DockerApi api = jcloudsApi.dockerApi();
        try {
            List<String> repos = api.repositories("docker-local");
            assertNotNull(repos);
            assertTrue(repos.size() == 3);
            assertTrue(repos.contains("busybox"));
            assertSent(server, "GET", "/api/docker/docker-local/_catalog", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }
}

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
import static com.cdancy.artifactory.rest.TestUtilities.assertNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertTrue;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import com.cdancy.artifactory.rest.BaseArtifactoryMockTest;
import com.google.common.collect.Lists;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import okio.Buffer;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock tests for the {@link com.cdancy.artifactory.rest.features.ArtifactApi}
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
               Payloads.newPayload(payload), null);

         assertNotNull(artifact);
         assertTrue(artifact.repo().equals("libs-release-local"));
         assertSent(server, "PUT", "/libs-release-local/my/jar/1.0/jar-1.0.jar", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
    }

    public void testDeployArtifactWithProperties() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/artifact.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            Map<String, List<String>> properties = new HashMap<>();
            properties.put("hello", Lists.newArrayList("world"));
            Artifact artifact = api.deployArtifact("libs-release-local", "my/jar/1.0/jar-1.0.jar",
                    Payloads.newPayload(payload), properties);

            assertNotNull(artifact);
            assertTrue(artifact.repo().equals("libs-release-local"));
            assertSent(server, "PUT", "/libs-release-local/my/jar/1.0/jar-1.0.jar;hello=world", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testCopyArtifact() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/artifact-copy.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            RequestStatus requestStatus = api.copyArtifact("libs-snapshot-local", "hello/world", "ext-snapshot-local/hello/world");
            assertNotNull(requestStatus);
            assertTrue(requestStatus.errors().size() == 0);
            assertTrue(requestStatus.messages().size() == 1);
            assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("info"));
            assertSent(server, "POST", "/api/copy/libs-snapshot-local/hello/world?failFast=1&suppressLayouts=0&to=ext-snapshot-local/hello/world", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testMoveArtifact() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/artifact-copy.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            RequestStatus requestStatus = api.moveArtifact("libs-snapshot-local", "hello/world", "ext-snapshot-local/hello/world");
            assertNotNull(requestStatus);
            assertTrue(requestStatus.errors().size() == 0);
            assertTrue(requestStatus.messages().size() == 1);
            assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("info"));
            assertSent(server, "POST", "/api/move/libs-snapshot-local/hello/world?failFast=1&suppressLayouts=0&to=ext-snapshot-local/hello/world", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testMoveArtifactWithNonExistentSource() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/artifact-move-src-not-exist.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            RequestStatus requestStatus = api.moveArtifact("libs-snapshot-local", "does/not/exist", "ext-snapshot-local/hello/world");
            assertNotNull(requestStatus);
            assertTrue(requestStatus.errors().size() == 0);
            assertTrue(requestStatus.messages().size() == 1);
            assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("error"));
            assertSent(server, "POST", "/api/move/libs-snapshot-local/does/not/exist?failFast=1&suppressLayouts=0&to=ext-snapshot-local/hello/world", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testCopyArtifactWithNonExistentSource() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/artifact-copy-src-not-exist.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            RequestStatus requestStatus = api.copyArtifact("libs-snapshot-local", "does/not/exist", "ext-snapshot-local/hello/world");
            assertNotNull(requestStatus);
            assertTrue(requestStatus.errors().size() == 0);
            assertTrue(requestStatus.messages().size() == 1);
            assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("error"));
            assertSent(server, "POST", "/api/copy/libs-snapshot-local/does/not/exist?failFast=1&suppressLayouts=0&to=ext-snapshot-local/hello/world", MediaType.APPLICATION_JSON);
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
         assertSent(server, "DELETE", "/libs-release-local/my/jar/1.0/jar-1.0.jar", MediaType.WILDCARD);
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
         assertSent(server, "DELETE", "/libs-release-local/my/jar/1.0/jar-1.0.jar", MediaType.WILDCARD);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
    }

    public void testDownloadArtifact() throws IOException {
        MockWebServer server = mockArtifactoryJavaWebServer();

        Buffer buffer = new Buffer();
        InputStream in = getClass().getResourceAsStream("/readme.txt");
        buffer.write(in.readAllBytes());

        server.enqueue(new MockResponse().setBody(buffer).setResponseCode(200));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();

        try {
            InputStream inputStream = api.downloadArtifact("libs-release-local", "my/artifact/readme.txt", false);
            assertNotNull(inputStream);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testDownloadNonExistentArtifact() throws IOException {
        MockWebServer server = mockArtifactoryJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();

        try {
            InputStream inputStream = api.downloadArtifact("libs-release-local", "my/artifact/nonexistent.txt", false);
            assertNull(inputStream);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testDownloadArchiveEntry() throws IOException {
        MockWebServer server = mockArtifactoryJavaWebServer();

        Buffer buffer = new Buffer();
        InputStream in = getClass().getResourceAsStream("/my-archive.zip");
        buffer.write(in.readAllBytes());

        server.enqueue(new MockResponse().setBody(buffer).setResponseCode(200));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();

        try {
            InputStream inputStream = api.downloadArchiveEntry("libs-release-local", "my/artifact/my-artifact.zip", "file1");
            assertNotNull(inputStream);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testDownloadNonExistentArchiveEntry() throws IOException {
        MockWebServer server = mockArtifactoryJavaWebServer();

        server.enqueue(new MockResponse().setResponseCode(404));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();

        try {
            InputStream inputStream = api.downloadArchiveEntry("libs-release-local", "my/artifact/my-artifact.zip", "non-existent/file1");
            assertNull(inputStream);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }
}

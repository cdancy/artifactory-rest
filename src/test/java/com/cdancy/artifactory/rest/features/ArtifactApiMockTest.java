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

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

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
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("hello", "world");
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

    public void testRetrieveArtifact() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/retrieve-artifact.txt");
        server.enqueue(new MockResponse().
                setBody(payload).
                setHeader("X-Artifactory-Filename", "jar-1.0.txt").
                setHeader("X-Checksum-Md5", randomString()).
                setResponseCode(200));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        File inputStream = null;
        try {
            inputStream = api.retrieveArtifact("libs-release-local", "my/jar/1.0/jar-1.0.txt", null);
            assertNotNull(inputStream);
            assertTrue(inputStream.exists());

            String content = FileUtils.readFileToString(inputStream);

            assertTrue(content.toString().equals(payload));
            assertSent(server, "GET", "/libs-release-local/my/jar/1.0/jar-1.0.txt", MediaType.APPLICATION_OCTET_STREAM);
        } finally {
            if (inputStream != null && inputStream.exists()) {
                FileUtils.deleteQuietly(inputStream.getParentFile());
            }

            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testRetrieveArtifactWithProperties() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/retrieve-artifact.txt");
        server.enqueue(new MockResponse().
                setBody(payload).
                setHeader("X-Artifactory-Filename", "jar-1.0.txt").
                setHeader("X-Checksum-Md5", randomString()).
                setResponseCode(200));

        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        File inputStream = null;
        try {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("hello", "world");

            inputStream = api.retrieveArtifact("libs-release-local", "my/jar/1.0/jar-1.0.txt", properties);
            assertTrue(inputStream.exists());

            String content = FileUtils.readFileToString(inputStream);

            assertTrue(content.toString().equals(payload));
            assertSent(server, "GET", "/libs-release-local/my/jar/1.0/jar-1.0.txt;hello=world", MediaType.APPLICATION_OCTET_STREAM);
        } finally {
            if (inputStream != null && inputStream.exists()) {
                FileUtils.deleteQuietly(inputStream.getParentFile());
            }

            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testRetrieveArtifactWithIllegalPropertyValue() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/retrieve-artifact.txt");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("hello", "world");

            File inputStream = api.retrieveArtifact("libs-release-local", "my/jar/1.0/jar-1.0.txt", properties);
            assertNull(inputStream);
            assertSent(server, "GET", "/libs-release-local/my/jar/1.0/jar-1.0.txt;hello=world", MediaType.APPLICATION_OCTET_STREAM);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testRetrieveNonExistentArtifact() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/retrieve-artifact.txt");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        ArtifactApi api = jcloudsApi.artifactApi();
        try {
            File inputStream = api.retrieveArtifact("libs-release-local", "my/jar/1.0/jar-1.0.txt", null);
            assertNull(inputStream);
            assertSent(server, "GET", "/libs-release-local/my/jar/1.0/jar-1.0.txt", MediaType.APPLICATION_OCTET_STREAM);
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
}

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
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.cdancy.artifactory.rest.options.PromoteBuildOptions;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.testng.Assert.*;

/**
 * Mock tests for the {@link BuildApi}
 * class.
 */
@Test(groups = "unit", testName = "BuildApiMockTest")
public class BuildApiMockTest extends BaseArtifactoryMockTest {

    public void testPromoteBuild() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/build-promote.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(200));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        BuildApi api = jcloudsApi.buildApi();
        try {
            String name = "MyBuildPlan";
            int buildNumber = 999;

            String sourceRepo = "dev-repo";
            String targetRepo = "release-repo";
            PromoteBuildOptions options = PromoteBuildOptions.create("promote", "error promoted", "BuildUser", null, false, sourceRepo, targetRepo, true, true, false, null, null, true);
            RequestStatus promoteStatus = api.promote(name, buildNumber, options);
            assertNotNull(promoteStatus);
            assertTrue(promoteStatus.errors().size() == 0);
            assertTrue(promoteStatus.messages().size() == 0);
            assertSent(server, "POST", "/api/build/promote/MyBuildPlan/999", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testPromoteBuildNonExistent() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/build-promote-non-existent.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(404));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        BuildApi api = jcloudsApi.buildApi();
        try {
            String name = "DevTest";
            int buildNumber = 999;

            String sourceRepo = "dev-repo";
            String targetRepo = "release-repo";
            PromoteBuildOptions options = PromoteBuildOptions.create("promote", "error promoted", "BuildUser", null, false, sourceRepo, targetRepo, true, true, false, null, null, true);
            RequestStatus promoteStatus = api.promote(name, buildNumber, options);
            assertNotNull(promoteStatus);
            assertTrue(promoteStatus.errors().size() == 1);
            assertTrue(promoteStatus.errors().get(0).status() == 404);
            assertTrue(promoteStatus.errors().get(0).message().startsWith("Cannot find builds"));
            assertSent(server, "POST", "/api/build/promote/DevTest/999", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }

    public void testPromoteBuildIllegalArgument() throws Exception {
        MockWebServer server = mockArtifactoryJavaWebServer();

        String payload = payloadFromResource("/build-promote-messages.json");
        server.enqueue(new MockResponse().setBody(payload).setResponseCode(400));
        ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
        BuildApi api = jcloudsApi.buildApi();
        try {
            String name = "DevTest";
            int buildNumber = 999;

            String sourceRepo = "dev-repo";
            String targetRepo = "release-repo";
            PromoteBuildOptions options = PromoteBuildOptions.create("promote", "error promoted", "BuildUser", null, false, sourceRepo, targetRepo, true, true, false, null, null, true);
            RequestStatus promoteStatus = api.promote(name, buildNumber, options);
            assertNotNull(promoteStatus);
            assertTrue(promoteStatus.messages().size() == 1);
            assertTrue(promoteStatus.messages().get(0).level().equals("ERROR"));
            assertTrue(promoteStatus.messages().get(0).message().startsWith("Skipping promotion"));
            assertSent(server, "POST", "/api/build/promote/DevTest/999", MediaType.APPLICATION_JSON);
        } finally {
            jcloudsApi.close();
            server.shutdown();
        }
    }
}

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

import com.sun.media.jfxmedia.Media;
import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.search.AQLResult;
import com.cdancy.artifactory.rest.features.SearchApi;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.ws.rs.core.MediaType;

/**
 * Mock tests for the {@link com.cdancy.artifactory.rest.features.SearchApi}
 * class.
 */
@Test(groups = "unit", testName = "SearchApiMockTest")
public class SearchApiMockTest extends BaseArtifactoryMockTest {

   public void testAql() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/aql.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {
         AQLResult res = api.aql("items.find({\"repo\":{\"$eq\":\"libs-release-local\"}})");
         assertNotNull(res);
         assertTrue(res.results().size() == 1);
         assertSent(server, "POST", "/api/search/aql", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

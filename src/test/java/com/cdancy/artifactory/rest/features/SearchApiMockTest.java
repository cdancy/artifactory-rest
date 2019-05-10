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

import static com.cdancy.artifactory.rest.TestUtilities.assertNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertNotNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertTrue;

import com.cdancy.artifactory.rest.domain.search.Mapping;
import com.cdancy.artifactory.rest.domain.search.SearchBuildArtifacts;
import com.cdancy.artifactory.rest.domain.search.SearchResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.domain.search.AQLResult;
import com.cdancy.artifactory.rest.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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

   public void testAqlWithNonAvailableArtifact() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/aql-empty.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {
         AQLResult res = api.aql("items.find({\"repo\":{\"$eq\":\"non-existent-repo\"}, \"@whatever\":\"true\"})");
         assertNotNull(res);
         assertTrue(res.results().size() == 0);
         assertSent(server, "POST", "/api/search/aql", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testBuildArtifacts() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/build-artifacts.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {
         SearchBuildArtifacts searchBuildArtifacts = SearchBuildArtifacts.create("error-name",
                 "15",
                 "Released",
                 Lists.newArrayList("libs-release-local"),
                 Lists.newArrayList(Mapping.create("(.+)-sources.jar"),
                         Mapping.create("(.+)-javadoc.jar")));
         List<SearchResult> res = api.buildArtifacts(searchBuildArtifacts);
         assertNotNull(res);
         assertTrue(res.size() == 2);
         assertSent(server, "POST", "/api/search/buildArtifacts", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testBuildArtifactsNonExistent() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/build-artifacts.json")).setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {
         SearchBuildArtifacts searchBuildArtifacts = SearchBuildArtifacts.create("error-name",
                 "15",
                 "Released",
                 Lists.newArrayList("libs-release-local"),
                 Lists.newArrayList(Mapping.create("(.+)-sources.jar"),
                         Mapping.create("(.+)-javadoc.jar")));
         List<SearchResult> res = api.buildArtifacts(searchBuildArtifacts);
         assertNotNull(res);
         assertTrue(res.size() == 0);
         assertSent(server, "POST", "/api/search/buildArtifacts", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGavcSearch() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/gavc-search.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("jcenter-cache");
         List<SearchResult> res = api.gavcSearch("ant-contrib", "ant-contrib", "1.0b3", null, repos);
         assertNotNull(res);
         assertTrue(res.size() > 0);
         assertSent(server, "GET", "/api/search/gavc?g=ant-contrib&a=ant-contrib&v=1.0b3&repos=jcenter-cache", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGavcSearchNoResults() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/gavc-search-empty.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("jcenter-cache");
         List<SearchResult> res = api.gavcSearch("hello", "world", "999", null, repos);
         assertNotNull(res);
         assertTrue(res.size() == 0);
         assertSent(server, "GET", "/api/search/gavc?g=hello&a=world&v=999&repos=jcenter-cache", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testPropertySearch() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/property-search.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("libs-release-local", "ext-release-local");
         Map<String, List<String>> props = ImmutableMap.<String, List<String>>of("hello", ImmutableList.of("hello1", "hello2"),
                 "world", ImmutableList.of("world1", "world2"));
         List<SearchResult> res = api.propertySearch(props, repos);
         assertNotNull(res);
         assertTrue(res.size() == 2);
         assertSent(server, "GET", "/api/search/prop?hello=hello1,hello2&world=world1,world2&repos=libs-release-local,ext-release-local", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testPropertySearchWithNotFoundProperty() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/property-search-empty.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("libs-release-local", "ext-release-local");
         Map<String, List<String>> props = ImmutableMap.<String, List<String>>of("hello", ImmutableList.of("hello1", "hello2"),
                 "world", ImmutableList.of("world1", "world2"));
         List<SearchResult> res = api.propertySearch(props, repos);
         assertNotNull(res);
         assertTrue(res.size() == 0);
         assertSent(server, "GET", "/api/search/prop?hello=hello1,hello2&world=world1,world2&repos=libs-release-local,ext-release-local", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testNotDownloadedSince() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/not-downloaded-since-search.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("libs-release-local", "ext-release-local");
         List<SearchResult> res = api.notDownloadedSince(12345, Long.valueOf(56789), repos);
         assertNotNull(res);
         assertTrue(res.size() == 2);
         assertSent(server, "GET", "/api/search/usage?notUsedSince=12345&createdBefore=56789&repos=libs-release-local,ext-release-local", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testNotDownloadedSinceWithNoMatches() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/property-search-empty.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("libs-release-local", "ext-release-local");
         List<SearchResult> res = api.notDownloadedSince(12345, null, repos);
         assertNotNull(res);
         assertTrue(res.size() == 0);
         assertSent(server, "GET", "/api/search/usage?notUsedSince=12345&repos=libs-release-local,ext-release-local", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testLatestVersionWithLayout() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody("1.0b3").setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("jcenter-cache");
         String remote = "1";
         String res = api.latestVersionWithLayout("ant-contrib", "ant-contrib", "1.0*", remote, repos);
         assertNotNull(res);
         assertSent(server, "GET", "/api/search/latestVersion?g=ant-contrib&a=ant-contrib&v=1.0%2A&remote=1&repos=jcenter-cache", MediaType.TEXT_PLAIN);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testLatestVersionWithLayoutNotFound() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody("1.0b3").setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      SearchApi api = jcloudsApi.searchApi();
      try {

         List<String> repos = ImmutableList.of("jcenter-cache");
         String remote = "1";
         String res = api.latestVersionWithLayout("ant-contrib", "ant-contrib", "9.0*", remote, repos);
         assertNull(res);
         assertSent(server, "GET", "/api/search/latestVersion?g=ant-contrib&a=ant-contrib&v=9.0%2A&remote=1&repos=jcenter-cache", MediaType.TEXT_PLAIN);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

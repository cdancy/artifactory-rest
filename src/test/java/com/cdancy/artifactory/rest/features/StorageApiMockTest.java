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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.cdancy.artifactory.rest.domain.storage.FileList;
import com.cdancy.artifactory.rest.domain.storage.StorageInfo;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.BaseArtifactoryMockTest;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock tests for the {@link com.cdancy.artifactory.rest.features.StorageApi}
 * class.
 */
@Test(groups = "unit", testName = "StorageApiMockTest")
public class StorageApiMockTest extends BaseArtifactoryMockTest {

   public void testSetItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
          Map<String, List<String>> props = new HashMap<>();
          props.put("hello", Lists.newArrayList("world"));
          props.put("bear", Lists.newArrayList("fish"));

          boolean itemSet = api.setItemProperties("libs-snapshot-local", "hello/world", props);
          assertTrue(itemSet);
          assertSent(server, "PUT", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=bear%3Dfish%7Chello%3Dworld", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGetItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/item-properties.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         Map<String, List<String>> itemProperties = api.getItemProperties("libs-snapshot-local", "hello/world");
         assertNotNull(itemProperties);
         assertTrue(itemProperties.size() > 0);
         assertTrue(itemProperties.containsKey("hello"));
         assertTrue(itemProperties.get("hello").contains("world"));
         assertSent(server, "GET", "/api/storage/libs-snapshot-local/hello/world?properties", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGetItemWithNoProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         Map<String, List<String>> itemProperties = api.getItemProperties("libs-snapshot-local", "hello/world");
         assertNotNull(itemProperties);
         assertTrue(itemProperties.size() == 0);
         assertSent(server, "GET", "/api/storage/libs-snapshot-local/hello/world?properties", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(204));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         List<String> props = new ArrayList<String>();
         props.add("hello");
         props.add("world");
         boolean propertyDeleted = api.deleteItemProperties("libs-snapshot-local", "hello/world", props);
         assertTrue(propertyDeleted);
         assertSent(server, "DELETE", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=hello,world", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testDeleteNonExistentItemProperties() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setResponseCode(404));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         List<String> props = new ArrayList<String>();
         props.add("hello");
         props.add("world");
         boolean propertyDeleted = api.deleteItemProperties("libs-snapshot-local", "hello/world", props);
         assertFalse(propertyDeleted);
         assertSent(server, "DELETE", "/api/storage/libs-snapshot-local/hello/world?recursive=1&properties=hello,world", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testGetStorageInfo() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/storage-info.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         StorageInfo storageInfo = api.storageInfo();
         assertNotNull(storageInfo);
         assertTrue(storageInfo.binariesSummary().binariesCount().equals("125,726"));
         assertTrue(storageInfo.fileStoreSummary().storageType().equals("filesystem"));
         assertTrue(storageInfo.repositoriesSummaryList().size() == 3);
         assertSent(server, "GET", "/api/storageinfo", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }

   public void testFileList() throws Exception {
      MockWebServer server = mockArtifactoryJavaWebServer();

      server.enqueue(new MockResponse().setBody(payloadFromResource("/file-list.json")).setResponseCode(200));
      ArtifactoryApi jcloudsApi = api(server.getUrl("/"));
      StorageApi api = jcloudsApi.storageApi();
      try {
         FileList ref = api.fileList("libs-release-local", "org/acme", 1, 1, 1, 1);
         assertNotNull(ref);
         assertTrue(ref.files().size() == 3);
         assertSent(server, "GET", "/api/storage/libs-release-local/org/acme?list=true&deep=1&depth=1&listFolders=1&includeRootPath=1", MediaType.APPLICATION_JSON);
      } finally {
         jcloudsApi.close();
         server.shutdown();
      }
   }
}

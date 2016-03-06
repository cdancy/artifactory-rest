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

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryApi;
import com.cdancy.artifactory.rest.internal.BaseArtifactoryMockTest;
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
          Map<String, String> props = new HashMap<String, String>();
          props.put("hello", "world");
          props.put("bear", "fish");

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
}

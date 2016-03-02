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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.io.Payloads;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;

@Test(groups = "live", testName = "StorageApiLiveTest")
public class StorageApiLiveTest extends BaseArtifactoryApiLiveTest {

   private File tempArtifact;
   private String repoKey = "libs-snapshot-local";
   private String itemPath;
   private Artifact artifact;

   @BeforeClass
   public void testInitialize() {
      tempArtifact = randomFile();
      itemPath = randomPath();
      artifact = api.artifactApi().deployArtifact(repoKey, itemPath + "/" + tempArtifact.getName(),
            Payloads.newPayload(tempArtifact), null);
      assertNotNull(artifact);
      assertTrue(artifact.repo().equals(repoKey));
   }

   @Test
   public void testSetItemProperties() {
       Map<String, String> props = new HashMap<String, String>();
       props.put("hello", "world");
       props.put("bear", "fish");
       boolean itemSet = api().setItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
       assertTrue(itemSet);
   }

   @Test(dependsOnMethods = "testSetItemProperties")
   public void testGetItemProperties() {
       Map<String, List<String>> itemProperties = api().getItemProperties(repoKey, artifact.path().replaceFirst("/", ""));
       assertNotNull(itemProperties);
       assertTrue(itemProperties.size() > 0);
       assertTrue(itemProperties.containsKey("hello"));
       assertTrue(itemProperties.get("hello").contains("world"));
       assertTrue(itemProperties.containsKey("bear"));
       assertTrue(itemProperties.get("bear").contains("fish"));
   }

   @Test(dependsOnMethods = "testGetItemProperties")
   public void testDeleteItemProperties() {
       Map<String, String> props = new HashMap<String, String>();
       props.put("hello", "world");
       boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
       assertTrue(itemDelete);
   }

   @Test(dependsOnMethods = "testDeleteItemProperties")
   public void testDeleteNonExistentItemProperties() {
      Map<String, String> props = new HashMap<String, String>();
      props.put("hello", "world");
      boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
      assertTrue(itemDelete);
   }

   @AfterClass
   public void testFinalize() {
      assertTrue(tempArtifact.delete());
      assertTrue(api.artifactApi().deleteArtifact(repoKey, itemPath.split("/")[0]));
   }

   private StorageApi api() {
      return api.storageApi();
   }
}

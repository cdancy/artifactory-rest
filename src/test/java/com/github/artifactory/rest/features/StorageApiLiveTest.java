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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.jclouds.io.Payloads;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.github.artifactory.rest.domain.artifact.Artifact;
import com.github.artifactory.rest.domain.storage.ItemProperties;
import com.github.artifactory.rest.options.CreateItemProperties;
import com.github.artifactory.rest.options.DeleteItemProperties;

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
            Payloads.newPayload(tempArtifact));
      assertNotNull(artifact);
      assertTrue(artifact.repo().equals(repoKey));
   }

   @Test
   public void testSetItemProperties() {
      boolean itemSet = api().setItemProperties(repoKey, artifact.path().replaceFirst("/", ""),
            CreateItemProperties.Builder.add("hello", "world"));
      assertTrue(itemSet);
   }

   @Test(dependsOnMethods = "testSetItemProperties")
   public void testGetItemProperties() {
      ItemProperties itemProperties = api().getItemProperties(repoKey, artifact.path().replaceFirst("/", ""));
      assertNotNull(itemProperties);
   }

   @Test(dependsOnMethods = "testGetItemProperties")
   public void testDeleteItemProperties() {
      boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""),
            DeleteItemProperties.Builder.add("hello"));
      assertTrue(itemDelete);
   }

   @Test(dependsOnMethods = "testDeleteItemProperties")
   public void testDeleteNonExistentItemProperties() {
      boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""),
            DeleteItemProperties.Builder.add("hello"));
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

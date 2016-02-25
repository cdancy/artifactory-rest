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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.jclouds.io.Payloads;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.github.artifactory.rest.domain.artifact.Artifact;

@Test(groups = "live", testName = "ArtifactApiLiveTest")
public class ArtifactApiLiveTest extends BaseArtifactoryApiLiveTest {

   private File tempArtifact;
   private String repoKey = "libs-snapshot-local";
   private String itemPath;

   @BeforeClass
   public void testInitialize() {
      tempArtifact = randomFile();
      itemPath = randomPath();
   }

   @Test
   public void testDeployArtifact() {
      Artifact artifact = api().deployArtifact(repoKey, itemPath + "/" + tempArtifact.getName(),
            Payloads.newPayload(tempArtifact));
      assertNotNull(artifact);
      assertTrue(artifact.repo().equals(repoKey));
   }

   @Test(dependsOnMethods = "testDeployArtifact")
   public void testDeleteArtifact() {
      assertTrue(api().deleteArtifact(repoKey, itemPath));
   }

   @Test
   public void testDeleteNonExistentArtifact() {
      assertFalse(api().deleteArtifact(repoKey, randomPath()));
   }

   @AfterClass
   public void testFinalize() {
      assertTrue(tempArtifact.delete());
      assertTrue(api().deleteArtifact(repoKey, itemPath.split("/")[0]));
   }

   private ArtifactApi api() {
      return api.artifactApi();
   }
}

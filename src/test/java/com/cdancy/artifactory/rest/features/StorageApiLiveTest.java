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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cdancy.artifactory.rest.domain.storage.FileList;
import com.cdancy.artifactory.rest.domain.storage.StorageInfo;
import com.google.common.collect.Lists;
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
        Map<String, List<String>> props = new HashMap<>();
        props.put("hello", Lists.newArrayList("world"));
        props.put("bear", Lists.newArrayList("fish"));
        boolean itemSet = api().setItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
        assertTrue(itemSet);
    }

    @Test(dependsOnMethods = "testSetItemProperties")
    public void testListFiles() {
        FileList ref = api().fileList(repoKey, itemPath, 1, 1, 1, 1);
        assertNotNull(ref);
        assertTrue(ref.files().size() > 0);
    }

    @Test(dependsOnMethods = "testListFiles")
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
        List<String> props = new ArrayList<String>();
        props.add("hello");
        boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
        assertTrue(itemDelete);
    }

    @Test(dependsOnMethods = "testDeleteItemProperties")
    public void testGetItemPropertiesAfterDelete() {
        Map<String, List<String>> itemProperties = api().getItemProperties(repoKey, artifact.path().replaceFirst("/", ""));
        assertNotNull(itemProperties);
        assertTrue(itemProperties.size() > 0);
        assertFalse(itemProperties.containsKey("hello"));
    }

    @Test(dependsOnMethods = "testGetItemPropertiesAfterDelete")
    public void testDeleteNonExistentItemProperties() {
        List<String> props = new ArrayList<String>();
        props.add("hello");
        props.add("world");
        boolean itemDelete = api().deleteItemProperties(repoKey, artifact.path().replaceFirst("/", ""), props);
        assertTrue(itemDelete);
    }

    @Test
    public void testGetStorageInfo() {
        StorageInfo storageInfo = api().storageInfo();
        assertNotNull(storageInfo);
        assertTrue(storageInfo.repositoriesSummaryList().size() > 0);
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

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

import static com.cdancy.artifactory.rest.TestUtilities.assertFalse;
import static com.cdancy.artifactory.rest.TestUtilities.assertNotNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertNull;
import static com.cdancy.artifactory.rest.TestUtilities.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import com.google.common.collect.Lists;
import org.jclouds.io.Payloads;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ArtifactApiLiveTest")
public class ArtifactApiLiveTest extends BaseArtifactoryApiLiveTest {

    private File tempArtifact;
    private File tempArchiveArtifact;
    private File archiveFileEntryOne;
    private File archiveFileEntryTwo;
    private String repoKey = "libs-snapshot-local";
    private String repoReleaseKey = "ext-snapshot-local";
    private String itemPath;
    private String itemPathWithProperties;
    private Map<String, List<String>> itemProperties = new HashMap<>();

    @BeforeClass
    public void testInitialize() {
        tempArtifact = randomFile();
        archiveFileEntryOne = randomFile();
        archiveFileEntryTwo = randomFile();
        tempArchiveArtifact = createRandomArchiveFile(new File[]{archiveFileEntryOne, archiveFileEntryTwo});
        itemPath = randomPath();
        itemPathWithProperties = randomPath();
        itemProperties.put("key1", Lists.newArrayList("value1"));
        itemProperties.put("key2", Lists.newArrayList("value2"));
        itemProperties.put("key3", Lists.newArrayList("value3"));
    }

    @Test
    public void testDeployArtifact() {
        Artifact artifact = api().deployArtifact(repoKey, itemPath + "/" + tempArtifact.getName(),
            Payloads.newPayload(tempArtifact), null);
        assertNotNull(artifact);
        assertTrue(artifact.repo().equals(repoKey));
    }

    @Test(dependsOnMethods = "testDeployArtifact")
    public void testDownloadArtifact() {
        InputStream is = api().downloadArtifact(repoKey, itemPath, false);
        assertNotNull(is);
    }

    @Test(dependsOnMethods = "testDownloadArtifact")
    public void testDownloadNonExistentArtifact() {
        InputStream is = api().downloadArtifact(repoKey, "non/existent/artifact", false);
        assertNull(is);
    }

    public void testDeployArchiveArtifact() {
        Artifact artifact = api().deployArtifact(repoKey, itemPath + "/" + tempArchiveArtifact.getName(),
            Payloads.newPayload(tempArchiveArtifact), null);
        assertNotNull(artifact);
        assertTrue(artifact.repo().equals(repoKey));
    }

    @Test(dependsOnMethods = "testDeployArchiveArtifact")
    public void testDownloadArchiveEntry() {
        InputStream isOne = api().downloadArchiveEntry(repoKey, itemPath + "/" + tempArchiveArtifact.getName(), archiveFileEntryOne.getName());
        assertNotNull(isOne);
        String textOne = new BufferedReader(new InputStreamReader(isOne, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        assertTrue(textOne.equals("Hello, World!"));

        InputStream isTwo = api().downloadArchiveEntry(repoKey, itemPath + "/" + tempArchiveArtifact.getName(), archiveFileEntryTwo.getName());
        assertNotNull(isTwo);
        String textTwo = new BufferedReader(new InputStreamReader(isTwo, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        assertTrue(textTwo.equals("Hello, World!"));

    }

    public  void testDownloadNonExistentArchiveEntry() {
        InputStream is = api().downloadArchiveEntry(repoKey, itemPath + "/" + tempArchiveArtifact.getName(), "non-existent-file.txt");
        assertNull(is);
    }

    @Test (dependsOnMethods = "testDeployArtifact")
    public void testCopyArtifact() {
        RequestStatus requestStatus = api().copyArtifact(repoKey, itemPath, repoReleaseKey + "/" + itemPath);
        assertNotNull(requestStatus);
        assertTrue(requestStatus.errors().size() == 0);
        assertTrue(requestStatus.messages().size() == 1);
        assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("info"));
    }

    @Test (dependsOnMethods = "testCopyArtifact")
    public void testCopyNonExistentArtifact() {
        RequestStatus requestStatus = api().copyArtifact(repoKey, randomPath(), repoReleaseKey + "/" + randomPath());
        assertNotNull(requestStatus);
        assertTrue(requestStatus.errors().size() == 0);
        assertTrue(requestStatus.messages().size() == 1);
        assertTrue(requestStatus.messages().get(0).level().equalsIgnoreCase("error"));
    }

    @Test(dependsOnMethods = "testCopyNonExistentArtifact")
    public void testDeleteArtifact() {
        assertTrue(api().deleteArtifact(repoKey, itemPath));
        assertTrue(api().deleteArtifact(repoReleaseKey, itemPath));
    }

    @Test
    public void testDeployArtifactWithProperties() {
        Artifact artifact = api().deployArtifact(repoKey, itemPathWithProperties + "/" + tempArtifact.getName(),
                Payloads.newPayload(tempArtifact), itemProperties);
        assertNotNull(artifact);
        assertTrue(artifact.repo().equals(repoKey));
    }

    @Test(dependsOnMethods = "testDeployArtifactWithProperties")
    public void testDeleteArtifactWithProperties() {
        assertTrue(api().deleteArtifact(repoKey, itemPathWithProperties));
    }

    @Test
    public void testDeleteNonExistentArtifact() {
        assertFalse(api().deleteArtifact(repoKey, randomPath()));
    }

    @AfterClass
    public void testFinalize() {
        assertTrue(tempArtifact.delete());
        assertTrue(tempArchiveArtifact.delete());
        assertTrue(archiveFileEntryOne.delete());
        assertTrue(archiveFileEntryTwo.delete());
        assertTrue(api().deleteArtifact(repoReleaseKey, itemPath.split("/")[0]));
        assertTrue(api().deleteArtifact(repoKey, itemPath.split("/")[0]));
        assertTrue(api().deleteArtifact(repoKey, itemPathWithProperties.split("/")[0]));
    }

    private ArtifactApi api() {
      return api.artifactApi();
    }
}

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

import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import com.cdancy.artifactory.rest.options.PromoteBuildOptions;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.jclouds.io.Payloads;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "BuildApiLiveTest")
public class BuildApiLiveTest extends BaseArtifactoryApiLiveTest {

    private File tempArtifact;
    private String repoKey = "libs-snapshot-local";
    private String repoReleaseKey = "ext-snapshot-local";
    private String itemPath;

    private String buildName = "Random Dev Test";
    private int buildNumber = 999;

    @BeforeClass
    public void testInitialize() {
        tempArtifact = randomFile();
        itemPath = randomPath();

        Map<String, List<String>> properties = new HashMap<>();
        properties.put("build.name", Lists.newArrayList(buildName));
        properties.put("build.number", Lists.newArrayList("" + buildNumber));

        Artifact artifact = api.artifactApi().deployArtifact(repoKey, itemPath + "/" + tempArtifact.getName(),
                Payloads.newPayload(tempArtifact), properties);
        assertNotNull(artifact);
        assertTrue(artifact.repo().equals(repoKey));
    }

    @Test
    public void testPromoteBuildCantFind() {
        PromoteBuildOptions options = PromoteBuildOptions.create("HelloWorld", "possible promote", "cambuild", null, false, repoKey, repoReleaseKey, true, true, false, null, null, true);
        RequestStatus promoteStatus = api().promote(buildName, buildNumber, options);
        assertNotNull(promoteStatus);
        assertTrue(promoteStatus.errors().size() == 1);
        assertTrue(promoteStatus.errors().get(0).status() == 404);
        assertTrue(promoteStatus.errors().get(0).message().startsWith("Cannot find builds by the name"));
    }

    @AfterClass
    public void testFinalize() {
        assertTrue(tempArtifact.delete());
        assertTrue(api.artifactApi().deleteArtifact(repoKey, itemPath.split("/")[0]));
    }

    private BuildApi api() {
      return api.buildApi();
    }
}

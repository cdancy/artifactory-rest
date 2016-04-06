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
import com.cdancy.artifactory.rest.domain.docker.PromoteImage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Test(groups = "live", testName = "DockerApiLiveTest")
public class DockerApiLiveTest extends BaseArtifactoryApiLiveTest {

    private String dockerRepo;
    private String dockerRepoPromoted;
    private String dockerImage;
    private String dockerTag;

    @BeforeClass
    public void init() {
        dockerRepo = System.getProperty("test.artifactory.docker.repo");
        dockerRepoPromoted = System.getProperty("test.artifactory.docker.repo.promoted");
        dockerImage = System.getProperty("test.artifactory.docker.image");
        dockerTag = System.getProperty("test.artifactory.docker.tag");
        assertNotNull(dockerRepo);
        assertNotNull(dockerRepoPromoted);
        assertNotNull(dockerImage);
        assertNotNull(dockerTag);
    }

    @Test
    public void testPromote() {
        PromoteImage dockerPromote = PromoteImage.create(dockerRepoPromoted, dockerImage, dockerTag, true);
        boolean success = api().promote(dockerRepo, dockerPromote);
        assertTrue(success);
    }

    @Test
    public void testPromoteNonExistentImage() {
        PromoteImage dockerPromote = PromoteImage.create(dockerRepoPromoted, dockerImage, "0009", false);
        boolean success = api().promote(dockerRepo, dockerPromote);
        assertFalse(success);
    }

    @Test (dependsOnMethods = "testPromote")
    public void testListRepositories() {
        List<String> repos = api().repositories(dockerRepoPromoted);
        assertNotNull(repos);
        assertTrue(repos.size() > 0);
    }

    private DockerApi api() {
      return api.dockerApi();
    }
}

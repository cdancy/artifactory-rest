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

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.ArtifactoryClient;
import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.domain.system.Version;

@Test(groups = "live", testName = "ArtifactoryClientLiveTest")
public class ArtifactoryClientLiveTest extends BaseArtifactoryApiLiveTest {

   @Test
   public void testCreateClient() {
      ArtifactoryClient client = new ArtifactoryClient.Builder().endPoint(this.endpoint).credentials(this.credential)
            .build();

      Version version = client.api().systemApi().version();
      assertNotNull(version);
   }
}

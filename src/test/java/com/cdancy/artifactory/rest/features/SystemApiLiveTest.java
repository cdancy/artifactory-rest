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

import org.testng.annotations.Test;

import com.cdancy.artifactory.rest.BaseArtifactoryApiLiveTest;
import com.cdancy.artifactory.rest.domain.system.Version;
import com.cdancy.artifactory.rest.features.SystemApi;

@Test(groups = "live", testName = "SystemApiLiveTest")
public class SystemApiLiveTest extends BaseArtifactoryApiLiveTest {

   private final String versionRegex = "^\\d+\\.\\d+\\.\\d+$";

   @Test
   public void testGetVersion() {
      Version version = api().version();
      assertNotNull(version);
      assertTrue(version.version().matches(versionRegex));
   }

   private SystemApi api() {
      return api.systemApi();
   }
}

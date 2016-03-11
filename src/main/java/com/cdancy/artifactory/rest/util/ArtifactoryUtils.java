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
package com.cdancy.artifactory.rest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ArtifactoryUtils {

   public static final String LOCATION_HEADER = "Artifactory-Rest-File-Location";

   public static File resolveInputStream(InputStream inputStream, File destination) {

      FileOutputStream outputStream = null;
      try {

         if (destination.exists()) {
            if (!destination.delete()) {
               throw new RuntimeException("Failed to delete previous file @ " + destination.getAbsolutePath());
            }
         }

         byte[] buffer = new byte[8192];
         outputStream = new FileOutputStream(destination);
         IOUtils.copyLarge(inputStream, outputStream, buffer);

      } catch (Exception e) {
         Throwables.propagate(e);
      } finally {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (Exception e2) {
               Throwables.propagate(e2);
            }
         }
      }

      return destination;
   }

   public static File getGradleFile(GAVCoordinates gavCoordinates, String fileName, String etag) {
      File gradleFile = new File(getGradleFilesDir(), gavCoordinates.group +
              "/" + gavCoordinates.artifact +
              "/" + gavCoordinates.version +
              "/" + etag +
              "/" + fileName);
      if (!gradleFile.getParentFile().exists()) {
         try {
            FileUtils.forceMkdir(gradleFile.getParentFile());
         } catch (Exception e) {
            Throwables.propagate(e);
         }
      }

      return gradleFile;
   }

   public static String getBasePath(URL url, String endpoint) {
      String pathFromUrl = url.toString().replaceFirst(endpoint, "");
      if (url.getQuery() != null) {
         int index = url.getQuery().lastIndexOf("?");
         if (index != -1) {
            pathFromUrl = url.getQuery().substring(0, index);
         }
      }

      return pathFromUrl;
   }

   public static GAVCoordinates gavFromURL(URL url, URL endpoint) {
      GAVCoordinates gavCoordinates = new GAVCoordinates();

      String sanitizedURL = getBasePath(url, endpoint.toString());
      String [] sanitizedURLParts = sanitizedURL.split("/");

      List<String> gavArray = Arrays.asList(Arrays.copyOfRange(sanitizedURLParts, 2, sanitizedURLParts.length));
      List<String> groupArray = gavArray.subList(0, gavArray.size() - 3);

      gavCoordinates.group = collectionToString(groupArray, ".");
      gavCoordinates.artifact = gavArray.get(groupArray.size());
      gavCoordinates.version = gavArray.get(groupArray.size() + 1);

      return gavCoordinates;
   }

   public static File getGradleFilesDir() {
      File gradleFilesDir = new File(getGradleHome(), "/caches/modules-2/files-2.1");
      if (!gradleFilesDir.exists()) {
         try {
            FileUtils.forceMkdir(gradleFilesDir);
         } catch (Exception e) {
            Throwables.propagate(e);
         }
      }
      return gradleFilesDir;
   }

   public static File getGradleHome() {
      String possibleGradleHome = System.getenv("GRADLE_HOME");
      if (possibleGradleHome == null) {
         possibleGradleHome = System.getProperty("user.home") + "/.gradle";
      }
      File gradleHome = new File(possibleGradleHome);
      if (!gradleHome.exists()) {
         try {
            FileUtils.forceMkdir(gradleHome);
         } catch (Exception e) {
            Throwables.propagate(e);
         }
      }
      return gradleHome;
   }

   public static String collectionToString(Collection<String> collection, String separator) {
      if (collection == null || collection.size() == 0) return null;
      Joiner joiner = Joiner.on(separator != null ? separator : ",").skipNulls();
      return joiner.join(collection);
   }
}

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
package com.cdancy.artifactory.rest;

import com.cdancy.artifactory.rest.config.ArtifactoryAuthenticationModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.jclouds.Constants;
import org.jclouds.apis.BaseApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live")
public class BaseArtifactoryApiLiveTest extends BaseApiLiveTest<ArtifactoryApi> {
    protected final ArtifactoryAuthentication artifactoryAuthentication;

    public BaseArtifactoryApiLiveTest() {
        provider = "artifactory";
        this.artifactoryAuthentication = TestUtilities.inferTestAuthentication();
    }

    @Override
    protected Iterable<Module> setupModules() {
        final ArtifactoryAuthenticationModule credsModule = new ArtifactoryAuthenticationModule(this.artifactoryAuthentication);
        return ImmutableSet.<Module> of(getLoggingModule(), credsModule);
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();
        overrides.setProperty(Constants.PROPERTY_MAX_RETRIES, "0");
        return overrides;
    }

    public String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String randomPath() {
        return UUID.randomUUID().toString().replaceAll("-", "/");
    }

    public String randomString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
           char c = chars[random.nextInt(chars.length)];
           sb.append(c);
        }
        return sb.toString();
    }

    public File randomFile() {
        File randomFile = null;
        PrintWriter writer = null;
        try {
            randomFile = new File(System.getProperty("java.io.tmpdir"), randomUUID() + ".txt");
            if (!randomFile.createNewFile()) {
                throw new RuntimeException("Could not create temporary file at " + randomFile.getAbsolutePath());
            }

            writer = new PrintWriter(randomFile, "UTF-8");
            writer.println("Hello, World!");
            writer.close();
        } catch (IOException e) {
            if (randomFile != null) {
               randomFile.delete();
            }
            throw Throwables.propagate(e);
        } finally {
            if (writer != null)
                writer.close();
        }
        return randomFile;
    }

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}

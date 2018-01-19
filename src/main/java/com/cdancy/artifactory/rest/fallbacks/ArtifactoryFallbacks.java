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
package com.cdancy.artifactory.rest.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdancy.artifactory.rest.domain.error.Error;
import com.cdancy.artifactory.rest.domain.error.Message;
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import org.jclouds.Fallback;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ArtifactoryFallbacks {

    private static final JsonParser PARSER = new JsonParser();

    public static final class RequestStatusFromError implements Fallback<Object> {
        public Object createOrPropagate(Throwable t) throws Exception {
            if (checkNotNull(t, "throwable") != null) {
                RequestStatus status = createPromoteBuildFromError(t.getMessage());
                if (status != null) {
                    return status;
                }
            }
            throw propagate(t);
        }
    }

    public static RequestStatus createPromoteBuildFromError(String message) {
        List<Message> messages = new ArrayList<>();
        List<Error> errors = new ArrayList<>();

        JsonReader reader = new JsonReader(new StringReader(message));
        reader.setLenient(true);

        JsonElement possibleMessages = PARSER.parse(reader).getAsJsonObject().get("messages");
        if (possibleMessages != null) {
            JsonArray jsonArray = possibleMessages.getAsJsonArray();
            Iterator<JsonElement> iter = jsonArray.iterator();
            while(iter.hasNext()) {
                JsonElement jsonElement = iter.next();
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Message mess = Message.create(jsonObject.get("level").getAsString(), jsonObject.get("message").getAsString());
                messages.add(mess);
            }
        }

        JsonElement possibleErrors = PARSER.parse(message).getAsJsonObject().get("errors");
        if (possibleErrors != null) {
            JsonArray jsonArray = possibleErrors.getAsJsonArray();
            Iterator<JsonElement> iter = jsonArray.iterator();
            while(iter.hasNext()) {
                JsonElement jsonElement = iter.next();
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Error error = Error.create(jsonObject.get("status").getAsInt(), jsonObject.get("message").getAsString());
                errors.add(error);
            }
        }

        return RequestStatus.create(messages, errors);
    }
}
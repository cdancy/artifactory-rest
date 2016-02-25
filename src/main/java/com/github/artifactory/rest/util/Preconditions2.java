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
package com.github.artifactory.rest.util;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Preconditions not in guava.
 * 
 * original author was Adrian Cole
 */
public class Preconditions2 {

   /**
    * Will throw an exception if the argument is null or empty.
    * 
    * @param nullableString
    *           string to verify. Can be null or empty.
    */
   public static String checkNotEmpty(String nullableString) {
      return Preconditions2.checkNotEmpty(nullableString, "Argument can't be null or empty");
   }

   /**
    * Will throw an exception if the argument is null or empty. Accepts a custom
    * error message.
    * 
    * @param nullableString
    *           string to verify. Can be null or empty.
    * @param message
    *           message to show in case of exception
    */
   public static String checkNotEmpty(String nullableString, String message) {
      checkArgument(nullableString != null && nullableString.length() > 0, message);
      return nullableString;
   }
}

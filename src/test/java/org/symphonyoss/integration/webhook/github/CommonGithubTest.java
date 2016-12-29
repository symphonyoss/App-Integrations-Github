/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.github;

import org.symphonyoss.integration.json.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by cmarcondes on 9/15/16.
 */
@Ignore("not a test per se")
public class CommonGithubTest {

  protected ClassLoader classLoader = getClass().getClassLoader();

  protected String readFile(String file) throws IOException {
    return FileUtils.readFileToString(new File(classLoader.getResource(file).getPath()))
        .replaceAll("\n", "");
  }

  protected String getExpectedMessageML(String expectedMessageFileName) {
    Scanner scan = new Scanner(
        classLoader.getResourceAsStream(expectedMessageFileName));
    StringBuilder expectedMessage = new StringBuilder();
    try {
      while (scan.hasNextLine()) {
        expectedMessage.append(scan.nextLine().trim());
      }
    } finally {
      scan.close();
    }
    return expectedMessage.toString();
  }

  protected JsonNode getJsonFile(String jsonFileName) throws IOException {
    return JsonUtils.readTree(classLoader.getResourceAsStream(jsonFileName));
  }
}

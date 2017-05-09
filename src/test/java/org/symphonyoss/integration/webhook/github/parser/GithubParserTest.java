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

package org.symphonyoss.integration.webhook.github.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Ignore;
import org.symphonyoss.integration.json.JsonUtils;

import java.io.IOException;

/**
 * Created by campidelli on 08/05/17.
 */
@Ignore("not a test per se")
public class GithubParserTest {

  protected JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }

}

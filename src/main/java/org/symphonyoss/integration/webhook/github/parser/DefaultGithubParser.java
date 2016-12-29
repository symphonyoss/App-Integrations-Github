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
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Parser to convert a received Json payload message from GitHub into a proper Symphony MessageML.
 * This is a default parser, it shouldn't do anything.
 *
 * Created by Milton Quilzini on 07/09/16.
 */
@Component
public class DefaultGithubParser implements GithubParser {

  @Override
  public List<String> getEvents() {
    return Collections.emptyList();
  }

  @Override
  public String parse(Map<String, String> parameters, JsonNode node) throws GithubParserException {
    return null;
  }
}
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

import java.util.List;
import java.util.Map;

/**
 * Interface that defines methods to validate GITHUB messages.
 *
 * Created by Milton Quilzini on 06/09/16.
 */
public interface GithubParser {

  /**
   * Integration identifier tag.
   * Required for usage with {@link com.symphony.integration.entity.EntityBuilder}
   */
  String INTEGRATION_TAG = "github";

  /**
   * Returns the supported events from one's parser.
   * @return a {@link List} with the supported events.
   */
  List<String> getEvents();

  /**
   * Parse a received Json message into a Symphony MessageML format.
   * @param parameters request's query string parameters.
   * @param node Json message.
   * @return Symphony MessageML converted message.
   * @throws GithubParserException when there's insufficient information to validate the message.
   */
  String parse(Map<String, String> parameters, JsonNode node) throws GithubParserException;

}

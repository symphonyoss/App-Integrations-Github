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

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_HEADER_EVENT_NAME;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Common methods to retrieve the parser according to received event.
 * Created by campidelli on 02/05/17.
 */
public abstract class GithubParserFactory implements WebHookParserFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(GithubParserFactory.class);

  protected Map<String, GithubParser> parsers = new HashMap<>();

  @Autowired
  private NullGithubParser defaultGithubParser;

  /**
   * Map the event type to the parser.
   */
  @PostConstruct
  public void init() {
    for (GithubParser parser : getBeans()) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  /**
   * Update the integration username on each parser class. This process is required to know which
   * user
   * must be used to query the Symphony API's.
   * @param settings Integration settings
   */
  @Override
  public void onConfigChange(IntegrationSettings settings) {
    String user = settings.getType();

    for (GithubParser parser : getBeans()) {
      parser.setIntegrationUser(user);
    }
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    try {
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());
      GithubParser parser = getParser(rootNode);

      if (parser == null) {
        parser = defaultGithubParser;
      }

      return new GithubWebHookParserAdapter(parser);
    } catch (IOException e) {
      throw new GithubParserException("Cannot retrieve the payload event", e);
    }
  }

  /**
   * Get the parser class based on the event received from Github.
   *
   * The field used to do perform this selection is 'x-github-event'.
   * @param node Github event
   * @return Parser class to handle the event
   */
  public GithubParser getParser(JsonNode node) {
    String eventName = node.path(GITHUB_HEADER_EVENT_NAME).asText();
    GithubParser result = parsers.get(eventName);

    if (result == null) {
      LOGGER.debug("Unhandled event {}", eventName);
    }

    return result;
  }

  /**
   * Get a list of parsers supported by the factory.
   * @return list of parsers supported by the factory.
   */
  protected abstract List<GithubParser> getBeans();

}

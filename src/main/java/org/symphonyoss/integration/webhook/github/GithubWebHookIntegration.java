/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.github.parser.DefaultGithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_HEADER_EVENT_NAME;

/**
 * Implementation of a WebHook to integrate with GITHUB, rendering it's messages.
 * <p>
 * Created by Milton Quilzini on 06/09/16.
 */
@Component
public class GithubWebHookIntegration extends WebHookIntegration {

  private Map<String, GithubParser> parsers = new HashMap<>();

  @Autowired
  private DefaultGithubParser defaultGithubParser;

  @Autowired
  private List<GithubParser> gitHubBeans;

  @PostConstruct
  public void init() {
    // adds those with events to our parser map
    for (GithubParser parser : gitHubBeans) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  @Override
  public Message parse(WebHookPayload input) throws WebHookParseException {
    try {
      JsonNode rootNode = JsonUtils.readTree(input.getBody());
      Map<String, String> parameters = input.getParameters();

      String webHookEvent = input.getHeaders().get(GITHUB_HEADER_EVENT_NAME);

      GithubParser parser = getParser(webHookEvent);

      String formattedMessage = parser.parse(parameters, rootNode);

      return super.buildMessageML(formattedMessage, webHookEvent);
    } catch (IOException e) {
      throw new GithubParserException(
          "Something went wrong while trying to convert your message to the expected format", e);
    }
  }

  /**
   * Gets a GitHub parser based on the event.
   * If none is found, we return a default parser.
   * @param webHookEvent the webhook event being parsed.
   * @return the most adequate parser to handle the webHookEvent.
   */
  private GithubParser getParser(String webHookEvent) {
    GithubParser result = parsers.get(webHookEvent);

    if (result == null) {
      return defaultGithubParser;
    }

    return result;
  }

  /**
   * @see WebHookIntegration#getSupportedContentTypes()
   */
  @Override
  public List<MediaType> getSupportedContentTypes() {
    List<MediaType> supportedContentTypes = new ArrayList<>();
    supportedContentTypes.add(MediaType.APPLICATION_JSON_TYPE);
    return supportedContentTypes;
  }
}


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

import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_DEPLOYMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.DEPLOYMENT_TAG;

import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.parser.SafeString;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Parser to convert a received Json payload message from GitHub into a proper Symphony MessageML.
 * This is a parser to handle GitHub's "deployment" events.
 *
 * Created by rsanchez on 23/09/16.
 */
@Component
public class DeploymentGithubParser extends BaseDeploymentGithubParser {

  /**
   * Specific message to the "deployment" event.
   */
  private static final String DEPLOYMENT_MESSAGE = "Deployment created";

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_DEPLOYMENT);
  }

  @Override
  protected EntityBuilder createEntityBuilder(JsonNode node) throws URISyntaxException {
    SafeString presentationML = buildPresentationMl(node);

    EntityBuilder builder =
        EntityBuilder.forIntegrationEvent(INTEGRATION_TAG, GITHUB_EVENT_DEPLOYMENT)
            .presentationML(presentationML);
    fillEntityDeploymentInfo(builder, node);

    return builder;
  }

  @Override
  protected SafeString getSpecificMessage(JsonNode baseNode) {
    return new SafeString(DEPLOYMENT_MESSAGE);
  }

  @Override
  protected JsonNode getEventNode(JsonNode baseNode) {
    return baseNode.path(DEPLOYMENT_TAG);
  }
}

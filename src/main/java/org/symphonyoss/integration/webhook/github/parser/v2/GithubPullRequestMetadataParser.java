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

package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubActionConstants
    .GITHUB_ACTION_REVIEW_REQUESTED;
import static org.symphonyoss.integration.webhook.github.GithubActionConstants
    .GITHUB_ACTION_REVIEW_REQUEST_REMOVED;
import static org.symphonyoss.integration.webhook.github.GithubActionConstants
    .GITHUB_ACTION_SYNCHRONIZE;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_PULL_REQUEST;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ACTION_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ASSIGNEE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.BASE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.HEAD_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.HTML_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ICON_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PULL_REQUEST_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REPO_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.SENDER_TAG;
import static org.symphonyoss.integration.webhook.github.parser.v1.PullRequestGithubParser
    .PR_REVIEW_REQUESTED;
import static org.symphonyoss.integration.webhook.github.parser.v1.PullRequestGithubParser
    .PR_REVIEW_REQUEST_REMOVED;
import static org.symphonyoss.integration.webhook.github.parser.v1.PullRequestGithubParser
    .PR_UPDATED;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.Integration;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible to validate the event 'pull_request' sent by Github Webhook when
 * the Agent version is equal to or greater than '1.46.0'.
 * Created by campidelli on 09/05/17.
 */
@Component
public class GithubPullRequestMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubPullRequest.xml";

  private static final String TEMPLATE_FILE = "templateGithubPullRequest.xml";

  private Map<String, String> actionsAndLabels = new HashMap<>();

  @Autowired
  public GithubPullRequestMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);

    actionsAndLabels.put(GITHUB_ACTION_SYNCHRONIZE, PR_UPDATED);
    actionsAndLabels.put(GITHUB_ACTION_REVIEW_REQUESTED, PR_REVIEW_REQUESTED);
    actionsAndLabels.put(GITHUB_ACTION_REVIEW_REQUEST_REMOVED, PR_REVIEW_REQUEST_REMOVED);
  }

  @Override
  protected String getTemplateFile() {
    return TEMPLATE_FILE;
  }

  @Override
  protected String getMetadataFile() {
    return METADATA_FILE;
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_PULL_REQUEST);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    proccessIconURL(input);
    processActionVerbs(input);
    processUser(input.path(SENDER_TAG));
    processUser(input.path(ASSIGNEE_TAG));
    processURL(input.path(PULL_REQUEST_TAG), HTML_URL_TAG);
    processURL(input.path(PULL_REQUEST_TAG).path(HEAD_TAG).path(REPO_TAG), HTML_URL_TAG);
    processURL(input.path(PULL_REQUEST_TAG).path(BASE_TAG).path(REPO_TAG), HTML_URL_TAG);
  }

  /**
   * Adjust some actions verbs to be more readable or grammar consistent.
   * @param node JSON input payload
   */
  private void processActionVerbs(JsonNode node) {
    String action = node.path(ACTION_TAG).textValue();

    if (actionsAndLabels.containsKey(action)) {
      action = actionsAndLabels.get(action);
      ((ObjectNode) node).put(ACTION_TAG, action);
    }
  }
}

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

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_PULL_REQUEST_REVIEW_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ACTION_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.COMMENT_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.HTML_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.USER_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to validate the event 'pull_request_review_comment' sent by Github
 * Webhook when the Agent version is equal to or greater than '1.46.0'.
 * Created by campidelli on 11/05/17.
 */
@Component
public class GithubPullRequestReviewCommentMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubPullRequestReviewComment.xml";

  private static final String TEMPLATE_FILE = "templateGithubPullRequestReviewComment.xml";

  private static final String ACTION_CREATED = "created";

  private static final String ACTION_ADDED = "added";

  @Autowired
  public GithubPullRequestReviewCommentMetadataParser(UserService userService,
      GithubParserUtils utils, IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);
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
    return Arrays.asList(GITHUB_EVENT_PULL_REQUEST_REVIEW_COMMENT);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    proccessIconURL(input);
    processAction(input);
    processUser(input.path(COMMENT_TAG).path(USER_TAG));
    processURL(input.path(COMMENT_TAG), HTML_URL_TAG);
  }

  /**
   * Translate the 'action' attribute. When value is 'created', the correct term is 'added'.
   * @param input Json node contaning the value to be translated.
   */
  private void processAction(JsonNode input) {
    String action = input.path(ACTION_TAG).asText();
    action = ACTION_CREATED.equals(action) ? ACTION_ADDED : action;
    ((ObjectNode) input).put(ACTION_TAG, action);
  }
}

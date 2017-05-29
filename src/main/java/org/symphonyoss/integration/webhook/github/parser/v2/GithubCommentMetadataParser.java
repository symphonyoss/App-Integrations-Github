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
    .GITHUB_ACTION_DELETED;
import static org.symphonyoss.integration.webhook.github.GithubActionConstants.GITHUB_ACTION_EDITED;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_COMMIT_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_ISSUE_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ACTION_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.BODY_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.COMMENT_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ENTITY_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ENTITY_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.EVENT_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.HTML_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.ISSUE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.USER_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to validate the events 'commit_comment' and 'issue_comment' sent by
 * Github Webhook when the Agent version is equal to or greater than '1.46.0'.
 * Created by apimentel on 10/05/17.
 */
@Component
public class GithubCommentMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubComment.xml";

  private static final String TEMPLATE_FILE = "templateGithubComment.xml";
  private static final String COMMIT_ENTITY = "commit";
  private static final String ISSUE_ENTITY = "issue";

  @Autowired
  public GithubCommentMetadataParser(UserService userService, GithubParserUtils utils,
      IntegrationProperties integrationProperties) {
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
    return Arrays.asList(GITHUB_EVENT_COMMIT_COMMENT, GITHUB_EVENT_ISSUE_COMMENT);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    proccessIconURL(input);
    processUser(input.with(COMMENT_TAG).path(USER_TAG));
    processUserComment(input);
    processCommentEntity(input);
  }

  /**
   * Produces a safe string, with HTML line breaks instead of \n.
   * @param rootNode Node to add the modified string.
   */
  private void processUserComment(JsonNode rootNode) {
    ObjectNode commentNode = (ObjectNode) rootNode.path(COMMENT_TAG);
    SafeString comment = ParserUtils.escapeAndAddLineBreaks(commentNode.path(BODY_TAG).asText());
    commentNode.put(BODY_TAG, isCommentProcessable(rootNode) ? comment.toString() : null);
  }

  /**
   * There are cases that the comment shouldn't be displayed. There are the cases: <br/>
   * 1. Github's issue comment edited does not send the updated text, just the old one. Therefore,
   * to avoid displaying outdated information, the comment is omitted
   * 2. Github's issue comment deleted is omitted, as the comment are not present anymore
   * @param inputNode
   * @return
   */
  private boolean isCommentProcessable(JsonNode inputNode) {
    String event = getGithubEvent(inputNode);
    String action = inputNode.path(ACTION_TAG).asText();
    if (GITHUB_EVENT_ISSUE_COMMENT.equals(event)) {
      if (GITHUB_ACTION_EDITED.equals(action) || GITHUB_ACTION_DELETED.equals(action)) {
        return false;
      }
    }
    return true;
  }

  /**
   * As this class handles issues and commit comments, this method inserts two fields in the final
   * json:
   * <ol>
   * <li><b>Entity</b>: contains the text describing if this event was triggered by an issue or a
   * commit</li>
   * <li><b>Entity Url</b>: the url to access this entity</li>
   * </ol>
   * @param input Root json node for the webhook event
   */
  private void processCommentEntity(JsonNode input) {
    String event = getGithubEvent(input);
    if (StringUtils.isNotEmpty(event)) {

      String entity;
      String entityUrl;
      switch (event) {
        case GITHUB_EVENT_ISSUE_COMMENT:
          entity = ISSUE_ENTITY;
          entityUrl = input.path(ISSUE_TAG).path(HTML_URL_TAG).asText();
          break;
        case GITHUB_EVENT_COMMIT_COMMENT:
        default:
          entity = COMMIT_ENTITY;
          entityUrl = input.path(COMMENT_TAG).path(HTML_URL_TAG).asText();
          break;
      }

      // Set the entity (issue/comment) and the entity url values
      ObjectNode objectNode = (ObjectNode) input;
      objectNode.put(ENTITY_TAG, entity);
      objectNode.put(ENTITY_URL_TAG, entityUrl);
    }
  }

  /**
   * Retrieve the event being parsed
   * @param input
   * @return
   */
  private String getGithubEvent(JsonNode input) {
    return input.path(EVENT_TAG).asText();
  }
}

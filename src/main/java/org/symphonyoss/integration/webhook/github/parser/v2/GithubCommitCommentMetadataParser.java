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
    .GITHUB_EVENT_COMMIT_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.BODY_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.COMMENT_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.USER_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Collections;
import java.util.List;

/**
 * This class is responsible to validate the event 'commit_comment' sent by Github Webhook when
 * the Agent version is equal to or greater than '1.46.0'.
 * Created by apimentel on 10/05/17.
 */
@Component
public class GithubCommitCommentMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubCommitComment.xml";

  private static final String TEMPLATE_FILE = "templateGithubCommitComment.xml";

  @Autowired
  public GithubCommitCommentMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
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
    return Collections.singletonList(GITHUB_EVENT_COMMIT_COMMENT);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    super.preProcessInputData(input);
    processUser(input.with(COMMENT_TAG).path(USER_TAG));
    processUserComment(input);
  }

  private void processUserComment(JsonNode rootNode) {
    ObjectNode commentNode = (ObjectNode) rootNode.path(COMMENT_TAG);
    SafeString comment = ParserUtils.escapeAndAddLineBreaks(commentNode.path(BODY_TAG).asText());
    commentNode.put(BODY_TAG, comment.toString());
  }
}

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

import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUSH;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.BRANCH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAGS;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TYPE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REPO_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.SENDER_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to validate the event 'push' sent by Github Webhook when
 * the Agent version is equal to or greater than '1.46.0'.
 * Created by campidelli on 02/05/17.
 */
@Component
public class GithubPushMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubPush.xml";

  private static final String TEMPLATE_FILE = "templateGithubPush.xml";

  @Autowired
  public GithubPushMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
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
    return Arrays.asList(GITHUB_EVENT_PUSH);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    proccessIconURL(input);
    processRefType(input);
    processUser(input.path(SENDER_TAG));
  }

  /**
   * Adds 'ref_type' and 'repo' based on 'tag' value.
   * @param input JSON input payload
   */
  private void processRefType(JsonNode input) {
    String ref = input.path(REF_TAG).asText();

    String refType = ref.contains(PATH_TAGS) ? PATH_TAG : BRANCH_TAG;
    refType = WordUtils.capitalize(refType);
    ((ObjectNode) input).put(REF_TYPE_TAG, refType);

    String repo = ref.contains("/") ? ref.substring(ref.lastIndexOf("/") + 1) : ref;
    ((ObjectNode) input).put(REPO_TAG, repo);
  }

}

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

import static org.symphonyoss.integration.webhook.github.GithubEventTags.BRANCH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.HTML_URL_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.LOGIN_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.NAME_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAGS;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TYPE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REPOSITORY_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REPO_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.SENDER_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.URL_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;
import org.symphonyoss.integration.webhook.parser.metadata.EntityObject;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.ProcessingException;

/**
 * Abstract Github parser responsible to augment the Github input data querying the user API and
 * pre-processing the input data.
 * Created by campidelli on 02/05/17.
 */
public abstract class GithubMetadataParser extends MetadataParser implements GithubParser {

  private static final Logger LOG = LoggerFactory.getLogger(GithubMetadataParser.class);

  private GithubParserUtils utils;

  private UserService userService;

  private String integrationUser;

  @Autowired
  public GithubMetadataParser(UserService userService, GithubParserUtils utils) {
    this.userService = userService;
    this.utils = utils;
  }

  @Override
  public void setIntegrationUser(String integrationUser) {
    this.integrationUser = integrationUser;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node) throws GithubParserException {
    return parse(node);
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    processRefType(input);
    processSender(input);
  }

  @Override
  protected void postProcessOutputData(EntityObject output, JsonNode input) {
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

  /**
   * Adds 'ref_type' based on 'tag' value.
   * @param input JSON input payload
   */
  private void processSender(JsonNode input) {
    JsonNode senderNode = input.path(SENDER_TAG);
    String login = senderNode.path(LOGIN_TAG).asText();
    String publicName = login;

    try {
      String url = senderNode.path(URL_TAG).asText();

      JsonNode publicUserInfo = utils.doGetJsonApi(url);
      if (publicUserInfo != null) {
        publicName = publicUserInfo.path(NAME_TAG).textValue();
        publicName = publicName == null ? login : publicName;
      }

    } catch (IOException e) {
      LOG.warn("Couldn't reach GitHub API due to " + e.getMessage(), e);
    } catch (ProcessingException e) {
      Throwable cause = e.getCause();
      LOG.warn("Couldn't reach GitHub API due to " + cause.getMessage(), e);
    }

    ((ObjectNode) senderNode).put(NAME_TAG, publicName);
  }

}
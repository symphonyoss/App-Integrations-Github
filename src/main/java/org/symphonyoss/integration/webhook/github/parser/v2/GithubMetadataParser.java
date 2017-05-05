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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.symphonyoss.integration.entity.model.EntityConstants.USER_ENTITY_FIELD;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.BRANCH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.PATH_TAGS;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.REF_TYPE_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;

import java.util.Map;

/**
 * Abstract Github parser responsible to augment the Github input data querying the user API and
 * pre-processing the input data.
 * Created by campidelli on 02/05/17.
 */
public abstract class GithubMetadataParser extends MetadataParser implements GithubParser {

  private UserService userService;

  private String integrationUser;

  @Autowired
  public GithubMetadataParser(UserService userService) {
    this.userService = userService;
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
  }

  /**
   * Adds 'ref_type' based on 'tag' value.
   * @param input JSON input payload
   */
  private void processRefType(JsonNode input) {
    String ref = input.path(REF_TAG).asText();
    String refType = ref.contains(PATH_TAGS) ? PATH_TAG : BRANCH_TAG;

    if (StringUtils.isNotEmpty(refType)) {
      ((ObjectNode) input).put(REF_TYPE_TAG, refType);
    }
  }
}
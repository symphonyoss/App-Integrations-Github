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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
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

  @Autowired
  private IntegrationProperties integrationProperties;

  private static final String PATH_IMG = "img";

  private static final String INTEGRATION_NAME = "github";

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

  protected String getURLFromIcon(String iconName) {
    String urlBase = integrationProperties.getApplicationUrl(INTEGRATION_NAME);

    if (!urlBase.isEmpty()) {
      return String.format("%s/%s/%s", urlBase, PATH_IMG, iconName);
    } else {
      return StringUtils.EMPTY;
    }
  }
}
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit test class for {@link GithubPushMetadataParser}
 * Created by campidelli on 03/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPushMetadataParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String MOCK_DISPLAY_NAME = "Mock user";

  private static final String MOCK_USERNAME = "username";

  private static final String MOCK_EMAIL_ADDRESS = "test@symphony.com";

  private static final Long MOCK_USER_ID = 123456L;

  private static final String FILE_PUSH = "payload_xgithubevent_push.json";

  @Mock
  private UserService userService;

  private GithubMetadataParser parser;

  private String expectedTemplateFile = "<messageML>\n"
      + "    <div class=\"entity\">\n"
      + "        <card class=\"barStyle\">\n"
      + "            <header>\n"
      + "                <img src=\"${entity['jiraIssueCreated'].issue.priority.iconUrl}\" "
      + "class=\"icon\" />\n"
      + "                <a href=\"${entity['jiraIssueCreated'].issue.url}\">${entity"
      + "['jiraIssueCreated'].issue.key}</a>\n"
      + "                <span>${entity['jiraIssueCreated'].issue.subject} - </span>\n"
      + "                <#if (entity['jiraIssueCreated'].user.id)??>\n"
      + "                    <mention email=\"${entity['jiraIssueCreated'].user.emailAddress}\" "
      + "/>\n"
      + "                <#else>\n"
      + "                    <span>${entity['jiraIssueCreated'].user.displayName}</span>\n"
      + "                </#if>\n"
      + "                <span class=\"action\">Created</span>\n"
      + "            </header>\n"
      + "            <body>\n"
      + "                <div class=\"entity\" data-entity-id=\"jiraIssueCreated\">\n"
      + "                    <br/>\n"
      + "                    <div>\n"
      + "                        <span class=\"label\">Description:</span>\n"
      + "                        <span>${entity['jiraIssueCreated'].issue.description}</span>\n"
      + "                    </div>\n"
      + "                    <br/>\n"
      + "                    <div>\n"
      + "                        <span class=\"label\">Assignee:</span>\n"
      + "                        <#if (entity['jiraIssueCreated'].issue.assignee.id)??>\n"
      + "                            <mention email=\"${entity['jiraIssueCreated'].issue.assignee"
      + ".emailAddress}\" />\n"
      + "                        <#else>\n"
      + "                            <span>${entity['jiraIssueCreated'].issue.assignee"
      + ".displayName}</span>\n"
      + "                        </#if>\n"
      + "                    </div>\n"
      + "                    <hr/>\n"
      + "                    <div class=\"labelBackground badge\">\n"
      + "                            <span class=\"label\">Type:</span>\n"
      + "                            <img src=\"${entity['jiraIssueCreated'].issue.issueType"
      + ".iconUrl}\" class=\"icon\" />\n"
      + "                            <span>${entity['jiraIssueCreated'].issue.issueType"
      + ".name}</span>\n"
      + "                            <span class=\"label\">Priority:</span>\n"
      + "                            <img src=\"${entity['jiraIssueCreated'].issue.priority"
      + ".iconUrl}\" class=\"icon\" />\n"
      + "                            <span>${entity['jiraIssueCreated'].issue.priority"
      + ".name}</span>\n"
      + "                            <#if (entity['jiraIssueCreated'].issue.epic)??>\n"
      + "                                <span class=\"label\">Epic:</span>\n"
      + "                                "
      + "<a href=\"${entity['jiraIssueCreated'].issue.epic.link}\">${entity['jiraIssueCreated']"
      + ".issue.epic.name}</a>\n"
      + "                            </#if>\n"
      + "                            <span class=\"label\">Status:</span>\n"
      + "                            <span class=\"infoBackground "
      + "badge\">${entity['jiraIssueCreated'].issue.status}</span>\n"
      + "                            <#if (entity['jiraIssueCreated'].issue.labels)??>\n"
      + "                                <span class=\"label\">Labels:</span>\n"
      + "                                <#list entity['jiraIssueCreated'].issue.labels as label>\n"
      + "                                    <a class=\"hashTag\">#${label.text}</a>\n"
      + "                                </#list>\n"
      + "                            </#if>\n"
      + "                    </div>\n"
      + "                </div>\n"
      + "            </body>\n"
      + "        </card>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Before
  public void init() {
    parser = new GithubPushMetadataParser(userService);
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);
  }

  private void mockUserInfo() {
    User user = new User();
    user.setId(MOCK_USER_ID);
    user.setDisplayName(MOCK_DISPLAY_NAME);
    user.setUserName(MOCK_USERNAME);
    user.setEmailAddress(MOCK_EMAIL_ADDRESS);

    doReturn(user).when(userService).getUserByEmail(eq(MOCK_INTEGRATION_USER), anyString());
  }

  protected JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }

  @Test
  public void testIssueCreated() throws IOException, GithubParserException {
    mockUserInfo();

    JsonNode node = readJsonFromFile(FILE_PUSH);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(FILE_PUSH);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(expectedTemplateFile, result.getMessage());
  }
}


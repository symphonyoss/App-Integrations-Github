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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

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

  private static final String PAYLOAD_FILE_PUSH = "payload_xgithubevent_push.json";

  private static final String EXPECTED_FILE_PUSH = "parser/pushParser/v2/expected_github_event_push.json";

  @Spy
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  private GithubMetadataParser parser;

  private static String EXPECTED_TEMPLATE_FILE = "<messageML>\n"
      + "    <div class=\"entity\">\n"
      + "        <card class=\"barStyle\">\n"
      + "            <header>\n"
      + "                <span class=\"tempo-text-color--normal\">${entity['githubPush'].refType}"
      + " </span>\n"
      + "                <a href=\"${entity['githubPush'].compare}\">${entity['githubPush'].ref} "
      + "</a>\n"
      + "                <span class=\"tempo-text-color--normal\">at </span>\n"
      + "                <a href=\"${entity['githubPush'].repository.url}\">${entity['githubPush"
      + "'].repository.fullName} </a>\n"
      + "                <span class=\"tempo-text-color--green\"><b>pushed </b></span>\n"
      + "                <span class=\"tempo-text-color--normal\">by </span>\n"
      + "                <a href=\"${entity['githubPush'].pusher.url}\">mailto:${entity"
      + "['githubPush'].pusher.email} </a>\n"
      + "            </header>\n"
      + "        </card>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Before
  public void init() {
    parser = new GithubPushMetadataParser(userService, utils);
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
  public void testPush() throws IOException, GithubParserException {
    mockUserInfo();

    JsonNode node = readJsonFromFile(PAYLOAD_FILE_PUSH);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_FILE_PUSH);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    //assertEquals(EXPECTED_TEMPLATE_FILE, result.getMessage());
  }
}


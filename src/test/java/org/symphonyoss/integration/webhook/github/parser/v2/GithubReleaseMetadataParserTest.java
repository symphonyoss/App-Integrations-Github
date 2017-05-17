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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit test class for {@link GithubReleaseMetadataParser}
 * Created by campidelli on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubReleaseMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String PAYLOAD_FILE_RELEASE = "payload_xgithubevent_release.json";
  private static final String EXPECTED_FILE_RELEASE =
      "parser/releaseParser/v2/expected_xgithub_event_release.json";

  @Mock
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  private GithubMetadataParser parser;

  private static String EXPECTED_TEMPLATE_FILE = "<messageML>\n"
      + "    <div class=\"entity\" data-entity-id=\"githubRelease\">\n"
      + "        <card class=\"barStyle\" iconSrc=\"${entity['githubRelease'].iconURL}\" "
      + "accent=\"gray\">\n"
      + "            <header>\n"
      + "                <a href=\"${entity['githubRelease'].release.url}\">\n"
      + "                    Release ${entity['githubRelease'].release.tagName}\n"
      + "                </a>\n"
      + "                <span class=\"tempo-text-color--green\"><b> created </b></span>\n"
      + "                <span class=\"tempo-text-color--normal\">by </span>\n"
      + "                <span class=\"tempo-text-color--normal\"><b>${entity['githubRelease']"
      + ".release.author.name} </b></span>\n"
      + "                <span class=\"tempo-text-color--normal\">in </span>\n"
      + "                <a href=\"${entity['githubRelease'].repository.url}\">\n"
      + "                    ${entity['githubRelease'].repository.fullName}\n"
      + "                </a>\n"
      + "            </header>\n"
      + "        </card>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Before
  public void init() {
    parser = new GithubReleaseMetadataParser(userService, utils, integrationProperties);
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);

    try {
      doReturn(null).when(utils).doGetJsonApi(anyString());
    } catch (IOException e) {
      fail("IOException should not be thrown because there is no real API calling, its mocked.");
    }

    mockIntegrationProperties(integrationProperties);
  }

  @Test
  public void testRelease() throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(PAYLOAD_FILE_RELEASE);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_FILE_RELEASE);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(EXPECTED_TEMPLATE_FILE, result.getMessage());
  }
}


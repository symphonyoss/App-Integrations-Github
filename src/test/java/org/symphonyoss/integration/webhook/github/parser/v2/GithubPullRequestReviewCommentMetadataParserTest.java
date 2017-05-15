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
 * Unit test class for {@link GithubPullRequestReviewCommentMetadataParser}
 * Created by campidelli on 11/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPullRequestReviewCommentMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String PAYLOAD_FILE_PR_REVIEW_COMMENT =
      "payload_xgithubevent_pullRequestReviewComment.json";
  private static final String EXPECTED_FILE_PR_REVIEW_COMMENT =
      "parser/pullRequestParser/v2/expected_xgithub_event_pull_request_review_comment.json";

  @Mock
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  private GithubMetadataParser parser;

  private static String EXPECTED_TEMPLATE_FILE = "<messageML>\n"
      + "    <div class=\"entity\" data-entity-id=\"githubPullRequestReviewComment\">\n"
      + "        <card class=\"barStyle\" iconSrc=\"${entity['githubPullRequestReviewComment']"
      + ".iconURL}\" accent=\"gray\">\n"
      + "            <header>\n"
      + "                <a href=\"${entity['githubPullRequestReviewComment'].url}\">Pull Request"
      + " #${entity['githubPullRequestReviewComment'].title} </a>\n"
      + "                <span class=\"tempo-text-color--green\">comment "
      + "${entity['githubPullRequestReviewComment'].action} </span>\n"
      + "                <span class=\"tempo-text-color--normal\">by </span>\n"
      + "                <span class=\"tempo-text-color--normal\"><b>${entity['githubPullRequest"
      + "'].comment.author.name} </b></span>\n"
      + "            </header>\n"
      + "            <body>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Comment: </span>\n"
      + "                    <span "
      + "class=\"tempo-text-color--normal\">${entity['githubPullRequestReviewComment'].comment"
      + ".body}</span>\n"
      + "                </p>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Diff Hunk: </span>\n"
      + "                    <span "
      + "class=\"tempo-text-color--normal\">${entity['githubPullRequestReviewComment'].comment"
      + ".diffHunk}</span>\n"
      + "                </p>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Details: </span>\n"
      + "                    <a href=\"${entity['githubPullRequestReviewComment'].comment.url}\">\n"
      + "                        ${entity['githubPullRequestReviewComment'].comment.url}\n"
      + "                    </a>\n"
      + "                </p>\n"
      + "            </body>\n"
      + "        </card>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Before
  public void init() {
    parser =
        new GithubPullRequestReviewCommentMetadataParser(userService, utils, integrationProperties);
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
  public void testPRReviewComment() throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(PAYLOAD_FILE_PR_REVIEW_COMMENT);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_FILE_PR_REVIEW_COMMENT);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(EXPECTED_TEMPLATE_FILE, result.getMessage());
  }
}


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
 * Unit test class for {@link GithubPullRequestMetadataParser}
 * Created by campidelli on 09/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPullRequestMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String PAYLOAD_FILE_PR_ASSIGNED =
      "parser/pullRequest/payload_xgithubevent_pull_request_assigned.json";
  private static final String EXPECTED_FILE_PR_ASSIGNED =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_assigned.json";

  private static final String PAYLOAD_FILE_PR_CLOSED =
      "parser/pullRequest/payload_xgithubevent_pull_request_closed.json";
  private static final String EXPECTED_FILE_PR_CLOSED =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_closed.json";

  private static final String PAYLOAD_FILE_PR_LABELED =
      "parser/pullRequest/payload_xgithubevent_pull_request_labeled.json";
  private static final String EXPECTED_FILE_PR_LABELED =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_labeled.json";

  private static final String PAYLOAD_FILE_PR_OPENED =
      "parser/pullRequest/payload_xgithubevent_pull_request_opened.json";
  private static final String EXPECTED_FILE_PR_OPENED =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_opened.json";

  private static final String PAYLOAD_FILE_PR_REVIEW_REQUEST =
      "parser/pullRequest/payload_xgithubevent_pull_request_review_request.json";
  private static final String EXPECTED_FILE_PR_REVIEW_REQUEST =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_review_request.json";

  private static final String PAYLOAD_FILE_PR_REVIEW_REQUEST_REMOVED =
      "parser/pullRequest/payload_xgithubevent_pull_request_review_request_removed.json";
  private static final String EXPECTED_FILE_PR_REVIEW_REQUEST_REMOVED =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_review_request_removed.json";

  private static final String PAYLOAD_FILE_PR_SYNCHRONIZE =
      "parser/pullRequest/payload_xgithubevent_pull_request_synchronize.json";
  private static final String EXPECTED_FILE_PR_SYNCHRONIZE =
      "parser/pullRequest/v2/expected_xgithub_event_pull_request_synchronize.json";

  @Mock
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  private GithubMetadataParser parser;

  private static String EXPECTED_TEMPLATE_FILE = "<messageML>\n"
      + "    <div class=\"entity\" data-entity-id=\"githubPullRequest\">\n"
      + "        <card class=\"barStyle\" iconSrc=\"${entity['githubPullRequest'].iconURL}\" "
      + "accent=\"gray\">\n"
      + "            <header>\n"
      + "                <a href=\"${entity['githubPullRequest'].url}\">Pull Request "
      + "#${entity['githubPullRequest'].number} </a>\n"
      + "                <span class=\"tempo-text-color--normal\">${entity['githubPullRequest']"
      + ".title} - </span>\n"
      + "                <span class=\"tempo-text-color--green\"><b>${entity['githubPullRequest']"
      + ".action} </b></span>\n"
      + "\n"
      + "                <#if entity['githubPullRequest'].action == 'assigned'>\n"
      + "                    <span class=\"tempo-text-color--normal\">to </span>\n"
      + "                    <span "
      + "class=\"tempo-text-color--normal\"><b>${entity['githubPullRequest'].assignee.name} "
      + "</b></span>\n"
      + "                <#elseif entity['githubPullRequest'].action == 'labeled'>\n"
      + "                    <span class=\"tempo-text-color--normal\">with </span>\n"
      + "                    <span "
      + "class=\"tempo-text-color--normal\"><b>${entity['githubPullRequest'].label.name} "
      + "</b></span>\n"
      + "                <#else>\n"
      + "                    <span class=\"tempo-text-color--normal\">by </span>\n"
      + "                    <span "
      + "class=\"tempo-text-color--normal\"><b>${entity['githubPullRequest'].sender.name} "
      + "</b></span>\n"
      + "                </#if>\n"
      + "            </header>\n"
      + "            <body>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Summary: </span>\n"
      + "                    <span class=\"tempo-text-color--normal\">${entity['githubPullRequest"
      + "'].body}</span>\n"
      + "                </p>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Commits in this PR: "
      + "</span>\n"
      + "                    <span class=\"tempo-text-color--normal\">${entity['githubPullRequest"
      + "'].commits}</span>\n"
      + "                </p>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">PR State: </span>\n"
      + "                    <span class=\"tempo-text-color--normal\">${entity['githubPullRequest"
      + "'].state} </span>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Merged: </span>\n"
      + "                    <#if entity['githubPullRequest'].merged == 'true'>\n"
      + "                        <span class=\"tempo-text-color--normal\">Yes </span>\n"
      + "                    <#else>\n"
      + "                        <span class=\"tempo-text-color--normal\">No </span>\n"
      + "                    </#if>\n"
      + "                </p>\n"
      + "                <p>\n"
      + "                    <span class=\"tempo-text-color--secondary\">Repositories involved: "
      + "</span>\n"
      + "                    <span class=\"tempo-text-color--normal\">Changes from </span>\n"
      + "                    "
      +
      "<a href=\"${entity['githubPullRequest'].repoHead.url}/tree/${entity['githubPullRequest'].repoHead.branch}\">\n"
      + "                        ${entity['githubPullRequest'].repoHead"
      + ".fullName}:${entity['githubPullRequest'].repoHead.branch}\n"
      + "                    </a>\n"
      + "                    <span class=\"tempo-text-color--normal\"> to </span>\n"
      + "                    "
      +
      "<a href=\"${entity['githubPullRequest'].repoBase.url}/tree/${entity['githubPullRequest'].repoBase.branch}\">\n"
      + "                        ${entity['githubPullRequest'].repoBase"
      + ".fullName}:${entity['githubPullRequest'].repoBase.branch}\n"
      + "                    </a>\n"
      + "                </p>\n"
      + "            </body>\n"
      + "        </card>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Before
  public void init() {
    parser = new GithubPullRequestMetadataParser(userService, utils, integrationProperties);
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);

    try {
      doReturn(null).when(utils).doGetJsonApi(anyString());
    } catch (IOException e) {
      fail("IOException should not be thrown because there is no real API calling, its mocked.");
    }

    mockIntegrationProperties(integrationProperties);
  }

  private void testPR(String payloadFile, String expectedFile)
      throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(payloadFile);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(expectedFile);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(EXPECTED_TEMPLATE_FILE, result.getMessage());
  }

  @Test
  public void testPRAssigned() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_ASSIGNED, EXPECTED_FILE_PR_ASSIGNED);
  }

  @Test
  public void testPRClosed() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_CLOSED, EXPECTED_FILE_PR_CLOSED);
  }

  @Test
  public void testPRLabeled() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_LABELED, EXPECTED_FILE_PR_LABELED);
  }

  @Test
  public void testPROpened() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_OPENED, EXPECTED_FILE_PR_OPENED);
  }

  @Test
  public void testPRReviewRequest() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_REVIEW_REQUEST, EXPECTED_FILE_PR_REVIEW_REQUEST);
  }

  @Test
  public void testPRReviewRequestRemoved() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_REVIEW_REQUEST_REMOVED, EXPECTED_FILE_PR_REVIEW_REQUEST_REMOVED);
  }

  @Test
  public void testPRSynchronize() throws IOException, GithubParserException {
    testPR(PAYLOAD_FILE_PR_SYNCHRONIZE, EXPECTED_FILE_PR_SYNCHRONIZE);
  }
}


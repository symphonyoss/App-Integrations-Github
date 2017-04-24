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

package org.symphonyoss.integration.webhook.github.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.webhook.github.CommonGithubTest;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit tests for {@link PullRequestGithubParser}
 *
 * Created by Milton Quilzini on 13/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestGithubParserTest extends CommonGithubTest {

  @Mock
  private GithubParserUtils utils;

  @InjectMocks
  private PullRequestGithubParser pullReqGithubParser = new PullRequestGithubParser();

  @Test
  public void testPullRequestOpenedParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_opened.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_opened_expected_message.xml");
    // mocks
    JsonNode publicUserInfo = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfo).when(utils).doGetJsonApi(anyString());

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestAssignedParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_assigned.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_assigned_expected_message.xml");
    // mocks
    String octocatUrl = "https://api.github.com/users/octocat";
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
    JsonNode publicUserInfoOctocat = getJsonFile("payload_github_public_info_octocat.json");
    doReturn(publicUserInfoOctocat).when(utils).doGetJsonApi(octocatUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestLabeledParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_labeled.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_labeled_expected_message.xml");
    // mocks
    String octocatUrl = "https://api.github.com/users/octocat";
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
    JsonNode publicUserInfoOctocat = getJsonFile("payload_github_public_info_octocat.json");
    doReturn(publicUserInfoOctocat).when(utils).doGetJsonApi(octocatUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestClosedParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_closed.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_closed_expected_message.xml");
    // mocks
    String octocatUrl = "https://api.github.com/users/octocat";
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
    JsonNode publicUserInfoOctocat = getJsonFile("payload_github_public_info_octocat.json");
    doReturn(publicUserInfoOctocat).when(utils).doGetJsonApi(octocatUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestSynchronizedParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_synchronize.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_synchronize_expected_message.xml");
    // mocks
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestReviewRequestParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_review_request.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_review_request_expected_message.xml");
    // mocks
    String octocatUrl = "https://api.github.com/users/octocat";
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
    JsonNode publicUserInfoOctocat = getJsonFile("payload_github_public_info_octocat.json");
    doReturn(publicUserInfoOctocat).when(utils).doGetJsonApi(octocatUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPullRequestReviewRequestRemovedParse() throws IOException, GithubParserException {
    // files
    JsonNode pullRequestNode = getJsonFile("payload_xgithubevent_pull_request_review_request_removed.json");
    String expectedMessage = getExpectedMessageML(
        "payload_xgithubevent_pull_request_review_request_removed_expected_message.xml");
    // mocks
    String octocatUrl = "https://api.github.com/users/octocat";
    String baxterUrl = "https://api.github.com/users/baxterthehacker";

    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
    JsonNode publicUserInfoOctocat = getJsonFile("payload_github_public_info_octocat.json");
    doReturn(publicUserInfoOctocat).when(utils).doGetJsonApi(octocatUrl);

    // call
    String result = pullReqGithubParser.parse(Collections.<String, String>emptyMap(), pullRequestNode);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

}

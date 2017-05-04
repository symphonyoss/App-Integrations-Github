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

package org.symphonyoss.integration.webhook.github;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.CREATE;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT_STATUS;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUBLIC;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_PULL_REQUEST_REVIEW_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUSH;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_STATUS;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_HEADER_EVENT_NAME;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.entity.model.User;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserFactory;
import org.symphonyoss.integration.webhook.github.parser.GithubParserResolver;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubWebHookParserAdapter;
import org.symphonyoss.integration.webhook.github.parser.NullGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.CreateGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.DeploymentGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.DeploymentStatusGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.PublicGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.PullRequestReviewCommentGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.PushGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.StatusGithubParser;
import org.symphonyoss.integration.webhook.github.parser.v1.V1GithubParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link GithubWebHookIntegration}.
 *
 * Created by Milton Quilzini on 11/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubWebHookIntegrationTest extends CommonGithubTest {

  @Spy
  private List<GithubParserFactory> factories = new ArrayList<>();

  @Mock
  private GithubParserFactory factory;

  @Mock
  private GithubParserResolver parserResolver;

  @Spy
  private GithubParserUtils githubParserUtils;

  @InjectMocks
  private GithubWebHookIntegration githubWHI = new GithubWebHookIntegration();

  @Mock
  private UserService userService;

  @InjectMocks
  private NullGithubParser defaultGithubParser = new NullGithubParser();

  @InjectMocks
  private PullRequestReviewCommentGithubParser prReviewCommentGithubParser =
      new PullRequestReviewCommentGithubParser();

  @InjectMocks
  private CreateGithubParser createGithubParser = new CreateGithubParser();

  @InjectMocks
  private PublicGithubParser publicGithubParser = new PublicGithubParser();

  @InjectMocks
  private DeploymentGithubParser deploymentGithubParser = new DeploymentGithubParser();

  @InjectMocks
  private DeploymentStatusGithubParser deploymentStatusGithubParser = new DeploymentStatusGithubParser();

  @InjectMocks
  private StatusGithubParser statusGithubParser = new StatusGithubParser();

  @InjectMocks
  private PushGithubParser pushGithubParser = new PushGithubParser();

  @Before
  public void setup() throws IOException {
    mockUsers("test@symphony.com", "test2@symphony.com", "mquilzini@symphony.com",
        "ppires@symphony.com");

    factories.add(factory);

    doReturn(factory).when(parserResolver).getFactory();
  }

  private void mockUsers(String... emails) {
    for (String email : emails) {
      User user = new User();
      user.setEmailAddress(email);
      when(userService.getUserByEmail(anyString(), eq(email))).thenReturn(user);
    }
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();

    githubWHI.onConfigChange(settings);

    verify(factory, times(1)).onConfigChange(settings);
  }

  @Test
  public void testNoEventPayload() throws WebHookParseException {
    String body = "{ \"random\": \"json\" }";

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(defaultGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    assertNull(githubWHI.parse(payload));
  }

  @Test(expected = GithubParserException.class)
  public void testFailReadingJSON() throws IOException, WebHookParseException {
    String emptyBody = "";

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), emptyBody);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(defaultGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    githubWHI.parse(payload);
  }

  @Test
  public void testPushEventPayload() throws WebHookParseException, IOException {
    String body = readFile("payload_xgithubevent_push.json");
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_PUSH);

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(pushGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected = getExpectedMessageML("payload_xgithubevent_push_expected_message_without_user_info.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPullRequestReviewCommentEventPayload() throws WebHookParseException, IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_PULL_REQUEST_REVIEW_COMMENT);

    String body = readFile("payload_xgithubevent_pullRequestReviewComment.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(prReviewCommentGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected = readFile(
        "payload_xgithubevent_pullRequestReviewComment_userDetailsNull_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testDeploymentEventPayload() throws IOException, WebHookParseException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_DEPLOYMENT);

    String body = readFile("payload_xgithubevent_deployment_without_description.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(deploymentGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected =
        readFile("payload_xgithubevent_deployment_without_userinfo_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testDeploymentStatusEventPayload() throws IOException, WebHookParseException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_DEPLOYMENT_STATUS);

    String body = readFile("payload_xgithubevent_deployment_status_without_description.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(deploymentStatusGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected =
        readFile("payload_xgithubevent_deployment_status_without_userinfo_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPublicEventPayload() throws WebHookParseException, IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_PUBLIC);

    String body = readFile("payload_xgithubevent_public.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(publicGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected = readFile("payload_xgithubevent_public_without_userinfo_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testTagCreatedEventPayload() throws WebHookParseException, IOException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, CREATE);

    String body = readFile("payload_xgithubevent_tag_created.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(createGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected = readFile("payload_xgithubevent_tag_created_without_fullname_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testStatusEventPayload() throws IOException, WebHookParseException {
    Map<String, String> headers = new HashMap<>();
    headers.put(GITHUB_HEADER_EVENT_NAME, GITHUB_EVENT_STATUS);

    String body = readFile("payload_xgithubevent_status_without_description.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    GithubWebHookParserAdapter parser = new GithubWebHookParserAdapter(statusGithubParser);
    doReturn(parser).when(factory).getParser(payload);

    Message result = githubWHI.parse(payload);

    String expected = readFile("payload_xgithubevent_status_without_userinfo_expected_message.xml");
    assertEquals(expected, result.getMessage());
  }
}

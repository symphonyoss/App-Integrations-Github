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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.symphonyoss.integration.exception.URISyntaxRuntimeException;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.webhook.github.CommonGithubTest;
import org.symphonyoss.integration.webhook.github.GithubEventConstants;
import org.symphonyoss.integration.webhook.github.GithubEventTags;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit tests for {@link PullRequestReviewCommentGithubParser}
 * Created by cmarcondes on 9/14/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestReviewCommentGithubParserTest extends CommonGithubTest {

  @Mock
  private GithubParserUtils githubParserUtils = new GithubParserUtils();

  @InjectMocks
  private PullRequestReviewCommentGithubParser prReviewCommentParser =
      new PullRequestReviewCommentGithubParser();

  @Before
  public void setup() throws IOException {
    JsonNode node = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_github_public_info_baxterthehacker.json"));
    doReturn(node).when(githubParserUtils).doGetJsonApi(anyString());
  }

  @Test
  public void testPRReviewCommentParse() throws IOException, GithubParserException {
    JsonNode node = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_xgithubevent_pullRequestReviewComment.json"));
    String result = prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);

    result = "<messageML>" + result + "</messageML>";
    String expected =
        readFile("payload_xgithubevent_pullRequestReviewComment_expected_message.xml");
    assertEquals(expected, result);
  }

  @Test
  public void testPRReviewCommentDeletedParse() throws IOException, GithubParserException {
    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(
        "payload_xgithubevent_pullRequestReviewComment.json"));
    ((ObjectNode) node).put(GithubEventTags.ACTION_TAG, GithubEventConstants.DELETED);
    String result = prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);

    result = "<messageML>" + result + "</messageML>";
    String expected =
        readFile("payload_xgithubevent_pullRequestReviewCommentDeleted_expected_message.xml");
    assertEquals(expected, result);
  }

  @Test
  public void testPRReviewCommentUpdatedParse() throws IOException, GithubParserException {
    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(
        "payload_xgithubevent_pullRequestReviewCommentUpdated.json"));

    String result = prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);

    result = "<messageML>" + result + "</messageML>";
    String expected =
        readFile("payload_xgithubevent_pullRequestReviewCommentUpdated_expected_message.xml");
    assertEquals(expected, result);
  }

  @Test
  public void testPRReviewCommentParseWithInvalidURI()
      throws IOException, GithubParserException {

    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(
        "payload_xgithubevent_pullRequestReviewComment.json"));
    JsonNode commentNode = node.path(GithubEventTags.COMMENT_TAG);
    ((ObjectNode) commentNode).put(GithubEventTags.HTML_URL_TAG, "te<s>t");
    try {
      prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);
      fail();
    } catch (URISyntaxRuntimeException e) {
      assertEquals("Failed to parse the URI te<s>t", e.getMessage());
    }
  }

  @Test
  public void testPRReviewCommentParseUserDetailsThrowingIOException()
      throws IOException, GithubParserException {
    doThrow(new IOException()).when(githubParserUtils).doGetJsonApi(anyString());
    ReflectionTestUtils.setField(prReviewCommentParser, "utils", githubParserUtils);

    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(
        "payload_xgithubevent_pullRequestReviewComment.json"));
    String result = prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);

    String expected = readFile(
        "payload_xgithubevent_pullRequestReviewComment_userDetailsNull_expected_message.xml");
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expected, result);
  }

  @Test
  public void testPRReviewCommentParseWithUserDetailsNull()
      throws IOException, GithubParserException {
    doReturn(null).when(githubParserUtils).doGetJsonApi(anyString());
    ReflectionTestUtils.setField(prReviewCommentParser, "utils", githubParserUtils);

    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(
        "payload_xgithubevent_pullRequestReviewComment.json"));
    String result = prReviewCommentParser.parse(Collections.<String, String>emptyMap(), node);

    String expected = readFile(
        "payload_xgithubevent_pullRequestReviewComment_userDetailsNull_expected_message.xml");
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expected, result);
  }

}

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
import static org.mockito.Mockito.doReturn;

import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.webhook.github.CommonGithubTest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit tests for {@link CreateGithubParser}
 * Created by rsanchez on 21/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateGithubParserTest extends CommonGithubTest {

  private static final String USER_URL = "https://api.github.com/users/baxterthehacker";

  @Mock
  private GithubParserUtils utils;

  @InjectMocks
  private CreateGithubParser parser = new CreateGithubParser();

  @Before
  public void setup() throws IOException {
    JsonNode publicUserInfoBaxter = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_github_public_info_baxterthehacker.json"));
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(USER_URL);
  }

  @Test
  public void testBranchCreated() throws IOException, GithubParserException {
    JsonNode node = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_xgithubevent_branch_created.json"));

    String expected = readFile("payload_xgithubevent_branch_created_expected_message.xml");
    String result = "<messageML>" + parser.parse(Collections.<String, String>emptyMap(), node) + "</messageML>";

    assertEquals(expected, result);
  }

  @Test
  public void testTagCreated() throws IOException, GithubParserException {
    JsonNode node = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_xgithubevent_tag_created.json"));

    String expected = readFile("payload_xgithubevent_tag_created_expected_message.xml");
    String result = "<messageML>" + parser.parse(Collections.<String, String>emptyMap(), node) + "</messageML>";

    assertEquals(expected, result);
  }

  @Test
  public void testTagCreatedWithoutFullName() throws IOException, GithubParserException {
    doReturn(null).when(utils).doGetJsonApi(USER_URL);

    JsonNode node = JsonUtils.readTree(
        classLoader.getResourceAsStream("payload_xgithubevent_tag_created.json"));

    String expected =
        readFile("payload_xgithubevent_tag_created_without_fullname_expected_message.xml");
    String result = "<messageML>" + parser.parse(Collections.<String, String>emptyMap(), node) + "</messageML>";

    assertEquals(expected, result);
  }
}

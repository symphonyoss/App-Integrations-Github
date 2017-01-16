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
 * Unit tests for {@link PushGithubParser}
 *
 * Created by Milton Quilzini on 08/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PushGithubParserTest extends CommonGithubTest {

  @Mock
  private GithubParserUtils utils;

  @InjectMocks
  private PushGithubParser pushGithubParser = new PushGithubParser();

  @Before
  public void setup() throws IOException {
    String baxterUrl = "https://api.github.com/users/baxterthehacker";
    JsonNode publicUserInfoBaxter = getJsonFile("payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(baxterUrl);
  }

  @Test
  public void testPushParse() throws IOException, GithubParserException {
    JsonNode node = getJsonFile("payload_xgithubevent_push.json");
    String expectedMessage = getExpectedMessageML("payload_xgithubevent_push_expected_message.xml");

    String result = pushGithubParser.parse(Collections.<String, String>emptyMap(), node);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }

  @Test
  public void testPushTagParse() throws IOException, GithubParserException {
    JsonNode node = getJsonFile("payload_xgithubevent_push_tag.json");
    String expectedMessage = getExpectedMessageML("payload_xgithubevent_push_tag_expected_message.xml");

    String result = pushGithubParser.parse(Collections.<String, String>emptyMap(), node);
    result = "<messageML>" + result + "</messageML>";
    assertEquals(expectedMessage, result);
  }
}

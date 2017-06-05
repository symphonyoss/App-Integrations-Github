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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.v2.GithubMetadataParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * Created by campidelli on 08/05/17.
 */
@Ignore("not a test per se")
public abstract class GithubParserTest<T extends GithubMetadataParser> {

  private static final String INTEGRATION_NAME = "github";
  private static final String MOCK_INTEGRATION_USER = "mockUser";


  @Mock
  protected GithubParserUtils utils;

  @Mock
  protected UserService userService;

  @Mock
  protected IntegrationProperties integrationProperties;

  private GithubMetadataParser parser;

  @Before
  public void init() throws IOException {
    parser = getParser();
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);

    try {
      doReturn(null).when(utils).doGetJsonApi(anyString());
    } catch (IOException e) {
      fail("IOException should not be thrown because there is no real API calling, its mocked.");
    }

    mockIntegrationProperties(integrationProperties);
  }

  protected abstract String getExpectedTemplate() throws IOException;

  protected abstract T getParser();

  protected JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }

  protected void testParser(String payloadFile, String expectedFile) throws IOException {
    testParser(Collections.<String, String>emptyMap(), payloadFile, expectedFile);
  }

  protected void testParser(Map<String, String> headerMap, String payloadFile, String expectedFile)
      throws IOException {
    JsonNode node = SimpleFileUtils.readJsonFromFile(payloadFile);
    Message result = parser.parse(headerMap, Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = SimpleFileUtils.readJsonFromFile(expectedFile);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(getExpectedTemplate(), result.getMessage());
  }

  protected void mockIntegrationProperties(IntegrationProperties integrationProperties) {
    doReturn("symphony.com").when(integrationProperties).getApplicationUrl(INTEGRATION_NAME);
  }

}

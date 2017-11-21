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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUSH;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.NullGithubParser;
import org.symphonyoss.integration.webhook.parser.WebHookParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Unit test for {@link V2GithubParserFactory}
 * Created by campidelli on 08/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2GithubParserFactoryTest {

  private static final String MOCK_INTEGRATION_TYPE = "mockType";

  @Spy
  private List<GithubParser> beans = new ArrayList<>();

  @Mock
  private GithubPushMetadataParser pushMetadataParser;

  @Spy
  private NullGithubParser defaultGithubParser;

  @InjectMocks
  private V2GithubParserFactory factory;

  @Before
  public void init() {
    doReturn(Arrays.asList(GITHUB_EVENT_PUSH)).when(pushMetadataParser).getEvents();

    beans.add(pushMetadataParser);
    beans.add(defaultGithubParser);

    factory.init();
  }

  @Test
  public void testNotAcceptable() {
    assertFalse(factory.accept(MessageMLVersion.V1));
  }

  @Test
  public void testAcceptable() {
    assertTrue(factory.accept(MessageMLVersion.V2));
  }

  @Test
  public void testOnConfigChange() {
    IntegrationSettings settings = new IntegrationSettings();
    settings.setType(MOCK_INTEGRATION_TYPE);

    factory.onConfigChange(settings);

    verify(pushMetadataParser, times(1)).setIntegrationUser(MOCK_INTEGRATION_TYPE);
    verify(defaultGithubParser, times(1)).setIntegrationUser(MOCK_INTEGRATION_TYPE);
  }

  @Test
  public void testGetDefaultParser() {
    Map<String, String> emptyMap = Collections.emptyMap();
    String body = "{}";
    WebHookPayload payload = new WebHookPayload(emptyMap, emptyMap, body);

    WebHookParser parser = factory.getParser(payload);
    assertNotNull(parser);
    assertNull(parser.parse(payload));
  }

  @Test
  public void testGetParser() {
    assertEquals(pushMetadataParser, factory.getParser(GITHUB_EVENT_PUSH));
  }
}

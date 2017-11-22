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
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUSH;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;
import java.util.List;

/**
 * Unit test class for {@link GithubPushMetadataParser}
 * Created by campidelli on 03/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPushMetadataParserTest extends GithubParserTest<GithubPushMetadataParser> {

  private static final String PAYLOAD_FILE_PUSH =
      "parser/push/payload_xgithubevent_push.json";
  private static final String EXPECTED_FILE_PUSH =
      "parser/push/v2/expected_xgithub_event_push.json";

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubPush.xml");
  }

  @Override
  protected GithubPushMetadataParser getParser() {
    return new GithubPushMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testSupportedEvents() {
    List<String> events = getParser().getEvents();
    assertNotNull(events);
    assertEquals(1, events.size());
    assertEquals(GITHUB_EVENT_PUSH, events.get(0));
  }

  @Test
  public void testPush() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PUSH, EXPECTED_FILE_PUSH);
  }
}


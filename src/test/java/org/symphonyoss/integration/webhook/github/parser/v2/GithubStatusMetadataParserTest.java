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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;

/**
 * Unit test class for {@link GithubStatusMetadataParser}
 * Created by campidelli on 16/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubStatusMetadataParserTest extends GithubParserTest<GithubStatusMetadataParser> {

  private static final String PAYLOAD_FILE_STATUS =
      "parser/status/payload_xgithubevent_status.json";

  private static final String EXPECTED_FILE_STATUS =
      "parser/status/v2/expected_xgithub_event_status.json";

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubStatus.xml");
  }

  @Override
  protected GithubStatusMetadataParser getParser() {
    return new GithubStatusMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testStatus() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_STATUS, EXPECTED_FILE_STATUS);
  }
}


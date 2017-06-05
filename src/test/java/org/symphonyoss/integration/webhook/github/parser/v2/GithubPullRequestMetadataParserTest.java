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
 * Unit test class for {@link GithubPullRequestMetadataParser}
 * Created by campidelli on 09/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPullRequestMetadataParserTest extends GithubParserTest<GithubPullRequestMetadataParser> {

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

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubPullRequest.xml");
  }

  @Override
  protected GithubPullRequestMetadataParser getParser() {
    return new GithubPullRequestMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testParserAssigned() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_ASSIGNED, EXPECTED_FILE_PR_ASSIGNED);
  }

  @Test
  public void testParserClosed() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_CLOSED, EXPECTED_FILE_PR_CLOSED);
  }

  @Test
  public void testParserLabeled() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_LABELED, EXPECTED_FILE_PR_LABELED);
  }

  @Test
  public void testParserOpened() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_OPENED, EXPECTED_FILE_PR_OPENED);
  }

  @Test
  public void testParserReviewRequest() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_REVIEW_REQUEST, EXPECTED_FILE_PR_REVIEW_REQUEST);
  }

  @Test
  public void testParserReviewRequestRemoved() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_REVIEW_REQUEST_REMOVED, EXPECTED_FILE_PR_REVIEW_REQUEST_REMOVED);
  }

  @Test
  public void testParserSynchronize() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_PR_SYNCHRONIZE, EXPECTED_FILE_PR_SYNCHRONIZE);
  }
}


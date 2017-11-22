package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_COMMIT_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_ISSUE_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_HEADER_EVENT_NAME;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apimentel on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubCommentMetadataParserTest extends GithubParserTest<GithubCommentMetadataParser> {

  private static final String PAYLOAD_FILE_COMMENT_CREATED =
      "parser/commitComment/payload_xgithubevent_commit_comment_created.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT =
      "parser/commitComment/v2/expected_xgithub_event_commit_comment_created.json";

  private static final String PAYLOAD_FILE_COMMENT_WITH_LINE_BREAK =
      "parser/commitComment/payload_xgithubevent_commit_comment_created_with_linebreak.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT_WITH_LINE_BREAK =
      "parser/commitComment/v2/expected_xgithub_event_commit_with_line_break.json";

  private static final String PAYLOAD_FILE_COMMENT_WITH_URL =
      "parser/commitComment/payload_xgithubevent_commit_comment_created_with_URL.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT_WITH_URL =
      "parser/commitComment/v2/expected_xgithub_event_commit_with_url.json";

  private static final String EXPECTED_FILE_CUSTOM_ACTION_HEADER =
      "parser/commitComment/v2/expected_xgithub_event_commit_with_custom_action.json";

  private static final String PAYLOAD_FILE_ISSUE_COMMENT_CREATED =
      "parser/issueComment/payload_xgithubevent_issue_comment_created.json";
  private static final String EXPECTED_FILE_ISSUE_COMMENT_CREATED =
      "parser/issueComment/v2/expected_xgithub_event_issue_comment_created.json";

  private static final String PAYLOAD_FILE_ISSUE_COMMENT_DELETED =
      "parser/issueComment/payload_xgithubevent_issue_comment_deleted.json";
  private static final String EXPECTED_FILE_ISSUE_COMMENT_DELETED =
      "parser/issueComment/v2/expected_xgithub_event_issue_comment_deleted.json";

  private static final String PAYLOAD_FILE_ISSUE_COMMENT_EDITED =
      "parser/issueComment/payload_xgithubevent_issue_comment_edited.json";
  private static final String EXPECTED_FILE_ISSUE_COMMENT_EDITED =
      "parser/issueComment/v2/expected_xgithub_event_issue_comment_edited.json";

  @Override
  @Before
  public void init() throws IOException {
    super.init();
    doReturn("").when(integrationProperties).getApplicationUrl(anyString());
  }

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubComment.xml");
  }

  @Override
  protected GithubCommentMetadataParser getParser() {
    return new GithubCommentMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testSupportedEvents() {
    List<String> events = getParser().getEvents();
    assertNotNull(events);
    assertEquals(2, events.size());
    assertTrue(events.contains(GITHUB_EVENT_COMMIT_COMMENT));
    assertTrue(events.contains(GITHUB_EVENT_ISSUE_COMMENT));
  }

  private void testCommitComment(String eventName, String payloadFile, String expectedFile)
      throws IOException, GithubParserException {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(GITHUB_HEADER_EVENT_NAME, eventName);

    testParser(headerMap, payloadFile, expectedFile);
  }

  @Test
  public void testCommitCommentCreated() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_COMMIT_COMMENT, PAYLOAD_FILE_COMMENT_CREATED,
        EXPECTED_FILE_COMMENT_COMMIT);
  }

  @Test
  public void testCommitCommentWithLineBreak() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_COMMIT_COMMENT, PAYLOAD_FILE_COMMENT_WITH_LINE_BREAK,
        EXPECTED_FILE_COMMENT_COMMIT_WITH_LINE_BREAK);
  }

  @Test
  public void testCommitCommentWithUrl() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_COMMIT_COMMENT, PAYLOAD_FILE_COMMENT_WITH_URL,
        EXPECTED_FILE_COMMENT_COMMIT_WITH_URL);
  }

  @Test
  public void testIssueCommentCreated() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_ISSUE_COMMENT, PAYLOAD_FILE_ISSUE_COMMENT_CREATED,
        EXPECTED_FILE_ISSUE_COMMENT_CREATED);
  }

  @Test
  public void testIssueCommentDeleted() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_ISSUE_COMMENT, PAYLOAD_FILE_ISSUE_COMMENT_DELETED,
        EXPECTED_FILE_ISSUE_COMMENT_DELETED);
  }

  @Test
  public void testIssueCommentEdited() throws IOException, GithubParserException {
    testCommitComment(GITHUB_EVENT_ISSUE_COMMENT, PAYLOAD_FILE_ISSUE_COMMENT_EDITED,
        EXPECTED_FILE_ISSUE_COMMENT_EDITED);
  }

  @Test
  public void testCustomHeader()
      throws IOException, GithubParserException {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(GITHUB_HEADER_EVENT_NAME, "phantom_event");
    testParser(headerMap, PAYLOAD_FILE_COMMENT_WITH_URL, EXPECTED_FILE_CUSTOM_ACTION_HEADER);
  }
}
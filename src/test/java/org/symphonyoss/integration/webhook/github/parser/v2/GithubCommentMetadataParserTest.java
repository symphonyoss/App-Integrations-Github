package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_COMMIT_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_ISSUE_COMMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_HEADER_EVENT_NAME;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apimentel on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubCommentMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

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

  private static String expectedMessageML;

  @Mock
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  @InjectMocks
  private GithubCommentMetadataParser parser;

  @BeforeClass
  public static void setupClass() throws IOException {
    URL fileResource = GithubCommentMetadataParserTest.class.getClassLoader()
        .getResource("templates/templateGithubComment.xml");

    expectedMessageML =
        FileUtils.readFileToString(new File(fileResource.getPath()), Charset.defaultCharset());
  }

  @Before
  public void init() {
    MockitoAnnotations.initMocks(GithubCommentMetadataParserTest.class);
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);

    try {
      doReturn(null).when(utils).doGetJsonApi(anyString());
      doReturn("").when(integrationProperties).getApplicationUrl(anyString());
    } catch (IOException e) {
      fail("IOException should not be thrown because there is no real API calling, its mocked.");
    }
  }

  private void testCommitComment(String eventName, String payloadFile, String expectedFile)
      throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(payloadFile);

    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(GITHUB_HEADER_EVENT_NAME, eventName);

    Message result = parser.parse(headerMap, Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(expectedFile);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(expectedMessageML, result.getMessage());
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
    JsonNode node = readJsonFromFile(PAYLOAD_FILE_COMMENT_WITH_URL);

    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(GITHUB_HEADER_EVENT_NAME, "phantom_event");

    Message result = parser.parse(headerMap, Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_FILE_CUSTOM_ACTION_HEADER);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(expectedMessageML, result.getMessage());
  }
}
package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * Created by apimentel on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubCommitCommentMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String PAYLOAD_FILE_COMMENT_CREATED =
      "payload_xgithubevent_commit_comment_created.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT =
      "parser/commitComment/v2/expected_xgithub_event_commit_comment_created.json";

  private static final String PAYLOAD_FILE_COMMENT_WITH_LINE_BREAK =
      "payload_xgithubevent_commit_comment_created_with_linebreak.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT_WITH_LINE_BREAK =
      "parser/commitComment/v2/expected_xgithub_event_commit_with_line_break.json";

  private static final String PAYLOAD_FILE_COMMENT_WITH_URL =
      "payload_xgithubevent_commit_comment_created_with_URL.json";
  private static final String EXPECTED_FILE_COMMENT_COMMIT_WITH_URL =
      "parser/commitComment/v2/expected_xgithub_event_commit_with_url.json";

  private static String expectedMessageML;

  @Mock
  private GithubParserUtils utils;

  @Mock
  private UserService userService;

  private GithubCommitCommentMetadataParser parser;

  @BeforeClass
  public static void setupClass() throws IOException {
    URL fileResource = GithubCommitCommentMetadataParserTest.class.getClassLoader()
        .getResource("templates/templateGithubCommitComment.xml");

    expectedMessageML =
        FileUtils.readFileToString(new File(fileResource.getPath()), Charset.defaultCharset());
  }

  @Before
  public void init() {
    parser = new GithubCommitCommentMetadataParser(userService, utils);
    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);

    try {
      doReturn(null).when(utils).doGetJsonApi(anyString());
    } catch (IOException e) {
      fail("IOException should not be thrown because there is no real API calling, its mocked.");
    }
  }

  private void testCommitComment(String payloadFile, String expectedFile)
      throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(payloadFile);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(expectedFile);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
    assertEquals(expectedMessageML, result.getMessage());
  }

  @Test
  public void testCommitCommentCreated() throws IOException, GithubParserException {
    testCommitComment(PAYLOAD_FILE_COMMENT_CREATED, EXPECTED_FILE_COMMENT_COMMIT);
  }

  @Test
  public void testCommitCommentWithLineBreak() throws IOException, GithubParserException {
    testCommitComment(PAYLOAD_FILE_COMMENT_WITH_LINE_BREAK,
        EXPECTED_FILE_COMMENT_COMMIT_WITH_LINE_BREAK);
  }

  @Test
  public void testCommitCommentWithUrl() throws IOException, GithubParserException {
    testCommitComment(PAYLOAD_FILE_COMMENT_WITH_URL, EXPECTED_FILE_COMMENT_COMMIT_WITH_URL);
  }
}
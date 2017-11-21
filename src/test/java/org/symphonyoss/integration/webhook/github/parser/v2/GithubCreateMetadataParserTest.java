package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_CREATE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;
import java.util.List;

/**
 * Created by pdarde on 16/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubCreateMetadataParserTest extends GithubParserTest<GithubCreateMetadataParser> {

  private static final String PAYLOAD_TAG_CREATED =
      "parser/created/v1/payload_xgithubevent_tag_created.json";

  private static final String PAYLOAD_BRANCH_CREATED =
      "parser/created/payload_xgithubevent_branch_created.json";

  private static final String EXPECTED_DATA_TAG_CREATED =
      "parser/created/v2/expected_xgithub_event_tag_created_data.json";

  private static final String EXPECTED_DATA_BRANCH_CREATED =
      "parser/created/v2/expected_xgithub_event_branch_created_data.json";

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubCreate.xml");
  }

  @Override
  protected GithubCreateMetadataParser getParser() {
    return new GithubCreateMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testSupportedEvents() {
    List<String> events = getParser().getEvents();
    assertNotNull(events);
    assertEquals(1, events.size());
    assertEquals(GITHUB_EVENT_CREATE, events.get(0));
  }

  @Test
  public void testTagCreated() throws IOException, GithubParserException {
    testParser(PAYLOAD_TAG_CREATED, EXPECTED_DATA_TAG_CREATED);
  }

  @Test
  public void testBranchCreated() throws IOException, GithubParserException {
    testParser(PAYLOAD_BRANCH_CREATED, EXPECTED_DATA_BRANCH_CREATED);
  }
}

package org.symphonyoss.integration.webhook.github.parser.v2;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by pdarde on 16/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubCreateMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String PAYLOAD_TAG_CREATED =
      "parser/created/v1/payload_xgithubevent_tag_created.json";

  private static final String PAYLOAD_BRANCH_CREATED =
      "parser/created/payload_xgithubevent_branch_created.json";

  private static final String EXPECTED_DATA_TAG_CREATED =
      "parser/created/v2/expected_xgithub_event_tag_created_data.json";

  private static final String EXPECTED_DATA_BRANCH_CREATED =
      "parser/created/v2/expected_xgithub_event_branch_created_data.json";

  private static final String EXPECTED_TEMPLATE_CREATED = "parser/created/v2/expected_xgithub_event_create_template";

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  @Mock
  private GithubParserUtils utils;


  private GithubMetadataParser parser;

  @Before
  public void init() {
    parser = new GithubCreateMetadataParser(userService, utils, integrationProperties);

    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);
    mockIntegrationProperties(integrationProperties);
  }

  @Test
  public void testTagCreated() throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(PAYLOAD_TAG_CREATED);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

      JsonNode expectedNode = readJsonFromFile(EXPECTED_DATA_TAG_CREATED);
      String expected = JsonUtils.writeValueAsString(expectedNode);

      assertEquals(expected, result.getData());

      String expectedTemplate = readFile(EXPECTED_TEMPLATE_CREATED);
      assertEquals(expectedTemplate, result.getMessage().replace("\n", ""));
  }


  @Test
  public void testBranchCreated() throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(PAYLOAD_BRANCH_CREATED);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_DATA_BRANCH_CREATED);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());
  }



}

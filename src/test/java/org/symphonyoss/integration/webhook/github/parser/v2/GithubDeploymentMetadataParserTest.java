package org.symphonyoss.integration.webhook.github.parser.v2;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

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
 * Unit test class for {@link GithubDeploymentMetadataParser}
 * Created by crepache on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubDeploymentMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String USER_URL = "https://api.github.com/users/baxterthehacker";

  private static final String EXPECTED_TEMPLATE_DEPLOYMENT =
      "parser/deployment/v2/expected_xgithub_event_deployment_template";

  private static final String PAYLOAD_FILE_DEPLOYMENT =
      "parser/deployment/payload_xgithubevent_deployment.json";

  private static final String EXPECTED_DATA_DEPLOYMENT =
      "parser/deployment/v2/expected_xgithub_event_deployment_data.json";

  public static final String PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_DESCRIPTION_JSON =
      "parser/deployment/payload_xgithubevent_deployment_without_description.json";

  public static final String
      PARSER_V2_PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_USERINFO_EXPECTED_DATA_JSON =
      "parser/deployment/v2/expected_xgithubevent_deployment_without_userinfo_expected_data.json";

  public static final String
      PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_USERINFO_EXPECTED_DATA_JSON =
      "parser/deployment/v2"
          + "/expected_xgithubevent_deployment_status_without_userinfo_expected_data.json";

  public static final String PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_DESCRIPTION_JSON =
      "parser/deployment/payload_xgithubevent_deployment_status_without_description.json";

  public static final String
      PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUB_EVENT_DEPLOYMENT_STATUS_DATA_JSON =
      "parser/deployment/v2/payload_xgithub_event_deployment_status_data.json";

  public static final String PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_JSON =
      "parser/deployment/payload_xgithubevent_deployment_status.json";

  public static final String PAYLOAD_GITHUB_PUBLIC_INFO_BAXTERTHEHACKER_JSON =
      "parser/payload_github_public_info_baxterthehacker.json";

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  @Mock
  private GithubParserUtils utils;

  private GithubMetadataParser parser;

  @Before
  public void init() {
    parser = new GithubDeploymentMetadataParser(userService, utils, integrationProperties);

    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);
    mockIntegrationProperties(integrationProperties);
  }

  @Test
  public void testDeploymentEvent() throws IOException, GithubParserException {
    JsonNode node = readJsonFromFile(PAYLOAD_FILE_DEPLOYMENT);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertNotNull(result);

    JsonNode expectedNode = readJsonFromFile(EXPECTED_DATA_DEPLOYMENT);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    assertEquals(expected, result.getData());

    String expectedTemplate = readFile(EXPECTED_TEMPLATE_DEPLOYMENT);
    assertEquals(expectedTemplate, result.getMessage().replace("\n", ""));
  }

  @Test
  public void testDeploymentEventWithoutUserInfoAndDescription() throws IOException {
    JsonNode node = readJsonFromFile(PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_DESCRIPTION_JSON);

    JsonNode expectedNode = readJsonFromFile(
        PARSER_V2_PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
    String expected = JsonUtils.writeValueAsString(expectedNode);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertEquals(expected, result.getData());
  }

  @Test
  public void testDeploymentStatusEvent() throws IOException {
    JsonNode publicUserInfoBaxter = readJsonFromFile(
        PAYLOAD_GITHUB_PUBLIC_INFO_BAXTERTHEHACKER_JSON);
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(USER_URL);

    JsonNode node = readJsonFromFile(PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_JSON);

    JsonNode expectedNode = readJsonFromFile(
        PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUB_EVENT_DEPLOYMENT_STATUS_DATA_JSON);
    String expected = JsonUtils.writeValueAsString(expectedNode);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertEquals(expected, result.getData());
  }

  @Test
  public void testDeploymentStatusEventWithoutUserInfoAndDescription()
      throws IOException, GithubParserException {
    doReturn(null).when(utils).doGetJsonApi(USER_URL);

    JsonNode node = readJsonFromFile(
        PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_DESCRIPTION_JSON);

    JsonNode expectedNode = readJsonFromFile(
        PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
    String expected = JsonUtils.writeValueAsString(expectedNode);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertEquals(expected, result.getData());
  }
}

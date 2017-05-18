package org.symphonyoss.integration.webhook.github.parser.v2;

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
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * Unit test class for {@link GithubPublicMetadataParser}
 * Created by crepache on 16/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPublicMetadataParserTest extends GithubParserTest {

  private static final String MOCK_INTEGRATION_USER = "mockUser";

  private static final String USER_URL = "https://api.github.com/users/baxterthehacker";
  public static final String
      PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_WITHOUT_USERINFO_EXPECTED_DATA_JSON =
      "parser/public/v2/payload_xgithubevent_public_without_userinfo_expected_data.json";
  public static final String PAYLOAD_XGITHUBEVENT_PUBLIC_JSON =
      "parser/public/payload_xgithubevent_public.json";
  public static final String PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_TEMPLATE =
      "parser/public/v2/payload_xgithubevent_public_expected_template";
  public static final String PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_DATA_JSON =
      "parser/public/v2/payload_xgithubevent_public_expected_data.json";
  public static final String PAYLOAD_XGITHUBEVENT_PUBLIC_JSON1 =
      "parser/public/payload_xgithubevent_public.json";

  @Mock
  private UserService userService;

  @Mock
  private IntegrationProperties integrationProperties;

  @Mock
  private GithubParserUtils utils;


  private GithubMetadataParser parser;

  @Before
  public void init() {
    parser = new GithubPublicMetadataParser(userService, utils, integrationProperties);

    parser.init();
    parser.setIntegrationUser(MOCK_INTEGRATION_USER);
    mockIntegrationProperties(integrationProperties);
  }

  @Test
  public void testPublicEvent() throws IOException {
    JsonNode publicUserInfoBaxter = readJsonFromFile(
        "parser/payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(USER_URL);

    JsonNode node = readJsonFromFile(PAYLOAD_XGITHUBEVENT_PUBLIC_JSON1);

    JsonNode expectedNode = readJsonFromFile(
        PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_DATA_JSON);
    String expected = JsonUtils.writeValueAsString(expectedNode);
    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertEquals(expected, result.getData());

    String expectedTemplate = readFile(
        PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_TEMPLATE);
    assertEquals(expectedTemplate, result.getMessage().replace("\n", ""));
  }

  @Test
  public void testPublicEventWithoutFullName() throws IOException {
    doReturn(null).when(utils).doGetJsonApi(USER_URL);

    JsonNode node = readJsonFromFile(PAYLOAD_XGITHUBEVENT_PUBLIC_JSON);

    JsonNode expectedNode = readJsonFromFile(
        PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
    String expected = JsonUtils.writeValueAsString(expectedNode);

    Message result = parser.parse(Collections.<String, String>emptyMap(), node);

    assertEquals(expected, result.getData());
  }

}

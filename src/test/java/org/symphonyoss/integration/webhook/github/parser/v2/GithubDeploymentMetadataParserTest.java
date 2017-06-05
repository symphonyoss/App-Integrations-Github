package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;

/**
 * Unit test class for {@link GithubDeploymentMetadataParser}
 * Created by crepache on 10/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubDeploymentMetadataParserTest
    extends GithubParserTest<GithubDeploymentMetadataParser> {

  private static final String USER_URL = "https://api.github.com/users/baxterthehacker";

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

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubDeployment.xml");
  }

  @Override
  protected GithubDeploymentMetadataParser getParser() {
    return new GithubDeploymentMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testDeploymentEvent() throws IOException, GithubParserException {
    testParser(PAYLOAD_FILE_DEPLOYMENT, EXPECTED_DATA_DEPLOYMENT);
  }

  @Test
  public void testDeploymentEventWithoutUserInfoAndDescription() throws IOException {
    testParser(PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_DESCRIPTION_JSON,
        PARSER_V2_PAYLOAD_XGITHUBEVENT_DEPLOYMENT_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
  }

  @Test
  public void testDeploymentStatusEvent() throws IOException {
    JsonNode publicUserInfoBaxter = readJsonFromFile(
        PAYLOAD_GITHUB_PUBLIC_INFO_BAXTERTHEHACKER_JSON);
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(USER_URL);

    testParser(PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_JSON,
        PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUB_EVENT_DEPLOYMENT_STATUS_DATA_JSON);
  }

  @Test
  public void testDeploymentStatusEventWithoutUserInfoAndDescription()
      throws IOException, GithubParserException {
    doReturn(null).when(utils).doGetJsonApi(USER_URL);

    testParser(PAYLOAD_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_DESCRIPTION_JSON,
        PARSER_DEPLOYMENT_V2_EXPECTED_XGITHUBEVENT_DEPLOYMENT_STATUS_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
  }
}

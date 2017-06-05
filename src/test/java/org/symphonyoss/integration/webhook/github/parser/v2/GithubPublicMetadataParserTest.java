package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.utils.SimpleFileUtils;
import org.symphonyoss.integration.webhook.github.parser.GithubParserTest;

import java.io.IOException;

/**
 * Unit test class for {@link GithubPublicMetadataParser}
 * Created by crepache on 16/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GithubPublicMetadataParserTest extends GithubParserTest<GithubPublicMetadataParser> {

  private static final String USER_URL = "https://api.github.com/users/baxterthehacker";

  private static final String
      PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_WITHOUT_USERINFO_EXPECTED_DATA_JSON =
      "parser/public/v2/payload_xgithubevent_public_without_userinfo_expected_data.json";

  private static final String PAYLOAD_XGITHUBEVENT_PUBLIC_JSON =
      "parser/public/payload_xgithubevent_public.json";

  private static final String PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_DATA_JSON =
      "parser/public/v2/payload_xgithubevent_public_expected_data.json";

  private static final String PAYLOAD_XGITHUBEVENT_PUBLIC_JSON1 =
      "parser/public/payload_xgithubevent_public.json";

  @Override
  protected String getExpectedTemplate() throws IOException {
    return SimpleFileUtils.readFile("templates/templateGithubPublic.xml");
  }

  @Override
  protected GithubPublicMetadataParser getParser() {
    return new GithubPublicMetadataParser(userService, utils, integrationProperties);
  }

  @Test
  public void testPublicEvent() throws IOException {
    JsonNode publicUserInfoBaxter = readJsonFromFile(
        "parser/payload_github_public_info_baxterthehacker.json");
    doReturn(publicUserInfoBaxter).when(utils).doGetJsonApi(USER_URL);

    testParser(PAYLOAD_XGITHUBEVENT_PUBLIC_JSON1,
        PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_EXPECTED_DATA_JSON);
  }

  @Test
  public void testPublicEventWithoutFullName() throws IOException {
    doReturn(null).when(utils).doGetJsonApi(USER_URL);

    testParser(PAYLOAD_XGITHUBEVENT_PUBLIC_JSON,
        PARSER_PUBLIC_V2_PAYLOAD_XGITHUBEVENT_PUBLIC_WITHOUT_USERINFO_EXPECTED_DATA_JSON);
  }
}

package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by crepache on 09/05/17.
 */
@Component
public class GithubDeploymentMetadataParser extends GithubMetadataParser {


  private static final String METADATA_FILE = "metadataGithubDeployment.xml";

  private static final String TEMPLATE_FILE = "templateGithubDeployment.xml";

  public GithubDeploymentMetadataParser(UserService userService) {
    super(userService);
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_DEPLOYMENT);
  }

  @Override
  protected String getTemplateFile() {
    return TEMPLATE_FILE;
  }

  @Override
  protected String getMetadataFile() {
    return METADATA_FILE;
  }

  @Override
  protected void preProcessInputData(JsonNode input) {
    // Do nothing
  }
}

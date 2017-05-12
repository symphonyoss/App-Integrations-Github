package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT_STATUS;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by crepache on 11/05/17.
 */
@Component
public class GithubDeploymentStatusMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubDeploymentStatus.xml";

  private static final String TEMPLATE_FILE = "templateGithubDeploymentStatus.xml";

  public GithubDeploymentStatusMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_DEPLOYMENT_STATUS);
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
    proccessIconURL(input);
  }
}

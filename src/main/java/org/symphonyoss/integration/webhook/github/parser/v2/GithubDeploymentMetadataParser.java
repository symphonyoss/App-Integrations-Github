package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT_STATUS;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.CREATOR_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.DEPLOYMENT_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to validate the events 'deployment' and 'deployment_status' sent by
 * Github Webhook when the Agent version is equal to or greater than '1.46.0'.
 * Created by crepache on 09/05/17.
 */
@Component
public class GithubDeploymentMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubDeployment.xml";

  private static final String TEMPLATE_FILE = "templateGithubDeployment.xml";

  public GithubDeploymentMetadataParser(UserService userService, GithubParserUtils utils,
      IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_DEPLOYMENT, GITHUB_EVENT_DEPLOYMENT_STATUS);
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
    processUser(input.path(DEPLOYMENT_TAG).path(CREATOR_TAG));
  }
}

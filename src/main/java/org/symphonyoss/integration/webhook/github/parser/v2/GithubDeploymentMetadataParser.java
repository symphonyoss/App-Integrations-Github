package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT;
import static org.symphonyoss.integration.webhook.github.GithubEventConstants
    .GITHUB_EVENT_DEPLOYMENT_STATUS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.GithubEventConstants;
import org.symphonyoss.integration.webhook.github.GithubEventTags;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by crepache on 09/05/17.
 */
@Component
public class GithubDeploymentMetadataParser extends GithubMetadataParser {


  private static final String METADATA_FILE = "metadataGithubDeployment.xml";

  private static final String TEMPLATE_FILE = "templateGithubDeployment.xml";

  public GithubDeploymentMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
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
    proccessUserGithub(input);
    processTypeEvent(input);
  }

  private void proccessUserGithub(JsonNode node) {
    JsonNode nodeCreator = node.path(GithubEventTags.DEPLOYMENT_TAG).path(GithubEventTags.CREATOR_TAG);

    ((ObjectNode) nodeCreator).put(GithubEventTags.LOGIN_TAG, getGithubUserPublicName(nodeCreator));
  }

  private void processTypeEvent(JsonNode node) {
    JsonNode nodeStatus = node.path(GithubEventTags.DEPLOYMENT_STATUS_TAG);

    if (nodeStatus.getNodeType() == JsonNodeType.MISSING) {
      ((ObjectNode) node).put(GithubEventTags.TYPE_EVENT, GithubEventTags.DEPLOYMENT_TAG);
    } else {
      ((ObjectNode) node).put(GithubEventTags.TYPE_EVENT, GithubEventTags.DEPLOYMENT_STATUS_TAG);
    }
  }

}

package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventConstants.GITHUB_EVENT_PUBLIC;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.GithubEventTags;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible to validate the event 'Public' sent by Github Webhook when
 * the Agent version is equal to or greater than '1.46.0'.
 * Created by crepache on 15/05/17.
 */
@Component
public class GithubPublicMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubPublic.xml";

  private static final String TEMPLATE_FILE = "templateGithubPublic.xml";

  public GithubPublicMetadataParser(UserService userService, GithubParserUtils utils, IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GITHUB_EVENT_PUBLIC);
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
  }

  private void proccessUserGithub(JsonNode node) {
    JsonNode nodeCreator = node.path(GithubEventTags.SENDER_TAG);

    ((ObjectNode) nodeCreator).put(GithubEventTags.LOGIN_TAG, getGithubUserPublicName(nodeCreator));
  }
}

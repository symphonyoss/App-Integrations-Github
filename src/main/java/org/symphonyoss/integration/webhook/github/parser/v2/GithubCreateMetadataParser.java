package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventTags.CREATOR_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.SENDER_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.GithubEventConstants;
import org.symphonyoss.integration.webhook.github.GithubEventTags;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pdarde on 11/05/17.
 */
@Component
public class GithubCreateMetadataParser extends GithubMetadataParser {

  private static final String METADATA_FILE = "metadataGithubCreate.xml";

  private static final String TEMPLATE_FILE = "templateGithubCreate.xml";

  public GithubCreateMetadataParser(UserService userService, GithubParserUtils utils,
      IntegrationProperties integrationProperties) {
    super(userService, utils, integrationProperties);
  }

  @Override
  public List<String> getEvents() {
    return Arrays.asList(GithubEventConstants.GITHUB_EVENT_CREATE);
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
    processUser(input.path(SENDER_TAG));
  }
}

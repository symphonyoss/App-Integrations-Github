package org.symphonyoss.integration.webhook.github.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Adapt the interface {@link WebHookParser} to {@link GithubParser}.
 * {@link WebHookParser} is the common interface implemented by all the parsers that support
 * MessageML v2.
 * {@link GithubParser} is the interface implemented by all the Github parsers.
 * Created by campidelli on 02/05/17.
 */
public class GithubWebHookParserAdapter implements WebHookParser {

  private GithubParser parser;

  public GithubWebHookParserAdapter(GithubParser parser) {
    this.parser = parser;
  }

  @Override
  public List<String> getEvents() {
    return parser.getEvents();
  }

  @Override
  public Message parse(WebHookPayload payload) throws WebHookParseException {
    try {
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());
      Map<String, String> parameters = payload.getParameters();
      Map<String, String> headers = payload.getHeaders();

      return parser.parse(headers, parameters, rootNode);
    } catch (IOException e) {
      throw new GithubParserException(
          "Something went wrong while trying to convert your message to the expected format", e);
    }
  }

}

/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.github.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Utilities class to help validate messages from GitHub.
 *
 * Created by Milton Quilzini on 13/09/16.
 */
@Lazy
@Component
public class GithubParserUtils {

  private static final Logger LOG = LoggerFactory.getLogger(GithubParserUtils.class);

  private List<String> unknownHosts = Collections.synchronizedList(new ArrayList<String>());

  private Client baseClientTargetBuilder;

  public GithubParserUtils() {
    baseClientTargetBuilder = ClientBuilder.newBuilder().build();
    baseClientTargetBuilder.property(ClientProperties.CONNECT_TIMEOUT, 15000);
    baseClientTargetBuilder.property(ClientProperties.READ_TIMEOUT, 15000);
  }

  /**
   * Hits an URL with http GET method, without any authentication.
   * Expects and returns a formatted json as an answer, null otherwise.
   *
   * @param url the URL to hit.
   * @return expects and returns a formatted JSON as an answer, null otherwise.
   * @throws IOException if something goes wrong while converting the answer into a JSON.
   */
  public JsonNode doGetJsonApi(String url) throws IOException {
    WebTarget githubWebTarget = baseClientTargetBuilder.target(url);

    Response response = null;
    try {
      response = githubWebTarget.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        return JsonUtils.readTree((InputStream) response.getEntity());
      } else {
        return null;
      }
    } catch (ProcessingException e){
      logUnknownHostException(e, url);
      return null;
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  /**
   * Log {@link UnknownHostException} just one time.
   * @param exception {@link ProcessingException} that wraps the root cause
   * @param url URL to hit.
   */
  private void logUnknownHostException(ProcessingException exception, String url) {
    if (!UnknownHostException.class.isInstance(exception.getCause())) {
      throw exception;
    }

    try {
      String host = new URI(url).getHost();

      if (!unknownHosts.contains(host)) {
        unknownHosts.add(host);
        LOG.error("Couldn't reach GitHub API due to: Host {} unreachable", host);
      }
    } catch (URISyntaxException e) {
      LOG.error("Invalid GitHub URL: {}", url);
    }
  }

}

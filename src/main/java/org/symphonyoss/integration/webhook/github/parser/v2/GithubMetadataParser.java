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

package org.symphonyoss.integration.webhook.github.parser.v2;

import static org.symphonyoss.integration.webhook.github.GithubEventTags.ASSIGNEE_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.LOGIN_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.NAME_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.SENDER_TAG;
import static org.symphonyoss.integration.webhook.github.GithubEventTags.URL_TAG;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.service.UserService;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserException;
import org.symphonyoss.integration.webhook.github.parser.GithubParserUtils;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ws.rs.ProcessingException;

/**
 * Abstract Github parser responsible to augment the Github input data querying the user API and
 * pre-processing the input data.
 * Created by campidelli on 02/05/17.
 */
public abstract class GithubMetadataParser extends MetadataParser implements GithubParser {

  private static final Logger LOG = LoggerFactory.getLogger(GithubMetadataParser.class);

  private static final int CACHE_EXPIRATION_IN_MINUTES = 60;

  private UserService userService;

  private String integrationUser;

  private GithubParserUtils utils;

  private LoadingCache<String, String> userServiceInfoCache;

  @Autowired
  public GithubMetadataParser(UserService userService, GithubParserUtils utils) {
    this.userService = userService;
    this.utils = utils;
  }

  @Override
  public void setIntegrationUser(String integrationUser) {
    this.integrationUser = integrationUser;
  }

  @Override
  public Message parse(Map<String, String> parameters, JsonNode node) throws GithubParserException {
    return parse(node);
  }

  @PostConstruct
  @Override
  public void init() {
    super.init();
    userServiceInfoCache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_EXPIRATION_IN_MINUTES,
        TimeUnit.MINUTES).build(new CacheLoader<String, String>() {
      /**
       * This is called when a key (url) is not found in cache. It tries do call Github's API and
       * then put this information in cache.
       * @param url Key used to find user info in cache or call the remote API.
       * @return Github User public name or "" if nothing was found.
       */
      @Override
      public String load(String url) {
        try {
          JsonNode publicUserInfo = utils.doGetJsonApi(url);
          if (publicUserInfo != null && !publicUserInfo.path(NAME_TAG).isNull()) {
            return publicUserInfo.path(NAME_TAG).textValue();
          }
        } catch (IOException e) {
          LOG.warn("Couldn't reach GitHub API due to " + e.getMessage(), e);
        } catch (ProcessingException e) {
          Throwable cause = e.getCause();
          LOG.warn("Couldn't reach GitHub API due to " + cause.getMessage(), e);
        }
        return StringUtils.EMPTY;
      }
    });
  }

  /**
   * Tries to hit GitHub's user API to retrieve a given public user name. It uses a local cache
   * to avoid lags due to HTTP API calls.
   * @param userNode {@link JsonNode} that contains the user info.
   * @return the public user name or user login if no info was found.
   */
  protected String getGithubUserPublicName(JsonNode userNode) {
    String userPublicName = null;
    String login = userNode.path(LOGIN_TAG).asText();

    try {
      String url = userNode.path(URL_TAG).asText();
      userPublicName = userServiceInfoCache.get(url);
      if (StringUtils.isEmpty(userPublicName)) {
        userServiceInfoCache.put(url, login);
      }

    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      LOG.warn("Couldn't reach GitHub API due to " + cause.getMessage(), e);
    }

    return StringUtils.isEmpty(userPublicName) ? login : userPublicName;
  }

  /**
   * Enhance user information with it's Github public name.
   * @param userNode JSON input payload
   */
  protected void processUser(JsonNode userNode) {
    if (!userNode.isMissingNode() && !userNode.isNull()) {
      String publicName = getGithubUserPublicName(userNode);
      ((ObjectNode) userNode).put(NAME_TAG, publicName);
    }
  }

}
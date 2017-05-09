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

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.github.parser.GithubParser;
import org.symphonyoss.integration.webhook.github.parser.GithubParserFactory;
import org.symphonyoss.integration.webhook.github.parser.v1.V1GithubParserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser factory for the MessageML v2.
 * Created by campidelli on 02/05/17.
 */
@Component
public class V2GithubParserFactory extends GithubParserFactory {


  @Autowired
  private List<GithubMetadataParser> beans;

  @Autowired
  private V1GithubParserFactory fallbackFactory;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V2.equals(version);
  }

  @Override
  protected List<GithubParser> getBeans() {
    return new ArrayList<GithubParser>(beans);
  }

  @Override
  public GithubParser getParser(String eventName) {
    GithubParser result = super.getParser(eventName);

    if (result == null) {
      // Fallback use V1 Factory
      return fallbackFactory.getParser(eventName);
    }

    return result;
  }
}

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

package org.symphonyoss.integration.webhook.github;

/**
 * Groups all handled actions from GitHub.
 * Created by campidelli on 16/05/17.
 */
public final class GithubActionConstants {

  /**
   * Prevents initialization.
   */
  private GithubActionConstants() {
  }

  public static final String GITHUB_ACTION_CREATED = "created";

  public static final String GITHUB_ACTION_ADDED = "added";

  public static final String GITHUB_ACTION_EDITED = "edited";

  public static final String GITHUB_ACTION_DELETED = "deleted";

  public static final String GITHUB_ACTION_SYNCHRONIZE = "synchronize";

  public static final String GITHUB_ACTION_REVIEW_REQUESTED = "review_requested";

  public static final String GITHUB_ACTION_REVIEW_REQUEST_REMOVED = "review_request_removed";
}

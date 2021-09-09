/*
 * Copyright (c) Octopus Deploy and contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  these files except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package octopus.teamcity.agent.generic.in_sdk;

import java.net.URL;
import java.util.Optional;

import com.google.common.base.Preconditions;

public class ProxyData {
  private final URL proxyUrl;
  private final Optional<String> username;
  private final Optional<String> password;

  public ProxyData(final URL proxyUrl, final String username, final String password) {
    Preconditions.checkNotNull(username, "Username must be specified for an authenticating proxy");
    Preconditions.checkNotNull(password, "Password must be specified for an authenticating proxy");
    this.proxyUrl = proxyUrl;
    this.username = Optional.of(username);
    this.password = Optional.of(password);
  }

  public ProxyData(final URL proxyUrl) {
    this.proxyUrl = proxyUrl;
    this.username = Optional.empty();
    this.password = Optional.empty();
  }

  public URL getProxyUrl() {
    return proxyUrl;
  }

  public Optional<String> getUsername() {
    return username;
  }

  public Optional<String> getPassword() {
    return password;
  }
}

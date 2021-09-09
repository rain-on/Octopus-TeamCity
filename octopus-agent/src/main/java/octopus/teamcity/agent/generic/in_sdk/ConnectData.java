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
import java.time.Duration;
import java.util.Optional;

import com.google.common.base.Preconditions;

public class ConnectData {

  private final URL octopusServerUrl;
  private final String apiKey;
  private final Optional<ProxyData> proxy;
  private final Duration connectTimeout;

  public ConnectData(
      final URL octopusServerUrl, final String apiKey, final Duration connectTimeout) {
    this(octopusServerUrl, apiKey, connectTimeout, Optional.empty());
  }

  public ConnectData(
      final URL octopusServerUrl,
      final String apiKey,
      final Duration connectTimeout,
      final Optional<ProxyData> proxy) {
    Preconditions.checkNotNull(
        octopusServerUrl, "Cannot construct an Octopus connection without a server URL");
    Preconditions.checkNotNull(apiKey, "Cannot construct an Octopus connection without an API key");
    Preconditions.checkNotNull(
        connectTimeout, "Cannot construct an Octopus connection without a connectTimeout");
    Preconditions.checkNotNull(
        proxy, "ProxyData must be specified - Optional.empty() is valid if not " + "required");

    this.octopusServerUrl = octopusServerUrl;
    this.apiKey = apiKey;
    this.connectTimeout = connectTimeout;
    this.proxy = proxy;
  }

  public URL getOctopusServerUrl() {
    return octopusServerUrl;
  }

  public String getApiKey() {
    return apiKey;
  }

  public Optional<ProxyData> getProxyData() {
    return proxy;
  }

  public Duration getConnectTimeout() {
    return connectTimeout;
  }
}

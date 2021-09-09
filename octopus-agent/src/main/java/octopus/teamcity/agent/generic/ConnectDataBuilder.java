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

package octopus.teamcity.agent.generic;

import com.octopus.openapi.invoker.ApiClient;

import java.net.URL;

public class ConnectDataBuilder {
  private URL serverUrl;
  private String apiKey;
  private ProxyData proxy;

  public ConnectDataBuilder setOctopusServerUrl(final URL serverUrl) {
    this.serverUrl = serverUrl;
    return this;
  }

  public ConnectDataBuilder setApiKey(final String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  public ConnectDataBuilder setProxy(final ProxyData proxy) {
    this.proxy = proxy;
    return this;
  }

  public ConnectData build() {

  }

}
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

import com.octopus.sdk.http.OctopusClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.google.common.base.Preconditions;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public class OctopusClientFactory {

  /* If a client of this library requires specific settings on the httpclient, they can be set on the builder, which
  is then mutated with proxy data in the connectData - if nothing special is required of the http client, the client
  can use the function which does not accept the builder*/
  public static OctopusClient createClient(
      final OkHttpClient.Builder clientBuilder, final ConnectData connectData) {
    if (connectData.getProxyData().isPresent()) {
      addProxy(clientBuilder, connectData.getProxyData().get());
    }

    final OkHttpClient httpClient = clientBuilder.build();
    return new OctopusClient(
        httpClient, connectData.getOctopusServerUrl(), connectData.getApiKey());
  }

  public static OctopusClient createClient(final ConnectData connectData) {
    Preconditions.checkNotNull(connectData, "Cannot create connection with no connection data");
    return createClient(new OkHttpClient.Builder(), connectData);
  }

  private static void addProxy(final OkHttpClient.Builder builder, final ProxyData proxyData) {
    final URL proxyUrl = proxyData.getProxyUrl();
    final Proxy proxy =
        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort()));
    builder.proxy(proxy);

    if (proxyData.getUsername().isPresent() && proxyData.getPassword().isPresent()) {
      builder.proxyAuthenticator(
          createAuthenticator(proxyData.getUsername().get(), proxyData.getPassword().get()));
    }
  }

  private static Authenticator createAuthenticator(final String username, final String password) {
    return (route, response) -> {
      String credential = Credentials.basic(username, password);
      return response.request().newBuilder().header("Proxy-Authorization", credential).build();
    };
  }
}

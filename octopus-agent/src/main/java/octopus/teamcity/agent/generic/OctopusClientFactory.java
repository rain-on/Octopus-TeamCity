/*
 * Copyright (c) Octopus Deploy and contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * these files except in compliance with the License. You may obtain a copy of the
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

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.octopus.sdk.http.OctopusClient;
import com.octopus.sdk.model.RootDocument;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class OctopusClientFactory {

  private static final String ROOT_PATH = "api";

  private static final Logger LOG = LogManager.getLogger();

  /* If a client of this library requires specific settings on the httpclient, they can be set on the builder, which
  is then mutated with proxy data in the connectData - if nothing special is required of the http client, the client
  can use the function which does not accept the builder*/
  public static OctopusClient createClient(
      final OkHttpClient.Builder clientBuilder, final ConnectData connectData) {
    if (connectData.getProxyData().isPresent()) {
      addProxy(clientBuilder, connectData.getProxyData().get());
    }

    final OkHttpClient httpClient = clientBuilder.build();
    final RootDocument rootDoc = fetchRootDocument(httpClient, connectData.getOctopusServerUrl());
    return new OctopusClient(
        httpClient, connectData.getOctopusServerUrl(), rootDoc, connectData.getApiKey());
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

    if (proxyData.getUsername() != null && proxyData.getPassword() != null) {
      builder.proxyAuthenticator(
          createAuthenticator(proxyData.getUsername(), proxyData.getPassword()));
    }
  }

  private static Authenticator createAuthenticator(final String username, final String password) {
    return (route, response) -> {
      String credential = Credentials.basic(username, password);
      return response.request().newBuilder().header("Proxy-Authorization", credential).build();
    };
  }

  public static RootDocument fetchRootDocument(final OkHttpClient httpClient, final URL serverUrl) {
    Preconditions.checkNotNull(
        httpClient, "Failed to specify a httpClient for Octopus server connection");
    Preconditions.checkNotNull(
        serverUrl, "Failed to specify the URL at which Octopus Server can be found");

    final HttpUrl.Builder urlBuilder = HttpUrl.parse(serverUrl.toString()).newBuilder();
    urlBuilder.addPathSegments(ROOT_PATH);
    final Request request = new Request.Builder().url(urlBuilder.build()).get().build();
    try (final Response response = httpClient.newCall(request).execute()) {
      return extractRootDocument(response);
    } catch (IOException e) {
      LOG.error("Failed to connect to an Octopus Server at " + serverUrl, e);
      throw new UncheckedIOException(e);
    }
  }

  private static RootDocument extractRootDocument(final Response response) throws IOException {
    final String responseBody = response.body().string();
    if (!response.isSuccessful()) {
      final String errorMessage =
          String.format(
              "Octopus Server at '%s' rejected request for the root document " + "(%d:%s)",
              response.request().url(), response.code(), responseBody);
      throw new RuntimeException(errorMessage);
    }

    try {
      return new Gson().fromJson(responseBody, RootDocument.class);
    } catch (final JsonSyntaxException e) {
      final String errorMessage =
          String.format(
              "Octopus Server at '%s' did not supply a valid root document %s",
              response.request().url(), responseBody);
      throw new RuntimeException(errorMessage, e);
    }
  }
}

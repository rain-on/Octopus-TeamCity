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

import com.octopus.sdk.api.OverwriteMode;

import java.net.MalformedURLException;
import java.net.URL;

import octopus.teamcity.agent.generic.in_sdk.ConnectData;
import octopus.teamcity.agent.generic.in_sdk.ConnectDataBuilder;
import octopus.teamcity.agent.generic.in_sdk.ProxyData;
import octopus.teamcity.common.commonstep.CommonStepUserData;

public class TypeConverters {

  public static OverwriteMode from(final octopus.teamcity.common.OverwriteMode input) {
    switch (input) {
      case OverwriteExisting:
        return OverwriteMode.OverwriteExisting;
      case IgnoreIfExists:
        return OverwriteMode.IgnoreIfExists;
      case FailIfExists:
        return OverwriteMode.FailIfExists;
    }
    throw new IllegalArgumentException("No matching output available");
  }

  /** This assumes the userData contains correctly formatted strings * */
  public static ConnectData from(final CommonStepUserData userData) throws MalformedURLException {
    final ConnectDataBuilder builder = new ConnectDataBuilder();
    builder.withOctopusServerUrl(new URL(userData.getServerUrl())).withApiKey(userData.getApiKey());
    if (userData.getProxyRequired()) {
      final ProxyData proxy =
          new ProxyData(
              new URL(userData.getProxyServerUrl()),
              userData.getProxyUsername(),
              userData.getProxyPassword());
      builder.withProxy(proxy);
    }

    return builder.build();
  }
}

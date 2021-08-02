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

package octopus.teamcity.common.pushpackage;

public class PushPackageKeys {

  // Note: key strings must be unique across all operations (thus the "pp"" prefix)

  public enum Keys {
    PACKAGE_PATHS("octopus_pp_packagepaths"),
    OVERWRITE_MODE("octopus_pp_overwritemode"),
    USE_DELTA_COMPRESSION("octopus_pp_usedeltacompression"),
    PUBLISH_ARTIFACTS("octopus_pp_publishartifacts");

    private final String keyString;

    Keys(final String keyString) {
      this.keyString = keyString;
    }

    public String getKeyString() {
      return keyString;
    }
  }

  public PushPackageKeys() {}

  public String getPackagePathsKey() {
    return Keys.PACKAGE_PATHS.keyString;
  }

  public String getOverwriteModeKey() {
    return Keys.OVERWRITE_MODE.keyString;
  }

  public String getDeltaComparison() {
    return Keys.USE_DELTA_COMPRESSION.keyString;
  }

  public String getPublishArtifactsKey() {
    return Keys.PUBLISH_ARTIFACTS.keyString;
  }
}

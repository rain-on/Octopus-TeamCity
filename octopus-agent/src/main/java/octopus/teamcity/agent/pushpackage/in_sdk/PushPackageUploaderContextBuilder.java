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

package octopus.teamcity.agent.pushpackage.in_sdk;

import com.octopus.sdk.api.OverwriteMode;

import java.io.File;
import java.util.Optional;

import com.google.common.base.Preconditions;

public class PushPackageUploaderContextBuilder {
  private Optional<String> spaceName = Optional.empty();
  private File fileToUpload;
  private OverwriteMode overwriteMode;

  public PushPackageUploaderContextBuilder withSpaceName(final String spaceName) {
    this.spaceName = Optional.ofNullable(spaceName);
    return this;
  }

  public PushPackageUploaderContextBuilder withFileToUpload(final File fileToUpload) {
    this.fileToUpload = fileToUpload;
    return this;
  }

  public PushPackageUploaderContextBuilder withOverwriteMode(final OverwriteMode overwriteMode) {
    this.overwriteMode = overwriteMode;
    return this;
  }

  public PushPackageUploaderContext build() {
    Preconditions.checkNotNull(
        fileToUpload, "The file to be pushed to Octopus Server must be specified");
    Preconditions.checkNotNull(
        overwriteMode,
        "overwriteMode must be specified when pushing packages to Octopus " + "Server");

    return new PushPackageUploaderContext(spaceName, fileToUpload, overwriteMode);
  }
}

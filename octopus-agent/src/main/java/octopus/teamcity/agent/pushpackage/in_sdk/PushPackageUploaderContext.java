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
import java.util.StringJoiner;

public class PushPackageUploaderContext {

  private final Optional<String> spaceName;
  private final File filename;
  private final OverwriteMode overwriteMode;

  public PushPackageUploaderContext(
      final Optional<String> spaceName, final File filename, final OverwriteMode overwriteMode) {
    this.spaceName = spaceName;
    this.filename = filename;
    this.overwriteMode = overwriteMode;
  }

  public Optional<String> getSpaceName() {
    return spaceName;
  }

  public File getFile() {
    return filename;
  }

  public OverwriteMode getOverwriteMode() {
    return overwriteMode;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PushPackageUploaderContext.class.getSimpleName() + "[", "]")
        .add("spaceName=" + spaceName)
        .add("filename=" + filename)
        .add("overwriteMode=" + overwriteMode)
        .toString();
  }
}

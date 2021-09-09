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

import com.octopus.sdk.api.PackagesApi;
import com.octopus.sdk.api.SpaceHomeApi;
import com.octopus.sdk.http.OctopusClient;
import com.octopus.sdk.model.packages.PackageFromBuiltInFeedResource;
import com.octopus.sdk.model.spaces.SpaceHome;
import com.octopus.sdk.operations.common.BaseUploader;
import com.octopus.sdk.operations.common.SpaceHomeSelector;

import java.io.IOException;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PushPackageUploader extends BaseUploader {
  private static final Logger LOG = LogManager.getLogger();

  public PushPackageUploader(
      final OctopusClient client, final SpaceHomeSelector spaceHomeSelector) {
    super(client, spaceHomeSelector);
  }

  public static PushPackageUploader create(final OctopusClient client) {
    final SpaceHomeApi spaceHomeApi = new SpaceHomeApi(client);
    final SpaceHomeSelector spaceHomeSelector = new SpaceHomeSelector(spaceHomeApi);
    return new PushPackageUploader(client, spaceHomeSelector);
  }

  public PackageFromBuiltInFeedResource upload(final PushPackageUploaderContext context)
      throws IOException {
    Preconditions.checkNotNull(context, "Attempted to upload a package with null context.");

    final SpaceHome spaceHome = spaceHomeSelector.getSpaceHome(context.getSpaceName());
    final PackagesApi packagesApi = PackagesApi.create(client, spaceHome);

    LOG.debug("Uploading {}", context.getFile());
    // TODO(tmm): "This is completelywrong
    final PackageFromBuiltInFeedResource result =
        packagesApi.uploadPackage(context.getFile(), context.getOverwriteMode());
    LOG.debug("Upload of {} complete.", context.getFile());

    return result;
  }
}

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

package octopus.teamcity.agent.pushpackage;

import static octopus.teamcity.agent.pushpackage.FileSelector.getMatchingFiles;

import com.octopus.sdk.operations.pushpackage.PushPackageParameters;
import com.octopus.sdk.operations.pushpackage.PushPackageParametersBuilder;
import com.octopus.sdk.operations.pushpackage.PushPackageUploader;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import octopus.teamcity.agent.InterruptableBuildProcess;
import octopus.teamcity.agent.generic.TypeConverters;
import octopus.teamcity.common.commonstep.CommonStepUserData;
import octopus.teamcity.common.pushpackage.PushPackageUserData;
import org.jetbrains.annotations.NotNull;

public class OctopusPushPackageBuildProcess extends InterruptableBuildProcess {

  private final PushPackageUploader uploader;
  private final BuildRunnerContext context;
  private final BuildProgressLogger buildLogger;

  public OctopusPushPackageBuildProcess(
      final PushPackageUploader uploader,
      @NotNull AgentRunningBuild runningBuild,
      @NotNull BuildRunnerContext context) {
    this.uploader = uploader;
    this.context = context;
    buildLogger = runningBuild.getBuildLogger();
  }

  @Override
  public void start() throws RunBuildException {
    try {
      buildLogger.message("Collating data for upload");
      final PushPackageParameters parameters = collateParameters();

      if (isInterrupted()) {
        complete(BuildFinishedStatus.INTERRUPTED);
        return;
      }

      if (uploader.upload(parameters)) {
        complete(BuildFinishedStatus.FINISHED_SUCCESS);
        return;
      }
      complete(BuildFinishedStatus.FINISHED_FAILED);
    } catch (final Throwable t) {
      throw new RunBuildException("Upload to server failed", t);
    }
  }

  private PushPackageParameters collateParameters() {
    final Map<String, String> parameters = context.getRunnerParameters();
    final CommonStepUserData commonStepUserData = new CommonStepUserData(parameters);
    final PushPackageUserData pushPackageUserData = new PushPackageUserData(parameters);

    final List<File> filesToUpload = determineFilesToUpload(pushPackageUserData.getPackagePaths());
    if (filesToUpload.isEmpty()) {
      buildLogger.error(
          "Supplied package globs ("
              + pushPackageUserData.getPackagePaths()
              + ") found no matching"
              + "files");
      throw new IllegalStateException("No files found which match supplied glob");
    }

    buildLogger.message("Files found to upload:");
    filesToUpload.forEach(f -> buildLogger.message("- " + f.getName()));

    return new PushPackageParametersBuilder()
        .withSpaceName(commonStepUserData.getSpaceName())
        .withFilesToUpload(filesToUpload)
        .withOverwriteMode(TypeConverters.from(pushPackageUserData.getOverwriteMode()))
        .build();
  }

  private List<File> determineFilesToUpload(final String globs) {
    final File packageRootPath = context.getWorkingDirectory();
    final List<String> packageFileGlobs = Lists.newArrayList(globs.split("\n"));
    return getMatchingFiles(packageRootPath.toPath(), packageFileGlobs);
  }
}
/*
 * Copyright 2000-2012 Octopus Deploy Pty. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package octopus.teamcity.agent.generic;

import static jetbrains.buildServer.messages.DefaultMessagesInfo.BLOCK_TYPE_BUILD_STEP;
import static octopus.teamcity.agent.generic.in_sdk.OctopusClientFactory.createClient;

import com.octopus.sdk.http.OctopusClient;
import com.octopus.sdk.operations.buildinformation.BuildInformationUploader;

import java.net.MalformedURLException;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunner;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import octopus.teamcity.agent.buildinformation.BaseBuildVcsData;
import octopus.teamcity.agent.buildinformation.BuildVcsData;
import octopus.teamcity.agent.buildinformation.OctopusBuildInformationBuildProcess;
import octopus.teamcity.agent.generic.in_sdk.ConnectData;
import octopus.teamcity.agent.pushpackage.OctopusPushPackageBuildProcess;
import octopus.teamcity.agent.pushpackage.in_sdk.PushPackageUploader;
import octopus.teamcity.common.OctopusConstants;
import octopus.teamcity.common.commonstep.CommonStepUserData;
import org.jetbrains.annotations.NotNull;

public class OctopusGenericRunner implements AgentBuildRunner {

  private static final String ACTIVITY_NAME = "OctopusDeploy";

  @NotNull
  @Override
  public AgentBuildRunnerInfo getRunnerInfo() {
    return new AgentBuildRunnerInfo() {
      @NotNull
      @Override
      public String getType() {
        return OctopusConstants.GENERIC_RUNNER_TYPE;
      }

      @Override
      public boolean canRun(@NotNull final BuildAgentConfiguration buildAgentConfiguration) {
        return true;
      }
    };
  }

  @NotNull
  @Override
  public BuildProcess createBuildProcess(
      @NotNull final AgentRunningBuild runningBuild, @NotNull final BuildRunnerContext context)
      throws RunBuildException {

    final BuildProgressLogger logger = runningBuild.getBuildLogger();
    final CommonStepUserData commonStepUserData =
        new CommonStepUserData(context.getRunnerParameters());

    final String activityName = ACTIVITY_NAME + " - " + commonStepUserData.getStepType();
    logger.activityStarted(activityName, BLOCK_TYPE_BUILD_STEP);

    logger.message("Creating connection to Octopus server @ " + commonStepUserData.getServerUrl());
    final OctopusClient client = createOctopusClient(commonStepUserData);

    return createBuildProcess(commonStepUserData.getStepType(), client, runningBuild, context);
  }

  private OctopusClient createOctopusClient(final CommonStepUserData userData)
      throws RunBuildException {
    try {
      final ConnectData connection = TypeConverters.from(userData);
      return createClient(connection);
    } catch (final MalformedURLException e) {
      throw new RunBuildException("Unable to decode supplied Octopus Server URL");
    }
  }

  private BuildProcess createBuildProcess(
      final String stepType,
      final OctopusClient client,
      final AgentRunningBuild runningBuild,
      final BuildRunnerContext context)
      throws RunBuildException {
    switch (stepType) {
      case ("build-information"):
        final BuildInformationUploader uploader = BuildInformationUploader.create(client);
        final BaseBuildVcsData buildVcsData = BuildVcsData.create(runningBuild);
        return new OctopusBuildInformationBuildProcess(uploader, buildVcsData, context);
      case ("push-package"):
        final PushPackageUploader pushPackageUploader = PushPackageUploader.create(client);
        return new OctopusPushPackageBuildProcess(pushPackageUploader, runningBuild, context);
      default:
        throw new RunBuildException("Unknown build step type " + stepType);
    }
  }
}

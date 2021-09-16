package octopus.teamcity.agent.createdeployment;

import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import octopus.teamcity.agent.InterruptableBuildProcess;

public class OctopusCreateDeploymentBuildProcess extends InterruptableBuildProcess {

  private final BuildProgressLogger buildLogger;

  public OctopusCreateDeploymentBuildProcess(BuildRunnerContext context) {
    this.buildLogger = context.getBuild().getBuildLogger();
  }

  @Override
  public void start() {
    buildLogger.message("Create deployment step not implemented");
    complete(BuildFinishedStatus.FINISHED_FAILED);
  }
}

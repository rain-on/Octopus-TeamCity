package octopus.teamcity.e2e.test;

import octopus.teamcity.e2e.dsl.OctopusDeployServer;
import octopus.teamcity.e2e.dsl.TeamCityContainers;
import octopus.teamcity.e2e.dsl.TeamCityFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.teamcity.rest.Build;
import org.jetbrains.teamcity.rest.BuildAgent;
import org.jetbrains.teamcity.rest.BuildConfiguration;
import org.jetbrains.teamcity.rest.BuildConfigurationId;
import org.jetbrains.teamcity.rest.BuildState;
import org.jetbrains.teamcity.rest.BuildStatus;
import org.jetbrains.teamcity.rest.TeamCityInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildInformationTest {

  private static final Logger LOG = LogManager.getLogger();

  protected static final String USERNAME = "admin";
  protected static final String PASSWORD = "Password01!";
  
  @Test
  public void buildInformationStepPublishesToOctopusDeploy(@TempDir Path teamcityDataDir)
      throws IOException, InterruptedException {
    final Network network = Network.newNetwork();
    final OctopusDeployServer octoServer = OctopusDeployServer.createOctopusServer(network);

    final TeamCityFactory tcFactory = new TeamCityFactory(teamcityDataDir, network);
    final TeamCityContainers teamCityContainers = tcFactory.createTeamCityServerAndAgent(octoServer.getOctopusUrl());

    final String teamCityUrl = String.format("http://localhost:%d",
        teamCityContainers.serverContainer.getFirstMappedPort());
    final TeamCityInstance tcInstance = TeamCityInstance.httpAuth(teamCityUrl, USERNAME, PASSWORD);

    final Iterator<BuildAgent> iBuildAgent = tcInstance.buildAgents().all().iterator();
    while (iBuildAgent.hasNext()) {
      final BuildAgent agent = iBuildAgent.next();
      agent.setAuthorized(true);
    }

    final BuildConfiguration buildConf = tcInstance.buildConfiguration(new BuildConfigurationId(
        "OctopusStepsWithVcs"));
    final Build build = buildConf.runBuild(
        Collections.emptyMap(),
        true,
        true,
        true,
        "My Test build run",
        null,
        false);

    final Duration buildTimeout = Duration.ofSeconds(30);
    final Instant buildStart = Instant.now();
    LOG.info("Waiting for requested build {} to complete", build.getId());
    while (Duration.between(Instant.now(), buildStart).minus(buildTimeout).isNegative()) {
      final Build updatedBuild = tcInstance.build(build.getId());
      if (updatedBuild.getState().equals(BuildState.FINISHED)) {
        break;
      }
      Thread.sleep(1000);
    }

    if (build.getState().equals(BuildState.FINISHED)) {
      LOG.info("The build is finished, and it {}", build.getStatus());
      assertThat(build.getStatus()).isEqualTo(BuildStatus.SUCCESS);
    } else {
      LOG.info("Build did not complete in time");
      assertThat(false).withFailMessage("Build did not complete in time");
    }
  }
}

package octopus.teamcity.e2e.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;

import com.google.common.io.Resources;
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

public class BuildInformationTest {

  private static final Logger LOG = LogManager.getLogger();


  @Test
  public void buildInformationStepPublishesToOctopusDeploy(@TempDir Path teamcityDataDir)
      throws IOException, InterruptedException {
    final URL projectsImport = Resources.getResource("projects.zip");

    final Network network = Network.newNetwork();
    final OctopusDeployServer octoServer = OctopusDeployServer.createOctopusServer(network);

    final TeamCityFactory tcFactory = new TeamCityFactory(teamcityDataDir, network);
    final TeamCityContainers teamCityContainers =
        tcFactory.createTeamCityServerAndAgent(octoServer.getOctopusUrl(), Path.of(projectsImport.getFile()));

    final TeamCityInstance tcInstance = teamCityContainers.getRestAPi();

    final BuildConfiguration buildConf =
        tcInstance.buildConfiguration(new BuildConfigurationId("OctopusStepsWithVcs"));
    final Build build = buildConf.runBuild(
            Collections.emptyMap(), true, true, true, "My Test build run", null, false);

    waitForBuildToFinish(build, tcInstance);

    assertThat(build.getStatus()).isEqualTo(BuildStatus.SUCCESS);
  }

  private void waitForBuildToFinish(final Build build, final TeamCityInstance tcInstance) throws InterruptedException {
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
    throw new RuntimeException("Build Failed to complete within 30 seconds");
  }
}

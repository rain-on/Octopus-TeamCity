package octopus.teamcity.e2e.test;

import com.google.common.io.Resources;
import octopus.teamcity.e2e.dsl.TeamCityContainers;
import octopus.teamcity.e2e.dsl.TeamCityFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.teamcity.rest.Build;
import org.jetbrains.teamcity.rest.BuildConfiguration;
import org.jetbrains.teamcity.rest.BuildConfigurationId;
import org.jetbrains.teamcity.rest.BuildState;
import org.jetbrains.teamcity.rest.BuildStatus;
import org.jetbrains.teamcity.rest.TeamCityInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AllBuildStepTest {

  private static final Logger LOG = LogManager.getLogger();
  protected static String apiKey = "API-D62EQ9I4EVET1E2LJUBKEHLNBYWMO3";

  // These tests assume an Octopus Server exists at a defined location - with an existing internal structure (spaces
  // projects, packages etc).

  @Test
  public void buildInformationStepPublishesToOctopusDeploy(@TempDir Path teamcityDataDir)
      throws InterruptedException, IOException {
    final URL projectsImport = Resources.getResource("projects.zip");

    final Network network = Network.newNetwork();
    //final OctopusDeployServer octoServer = OctopusDeployServer.createOctopusServer(network);

    final TeamCityFactory tcFactory = new TeamCityFactory(teamcityDataDir, network);
    final TeamCityContainers teamCityContainers =
        tcFactory.createTeamCityServerAndAgent(8065, apiKey, Path.of(projectsImport.getFile()));

    final TeamCityInstance tcRestApi = teamCityContainers.getRestAPi();

    final BuildConfiguration buildConf =
        tcRestApi.buildConfiguration(new BuildConfigurationId("OctopusStepsWithVcs"));
    final Build build = buildConf.runBuild(
            Collections.emptyMap(), true, true, true, "My Test build run", null, false);

    waitForBuildToFinish(build, tcRestApi);

    assertThat(build.getStatus()).isEqualTo(BuildStatus.SUCCESS);
  }

  private void waitForBuildToFinish(final Build build, final TeamCityInstance tcRestApi) throws InterruptedException {
    final Duration buildTimeout = Duration.ofSeconds(30);
    final Instant buildStart = Instant.now();
    LOG.info("Waiting for requested build {} to complete", build.getId());
    while (Duration.between(Instant.now(), buildStart).minus(buildTimeout).isNegative()) {
      final Build updatedBuild = tcRestApi.build(build.getId());
      if (updatedBuild.getState().equals(BuildState.FINISHED)) {
        break;
      }
      Thread.sleep(1000);
    }
    throw new RuntimeException("Build Failed to complete within 30 seconds");
  }
}

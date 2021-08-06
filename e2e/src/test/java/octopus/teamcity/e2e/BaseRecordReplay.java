package octopus.teamcity.e2e;

import octopus.teamcity.e2e.dsl.TeamCityContainers;
import octopus.teamcity.e2e.dsl.TeamCityFactory;
import org.jetbrains.teamcity.rest.Build;
import org.jetbrains.teamcity.rest.BuildAgent;
import org.jetbrains.teamcity.rest.BuildConfiguration;
import org.jetbrains.teamcity.rest.BuildConfigurationId;
import org.jetbrains.teamcity.rest.BuildState;
import org.jetbrains.teamcity.rest.TeamCityInstance;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;

public abstract class BaseRecordReplay {
  private static final Logger LOG = LoggerFactory.getLogger(BaseRecordReplay.class);
  protected static final String USERNAME = "admin";
  protected static final String PASSWORD = "Password01!";

  protected TeamCityContainers teamCityContainers;
  protected ClientAndServer mockServer;

  protected void executeTeamCityBuild(final Path teamCityDataDir) throws IOException, InterruptedException {

    final Network networkForTest = Network.newNetwork();
    final TeamCityFactory tcFactory = new TeamCityFactory(teamCityDataDir, networkForTest);

    // mock server will act as a proxy for creating logs, and a loopback when executing them
    mockServer = createMockServer();

    teamCityContainers = tcFactory.createTeamCityServerAndAgent(mockServer.getPort());

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
    } else {
      LOG.info("Build did not complete in time");
      //assertThat(false).withFailMessage("Build did not complete in time");
    }
  }

  protected Path generateLogPath() {
    return Paths.get("./recordedEvents.json");
  }

  protected abstract ClientAndServer createMockServer();

}

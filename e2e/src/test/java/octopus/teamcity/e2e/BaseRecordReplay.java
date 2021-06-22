package octopus.teamcity.e2e;

import com.google.common.io.Resources;
import net.lingala.zip4j.ZipFile;
import org.jetbrains.teamcity.rest.Build;
import org.jetbrains.teamcity.rest.BuildAgent;
import org.jetbrains.teamcity.rest.BuildConfiguration;
import org.jetbrains.teamcity.rest.BuildConfigurationId;
import org.jetbrains.teamcity.rest.BuildState;
import org.jetbrains.teamcity.rest.TeamCityInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.serialization.LogEventRequestAndResponseSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;

public abstract class BaseRecordReplay {
  private static final Logger LOG = LoggerFactory.getLogger(BaseRecordReplay.class);
  protected static final String USERNAME = "admin";
  protected static final String PASSWORD = "Password01!";

  public static class TeamCityContainers {
    public GenericContainer<?> serverContainer;
    public GenericContainer<?> agentContainer;

    public TeamCityContainers(final GenericContainer<?> serverContainer, final GenericContainer<?> agentContainer) {
      this.serverContainer = serverContainer;
      this.agentContainer = agentContainer;
    }
  }

  protected TeamCityContainers teamCityContainers;
  protected ClientAndServer mockServer;

  protected void setupDataDir(final Path teamCityDataDir) throws IOException {
    LOG.info("starting test - teamcity data dir is at {}", teamCityDataDir);
    final URL teamcityInitialConfig = Resources.getResource("teamcity_config.zip");
    new ZipFile(new File(teamcityInitialConfig.getFile()).toString()).extractAll(teamCityDataDir.toAbsolutePath().toString());

    final URL projectsImport = Resources.getResource("projects.zip");
    new ZipFile(new File(projectsImport.getFile()).toString()).extractAll(teamCityDataDir.toAbsolutePath().toString());

    LOG.info("unzipped config into {}", teamCityDataDir);
    //teamcity_plugin_dist property will be set by gradle buildsystem
    final String pluginDistribution = System.getProperty("teamcity_plugin_dist");
    Files.copy(Paths.get(pluginDistribution), teamCityDataDir.resolve("plugins").resolve("Octopus.Teamcity.zip"),
        StandardCopyOption.REPLACE_EXISTING);
  }

  protected void updateProjectFilesWithMockserverHostAndPort(final Path projectFilePath, final String httpEndpoint) throws IOException {
    final String projectContent = new String(Files.readAllBytes(projectFilePath), StandardCharsets.UTF_8);
    final String updatedContent = projectContent.replaceAll("<param name=\"octopus_host\".*",
        "<param name=\"octopus_host\" value=\"" + httpEndpoint + "\" />");
    Files.write(projectFilePath, updatedContent.getBytes(StandardCharsets.UTF_8));
  }

  protected void executeTeamCityBuild(final Path teamCityDataDir) throws IOException, InterruptedException {
    setupDataDir(teamCityDataDir);

    // mock server will act as a proxy for creating logs, and a loopback when executing them
    mockServer = createMockServer();

    teamCityContainers = createTeamCityServerAndAgent(teamCityDataDir, mockServer.getPort());

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

  protected TeamCityContainers createTeamCityServerAndAgent(final Path teamCityDataDir, final int mockServerPort) throws IOException {
    // need to update the test directory to replace <param name="octopus_host" value="http://localhost:8065" />
    // with port that the mockServer is actually listening on.
    final String mockServerEndpoint = String.format("http://host.testcontainers.internal:%d", mockServerPort);
    final Path projectFile =
        teamCityDataDir.resolve("config").resolve("projects").resolve("TeamCityTestProject").resolve(
        "buildTypes").resolve("OctopusStepsWithVcs.xml");
    updateProjectFilesWithMockserverHostAndPort(projectFile, mockServerEndpoint);

    Testcontainers.exposeHostPorts(mockServerPort);

    final Network network = Network.newNetwork();
    final GenericContainer<?> teamCityServer = new GenericContainer<>(DockerImageName.parse("jetbrains/teamcity-server"))
          .withExposedPorts(8111)
        .waitingFor(Wait.forLogMessage(".*Super user authentication token.*", 1))
        .withNetwork(network)
        .withNetworkAliases("server")
        .withEnv("TEAMCITY_SERVER_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5011")
        .withStartupTimeout(Duration.ofMinutes(2));
    teamCityServer.withFileSystemBind(teamCityDataDir.toAbsolutePath().toString(), "/data/teamcity_server/datadir");

    teamCityServer.start();
    GenericContainer<?> teamCityAgent = null;
    try {

      final String teamCityUrl = String.format("http://%s:%d", teamCityServer.getHost(),
          teamCityServer.getFirstMappedPort());
      LOG.info("TeamCity server running on {}}", teamCityUrl);

      teamCityAgent = new GenericContainer<>(DockerImageName.parse("jetbrains/teamcity-agent"))
          .withExposedPorts(5010)
          .withNetwork(network)
          .withEnv("SERVER_URL", "http://server:8111")
          .withEnv("TEAMCITY_AGENT_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5010")
          .waitingFor(Wait.forLogMessage(".*jetbrains.buildServer.AGENT - Agent name was.*", 1));
      teamCityAgent.start();
    } catch (final Exception e) {
      teamCityServer.stop();
      throw e;
    }

    return new TeamCityContainers(teamCityServer, teamCityAgent);
  }

  protected Path generateLogPath() {
    return Paths.get("./recordedEvents.json");
  }

  protected abstract ClientAndServer createMockServer();

}

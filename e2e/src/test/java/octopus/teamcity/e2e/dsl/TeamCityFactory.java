package octopus.teamcity.e2e.dsl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import com.google.common.io.Resources;
import net.lingala.zip4j.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class TeamCityFactory {

  private static final Logger LOG = LogManager.getLogger();

  private final Path teamCityDataDir;
  private final Network dockerNetwork;

  public TeamCityFactory(final Path teamCityDataDir, final Network dockerNetwork) {
    this.teamCityDataDir = teamCityDataDir;
    this.dockerNetwork = dockerNetwork;
  }

  public TeamCityContainers createTeamCityServerWithHostBasedOctopus(final int octopusServerPort)
      throws IOException {
    final String serverUrl =
        String.format("http://host.testcontainers.internal:%d", octopusServerPort);
    Testcontainers.exposeHostPorts(octopusServerPort);
    return createTeamCityServerAndAgent(serverUrl);
  }

  public TeamCityContainers createTeamCityServerAndAgent(final String octopusServerUrl)
      throws IOException {

    setupDataDir(teamCityDataDir);

    // need to update the test directory to replace <param name="octopus_host"
    // value=<octopusServerUrl> />
    final Path projectFile =
        Path.of(
            teamCityDataDir.toString(),
            "config",
            "projects",
            "TeamCityTestProject",
            "buildTypes",
            "OctopusStepsWithVcs.xml");
    updateProjectFilesWithOctopusServerEndpoint(projectFile, octopusServerUrl);

    final GenericContainer<?> teamCityServer = createAndStartServer();

    final String teamCityUrl =
        String.format(
            "http://%s:%d", teamCityServer.getHost(), teamCityServer.getFirstMappedPort());
    LOG.info("TeamCity server running on {}}", teamCityUrl);

    try {
      final GenericContainer<?> teamCityAgent = createAndStartAgent();
      return new TeamCityContainers(teamCityServer, teamCityAgent);
    } catch (final Exception e) {
      teamCityServer.stop();
      throw e;
    }
  }

  private GenericContainer<?> createAndStartServer() {
    final GenericContainer<?> teamCityServer =
        new GenericContainer<>(DockerImageName.parse("jetbrains/teamcity-server"))
            .withExposedPorts(8111)
            .waitingFor(Wait.forLogMessage(".*Super user authentication token.*", 1))
            .withNetwork(dockerNetwork)
            .withNetworkAliases("server")
            .withStartupTimeout(Duration.ofMinutes(2));
    teamCityServer.withFileSystemBind(
        teamCityDataDir.toAbsolutePath().toString(), "/data/teamcity_server/datadir");

    teamCityServer.start();
    return teamCityServer;
  }

  private GenericContainer<?> createAndStartAgent() {
    final GenericContainer<?> teamCityAgent =
        new GenericContainer<>(DockerImageName.parse("jetbrains/teamcity-agent"))
            .withNetwork(dockerNetwork)
            .withEnv("SERVER_URL", "http://server:8111")
            .waitingFor(Wait.forLogMessage(".*jetbrains.buildServer.AGENT - Agent name was.*", 1));
    teamCityAgent.start();
    return teamCityAgent;
  }

  private void updateProjectFilesWithOctopusServerEndpoint(
      final Path projectFilePath, final String httpEndpoint) throws IOException {
    final String projectContent = Files.readString(projectFilePath);
    final String updatedContent =
        projectContent.replaceAll(
            "<param name=\"octopus_host\".*",
            "<param name=\"octopus_host\" value=\"" + httpEndpoint + "\" />");
    Files.write(projectFilePath, updatedContent.getBytes(StandardCharsets.UTF_8));
  }

  protected void setupDataDir(final Path teamCityDataDir) throws IOException {
    LOG.info("starting test - teamcity data dir is at {}", teamCityDataDir);
    final URL teamcityInitialConfig = Resources.getResource("teamcity_config.zip");
    new ZipFile(new File(teamcityInitialConfig.getFile()).toString())
        .extractAll(teamCityDataDir.toAbsolutePath().toString());

    final URL projectsImport = Resources.getResource("projects.zip");
    new ZipFile(new File(projectsImport.getFile()).toString())
        .extractAll(teamCityDataDir.toAbsolutePath().toString());

    LOG.info("unzipped config into {}", teamCityDataDir);
    // teamcity_plugin_dist property will be set by gradle buildsystem
    final String pluginDistribution = System.getProperty("teamcity_plugin_dist");
    Files.copy(
        Paths.get(pluginDistribution),
        teamCityDataDir.resolve("plugins").resolve("Octopus.Teamcity.zip"),
        StandardCopyOption.REPLACE_EXISTING);
  }
}

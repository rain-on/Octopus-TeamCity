package octopus.teamcity.e2e.test;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.octopus.sdk.api.BuildInformationApi;
import com.octopus.sdk.api.SpaceHomeApi;
import com.octopus.sdk.http.ConnectData;
import com.octopus.sdk.http.OctopusClient;
import com.octopus.sdk.http.OctopusClientFactory;
import com.octopus.sdk.model.buildinformation.OctopusPackageVersionBuildInformationMappedResource;
import com.octopus.sdk.model.spaces.SpaceHome;
import com.octopus.testsupport.ExistingOctopusDeployServer;
import com.octopus.testsupport.OctopusDeployServer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

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

public class BuildInformationEndToEndTest {

  private static final Logger LOG = LogManager.getLogger();
  protected static String apiKey = "API-D62EQ9I4EVET1E2LJUBKEHLNBYWMO3";

  @Test
  public void buildInformationStepPublishesToOctopusDeploy(@TempDir Path teamcityDataDir)
      throws InterruptedException, IOException {
    final URL projectsImport = Resources.getResource("TeamCity_StepVnext.zip");

    final Network network = Network.newNetwork();
    final OctopusDeployServer octoServer = new ExistingOctopusDeployServer();

    final TeamCityFactory tcFactory = new TeamCityFactory(teamcityDataDir, network);

    // NOTE: the 8065 SHOULD be octoServer.getPort(), not the magic number!  (coming after updates
    // to SDK)
    final TeamCityContainers teamCityContainers =
        tcFactory.createTeamCityServerAndAgent(8065, apiKey, Paths.get(projectsImport.getFile()));

    final TeamCityInstance tcRestApi = teamCityContainers.getRestAPi();

    final BuildConfiguration buildConf =
        tcRestApi.buildConfiguration(new BuildConfigurationId("StepVnext_ExecuteBuildInfo"));
    final Build build =
        buildConf.runBuild(emptyMap(), true, true, true, "My Test build run", null, false);

    waitForBuildToFinish(build, tcRestApi);

    final File logDump = teamcityDataDir.resolve("build.log").toFile();
    build.downloadBuildLog(logDump);
    LOG.info(teamCityContainers.getAgentContainer().getLogs());

    assertThat(build.getStatus())
        .withFailMessage(() -> teamCityContainers.getAgentContainer().getLogs())
        .isEqualTo(BuildStatus.SUCCESS);

    final ConnectData connectData =
        new ConnectData(
            new URL(octoServer.getOctopusUrl()), octoServer.getApiKey(), Duration.ofSeconds(2));
    final OctopusClient client = OctopusClientFactory.createClient(connectData);

    final SpaceHomeApi spaceHomeApi = new SpaceHomeApi(client);
    final SpaceHome spaceHome =
        spaceHomeApi.getByName("My Space"); // TODO(tmm): Should get this from the PROJECT!
    final BuildInformationApi buildInfoApi = BuildInformationApi.create(client, spaceHome);
    final List<OctopusPackageVersionBuildInformationMappedResource> items =
        buildInfoApi.getByQuery(emptyMap());

    assertThat(items.size()).isEqualTo(1);
    assertThat(items.get(0).getPackageId()).isEqualTo("mypackage.noreally");
  }

  private void waitForBuildToFinish(final Build build, final TeamCityInstance tcRestApi)
      throws InterruptedException {
    final Duration buildTimeout = Duration.ofSeconds(30);
    final Instant buildStart = Instant.now();
    LOG.info("Waiting for requested build {} to complete", build.getId());
    while (Duration.between(Instant.now(), buildStart).minus(buildTimeout).isNegative()) {
      final Build updatedBuild = tcRestApi.build(build.getId());
      if (updatedBuild.getState().equals(BuildState.FINISHED)) {
        return;
      }
      Thread.sleep(1000);
    }
    throw new RuntimeException("Build Failed to complete within 30 seconds");
  }
}

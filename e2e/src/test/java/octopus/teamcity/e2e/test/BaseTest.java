package octopus.teamcity.e2e.test;

import com.octopus.sdk.http.OctopusClient;
import octopus.teamcity.e2e.dsl.OctopusDeployServer;
import octopus.teamcity.e2e.dsl.TeamCityContainers;
import octopus.teamcity.e2e.dsl.TeamCityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.nio.file.Path;

public class BuildInformationTest {

  @Test
  public void buildInformationStepPublishesToOctopusDeploy(@TempDir Path teamcityDataDir) throws IOException {
    final Network network = Network.newNetwork();
    final OctopusDeployServer octoServer = OctopusDeployServer.createOctopusServer(network);

    final TeamCityFactory tcFactory = new TeamCityFactory(teamcityDataDir, network);
    final TeamCityContainers teamCity = tcFactory.createTeamCityServerAndAgent(octoServer.getOctopusUrl());

    


  }
}

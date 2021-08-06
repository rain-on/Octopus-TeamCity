package octopus.teamcity.e2e.dsl;

import org.testcontainers.containers.GenericContainer;

public class TeamCityContainers {
  public GenericContainer<?> serverContainer;
  public GenericContainer<?> agentContainer;

  public TeamCityContainers(
      final GenericContainer<?> serverContainer, final GenericContainer<?> agentContainer) {
    this.serverContainer = serverContainer;
    this.agentContainer = agentContainer;
  }
}

package octopus.teamcity.e2e.dsl;

import com.google.common.io.Resources;
import net.lingala.zip4j.ZipFile;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TeamCityContainers {
  public GenericContainer<?> serverContainer;
  public GenericContainer<?> agentContainer;

  public TeamCityContainers(final GenericContainer<?> serverContainer, final GenericContainer<?> agentContainer) {
    this.serverContainer = serverContainer;
    this.agentContainer = agentContainer;
  }

}

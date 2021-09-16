package octopus.teamcity.server.generic;

import java.util.List;
import java.util.Map;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.serverSide.InvalidProperty;
import octopus.teamcity.common.createdeployment.CreateDeploymentPropertyNames;
import octopus.teamcity.common.createdeployment.CreateDeploymentUserData;
import org.apache.commons.compress.utils.Lists;

public class CreateDeploymentStep extends OctopusBuildStep {

  private final CreateDeploymentPropertyNames KEYS = new CreateDeploymentPropertyNames();

  public CreateDeploymentStep() {
    super(
        "create-deployment",
        "Create deployment",
        "editCreateDeploymentParameters.jsp",
        "viewCreateDeploymentParameters.jsp");
  }

  @Override
  public String describeParameters(Map<String, String> properties) {
    final CreateDeploymentUserData userData = new CreateDeploymentUserData(properties);

    return String.format(
        "Project: %s\nEnvironments: %s\nRelease version: %s",
        userData.getProjectNameOrId(),
        String.join(", ", userData.getEnvironmentIdsOrNames()),
        userData.getReleaseVersion());
  }

  @Override
  public List<InvalidProperty> validateProperties(Map<String, String> properties) {
    final List<InvalidProperty> failedProperties = Lists.newArrayList();
    final CreateDeploymentUserData userData = new CreateDeploymentUserData(properties);

    if (StringUtil.isEmpty(userData.getProjectNameOrId().trim())) {
      failedProperties.add(
          new InvalidProperty(
              KEYS.getProjectNameOrIdPropertyName(),
              "A project name/id must be specified and cannot be whitespace."));
    }

    final List<String> environmentIdentifiers = userData.getEnvironmentIdsOrNames();
    if (environmentIdentifiers.isEmpty()) {
      failedProperties.add(
          new InvalidProperty(
              KEYS.getEnvironmentIdsOrNamesPropertyName(),
              "At least one environment name/id must be specified."));
    } else {
      for (String identifier : environmentIdentifiers) {
        if (StringUtil.isEmpty(identifier.trim())) {
          failedProperties.add(
              new InvalidProperty(
                  KEYS.getEnvironmentIdsOrNamesPropertyName(),
                  "A environment name/id must be specified and cannot be whitespace."));
          break;
        }
      }
    }

    if (StringUtil.isEmpty(userData.getReleaseVersion().trim())) {
      failedProperties.add(
          new InvalidProperty(
              KEYS.getReleaseVersionPropertyName(),
              "A release version must be specified and cannot be whitespace."));
    }

    return failedProperties;
  }
}

package octopus.teamcity.common.createdeployment;

import java.util.List;
import java.util.Map;

import jetbrains.buildServer.util.StringUtil;
import octopus.teamcity.common.BaseUserData;

public class CreateDeploymentUserData extends BaseUserData {

  private final CreateDeploymentPropertyNames KEYS = new CreateDeploymentPropertyNames();

  public CreateDeploymentUserData(Map<String, String> parameters) {
    super(parameters);
  }

  public String getProjectNameOrId() {
    return fetchRaw(KEYS.getProjectNameOrIdPropertyName());
  }

  public List<String> getEnvironmentIdsOrNames() {
    final String rawInput = fetchRaw(KEYS.getEnvironmentIdsOrNamesPropertyName());
    return StringUtil.split(rawInput, "\n");
  }

  public String getReleaseVersion() {
    return fetchRaw(KEYS.getReleaseVersionPropertyName());
  }
}

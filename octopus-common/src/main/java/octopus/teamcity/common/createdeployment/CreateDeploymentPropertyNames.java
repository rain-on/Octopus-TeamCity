package octopus.teamcity.common.createdeployment;

public class CreateDeploymentPropertyNames {

  public static final String PROJECT_NAME_OR_ID = "octopus_cd_project-name-or-id";
  public static final String ENVIRONMENT_IDS_OR_NAMES = "octopus_cd_environment-ids-or-names";
  public static final String RELEASE_VERSION = "octopus_cd_release-version";
  public static final String VARIABLES = "octopus_cd_variables";

  public CreateDeploymentPropertyNames() {}

  public String getProjectNameOrIdPropertyName() {
    return PROJECT_NAME_OR_ID;
  }

  public String getEnvironmentIdsOrNamesPropertyName() {
    return ENVIRONMENT_IDS_OR_NAMES;
  }

  public String getReleaseVersionPropertyName() {
    return RELEASE_VERSION;
  }

  public String getVariablesPropertyName() {
    return VARIABLES;
  }
}

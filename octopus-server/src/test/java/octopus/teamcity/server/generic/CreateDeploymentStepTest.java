package octopus.teamcity.server.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetbrains.buildServer.serverSide.InvalidProperty;
import octopus.teamcity.common.createdeployment.CreateDeploymentPropertyNames;
import org.junit.jupiter.api.Test;

class CreateDeploymentStepTest {

  private final CreateDeploymentStep step = new CreateDeploymentStep();

  @Test
  public void describePropertiesProducesExpectedOutput() {
    String message = step.describeParameters(buildPropertiesMap());
    assertEquals(
        "Project: ProjectName\nEnvironments: Env1, Env2\nRelease version: 1.0.0-SNAPSHOT", message);
  }

  @Test
  public void validatePropertiesReturnsEmptyInvalidPropertiesListWithValidProperties() {
    List<InvalidProperty> invalidProperties = step.validateProperties(buildPropertiesMap());
    assertNotNull(invalidProperties);
    assertEquals(0, invalidProperties.size());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnNullProjectNameOrId() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.PROJECT_NAME_OR_ID, null);

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "A project name/id must be specified and cannot be whitespace.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnEmptyProjectNameOrId() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.PROJECT_NAME_OR_ID, "");

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "A project name/id must be specified and cannot be whitespace.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnNullEnvironments() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.ENVIRONMENT_IDS_OR_NAMES, null);

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "At least one environment name/id must be specified.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnEmptyEnvironments() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.ENVIRONMENT_IDS_OR_NAMES, "");

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "At least one environment name/id must be specified.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void
      validatePropertiesReturnsSingleInvalidPropertyOnEnvironmentsWithWhitespaceIdentifiers() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.ENVIRONMENT_IDS_OR_NAMES, "env1\n \nenv3");

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "A environment name/id must be specified and cannot be whitespace.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnNullReleaseVersion() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.RELEASE_VERSION, null);

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "A release version must be specified and cannot be whitespace.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsSingleInvalidPropertyOnEmptyReleaseVersion() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.RELEASE_VERSION, "");

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(1, invalidProperties.size());
    assertEquals(
        "A release version must be specified and cannot be whitespace.",
        invalidProperties.get(0).getInvalidReason());
  }

  @Test
  public void validatePropertiesReturnsMultipleInvalidProperty() {
    Map<String, String> properties = buildPropertiesMap();
    properties.put(CreateDeploymentPropertyNames.PROJECT_NAME_OR_ID, "");
    properties.put(CreateDeploymentPropertyNames.ENVIRONMENT_IDS_OR_NAMES, "");
    properties.put(CreateDeploymentPropertyNames.RELEASE_VERSION, "");

    List<InvalidProperty> invalidProperties = step.validateProperties(properties);
    assertNotNull(invalidProperties);
    assertEquals(3, invalidProperties.size());
  }

  private Map<String, String> buildPropertiesMap() {
    final Map<String, String> validMap = new HashMap<>();
    validMap.put(CreateDeploymentPropertyNames.PROJECT_NAME_OR_ID, "ProjectName");
    validMap.put(CreateDeploymentPropertyNames.ENVIRONMENT_IDS_OR_NAMES, "Env1\nEnv2");
    validMap.put(CreateDeploymentPropertyNames.RELEASE_VERSION, "1.0.0-SNAPSHOT");
    return validMap;
  }
}

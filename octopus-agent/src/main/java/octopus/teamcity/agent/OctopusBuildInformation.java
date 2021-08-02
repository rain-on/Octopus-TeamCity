package octopus.teamcity.agent;

import com.octopus.sdk.operations.buildinformation.Commit;

import java.util.List;

public class OctopusBuildInformation {
  public String BuildEnvironment;
  public String Branch;
  public String BuildNumber;
  public String BuildUrl;
  public String VcsType;
  public String VcsRoot;
  public String VcsCommitNumber;

  public List<Commit> Commits;

  public OctopusBuildInformation() {
    BuildEnvironment = "TeamCity";
  }
}

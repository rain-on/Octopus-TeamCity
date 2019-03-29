package octopus.teamcity.agent;

import octopus.teamcity.common.Commit;

import java.util.List;

public class OctopusPackageMetadata {
    public String BuildEnvironment;
    public String CommentParser;
    public String BuildNumber;
    public String BuildUrl;
    public String VcsType;
    public String VcsRoot;
    public String VcsCommitNumber;

    public List<Commit> Commits;

    public OctopusPackageMetadata() {
        BuildEnvironment = "TeamCity";
    }
}

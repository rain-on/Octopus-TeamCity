package octopus.teamcity.agent;

import java.util.List;

public class OctopusMetadata {
    public String BuildEnvironment;
    public List<WorkItem> WorkItems;

    public OctopusMetadata() {
        BuildEnvironment = "TeamCity";
    }
}

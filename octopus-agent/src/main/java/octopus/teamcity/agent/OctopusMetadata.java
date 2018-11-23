package octopus.teamcity.agent;

import java.util.List;

public class OctopusMetadata {
    public String BuildServerType;
    public List<WorkItem> WorkItems;

    public OctopusMetadata() {
        BuildServerType = "TeamCity";
    }
}

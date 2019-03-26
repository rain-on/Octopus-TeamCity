package octopus.teamcity.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jetbrains.buildServer.agent.BuildProgressLogger;
import octopus.teamcity.common.Commit;

import java.util.List;

public class OctopusMetadataBuilder {

    private BuildProgressLogger buildLogger;

    public OctopusMetadataBuilder(final BuildProgressLogger buildLogger){

        this.buildLogger = buildLogger;
    }

    public OctopusPackageMetadata build(
            final String vcsRoot,
            final String vcsCommitNumber,
            final String commitsJson,
            final String commentParser,
            final String serverUrl,
            final String buildId,
            final String buildNumber) {

        final OctopusPackageMetadata metadata = new OctopusPackageMetadata();

        final Gson gson = new GsonBuilder()
                .create();

        metadata.Commits = gson.fromJson(commitsJson, new TypeToken<List<Commit>>() {}.getType());
        metadata.CommentParser = commentParser;
        metadata.BuildNumber = buildNumber;
        metadata.BuildLink = serverUrl + "/viewLog.html?buildId=" + buildId;
        metadata.VcsRoot = vcsRoot;
        metadata.VcsCommitNumber = vcsCommitNumber;

        return metadata;
    }
}

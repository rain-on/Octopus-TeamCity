package octopus.teamcity.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import octopus.teamcity.common.Commit;

import java.util.List;

public class OctopusMetadataBuilder {

    public OctopusPackageMetadata build(
            final String vcsType,
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
        metadata.BuildUrl = serverUrl + "/viewLog.html?buildId=" + buildId;
        metadata.VcsType = vcsType;
        metadata.VcsRoot = vcsRoot;
        metadata.VcsCommitNumber = vcsCommitNumber;

        return metadata;
    }
}

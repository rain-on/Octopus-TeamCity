package octopus.teamcity.agent;

import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.vcs.VcsChangeInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.vcs.VcsRootEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommentWorkItemHandler {
    public void processComments(final AgentRunningBuild myRunningBuild, final String commentParser, final String basePath) throws Exception {
        BuildProgressLogger buildLogger = myRunningBuild.getBuildLogger();

        final CommentParserFactory parserFactory = new CommentParserFactory();
        final List<WorkItem> workItems = new ArrayList<WorkItem>();

        final String comments = myRunningBuild.getSharedConfigParameters().get("comments");
        if (comments != null && !comments.isEmpty()) {
            final List<WorkItem> items = parserFactory.getParser(commentParser).parse(comments, buildLogger);
            workItems.addAll(items);
        }

        if (workItems.size() > 0) {
            buildLogger.message("Found work items in comments, adding " + workItems.size() + " work items to octopus.metadata");

            try {
                final String metaFile = Paths.get(myRunningBuild.getCheckoutDirectory().getPath(), basePath, "octopus.metadata").toAbsolutePath().toString();
                buildLogger.message("Creating " + metaFile);

                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                buildLogger.message("Serializing work items");
                final OctopusMetadata octopusMetadata = new OctopusMetadata();
                octopusMetadata.WorkItems = workItems;
                final String jsonData = gson.toJson(octopusMetadata);
                buildLogger.message("Serialized Octopus metadata - " + jsonData);

                BufferedWriter bw = new BufferedWriter(new FileWriter(metaFile));
                bw.write(jsonData);
                bw.close();
                buildLogger.message("Wrote " + metaFile);
            } catch (IOException e) {
                e.printStackTrace();
                buildLogger.message("Error writing the octopus.metadata file");
                throw e;
            }
        } else {
            buildLogger.message("No work items found in comments");
        }
    }
}

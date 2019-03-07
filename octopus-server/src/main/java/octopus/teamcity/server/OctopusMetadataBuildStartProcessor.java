package octopus.teamcity.server;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRootInstanceEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OctopusMetadataBuildStartProcessor implements BuildStartContextProcessor {

    private ExtensionHolder extensionHolder;

    public OctopusMetadataBuildStartProcessor(@NotNull ExtensionHolder extensionHolder) {
        this.extensionHolder = extensionHolder;
    }

    @Override
    public void updateParameters(@NotNull BuildStartContext buildStartContext) {

        final SRunningBuild build = buildStartContext.getBuild();
        final List<SVcsModification> changes = build.getChanges(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD, true);
        final List<VcsRootInstanceEntry> vcsRoots = build.getVcsRootEntries();
        String changesText = "";

        final VcsRootInstanceEntry vcsRoot = vcsRoots.get(0);
        final Map<String, String> props = vcsRoot.getProperties();
        final String vcsRootUrl = props.get("url");

        for (SVcsModification change : changes) {
            changesText += change.getDescription();
        }

        buildStartContext.addSharedParameter("comments", changesText);
        buildStartContext.addSharedParameter("vcsroot", vcsRootUrl);
    }

    public void register() {
        extensionHolder.registerExtension(BuildStartContextProcessor.class, this.getClass().getName(), this);
    }
}

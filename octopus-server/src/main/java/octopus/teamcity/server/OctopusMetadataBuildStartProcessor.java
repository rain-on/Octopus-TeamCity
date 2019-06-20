package octopus.teamcity.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRootInstanceEntry;
import octopus.teamcity.common.Commit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OctopusMetadataBuildStartProcessor implements BuildStartContextProcessor {

    private ExtensionHolder extensionHolder;
    private WebLinks webLinks;

    public OctopusMetadataBuildStartProcessor(@NotNull final ExtensionHolder extensionHolder,
                                              @NotNull final WebLinks webLinks) {
        this.extensionHolder = extensionHolder;
        this.webLinks = webLinks;
    }

    @Override
    public void updateParameters(@NotNull BuildStartContext buildStartContext) {

        final SRunningBuild build = buildStartContext.getBuild();
        final List<SVcsModification> changes = build.getChanges(SelectPrevBuildPolicy.SINCE_LAST_BUILD, true);
        final List<VcsRootInstanceEntry> vcsRoots = build.getVcsRootEntries();

        if (vcsRoots.size() == 0) {
            return;
        }

        final VcsRootInstanceEntry vcsRoot = vcsRoots.get(0);
        final Map<String, String> props = vcsRoot.getProperties();
        final String vcsRootUrl = props.get("url");
        String vcsType = "Unknown";
        if (vcsRoot.getVcsName().contains("git")) {
            vcsType = "Git";
        }

        final List<Commit> commits = new ArrayList<Commit>();
        for (SVcsModification change : changes) {

            final Commit c = new Commit();
            c.Id = change.getVersion();
            c.Comment = change.getDescription();

            commits.add(c);
        }

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        final String jsonData = gson.toJson(commits);

        buildStartContext.addSharedParameter("serverRootUrl", webLinks.getRootUrl());
        buildStartContext.addSharedParameter("commits", jsonData);
        buildStartContext.addSharedParameter("vcstype", vcsType);
        if (vcsRootUrl != null) {
            buildStartContext.addSharedParameter("vcsroot", vcsRootUrl);
        }
    }

    public void register() {
        extensionHolder.registerExtension(BuildStartContextProcessor.class, this.getClass().getName(), this);
    }
}

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

public class OctopusBuildInformationBuildStartProcessor implements BuildStartContextProcessor {

    private ExtensionHolder extensionHolder;
    private WebLinks webLinks;

    public OctopusBuildInformationBuildStartProcessor(@NotNull final ExtensionHolder extensionHolder,
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

        final BuildPromotion promotion = build.getBuildPromotion();
        Branch branch = promotion.getBranch();
        final String branchName = branch == null ? "" : branch.getDisplayName();

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

        buildStartContext.addSharedParameter("octopus_serverRootUrl", webLinks.getRootUrl());
        buildStartContext.addSharedParameter("octopus_commits", jsonData);
        buildStartContext.addSharedParameter("octopus_branch", branchName);
        buildStartContext.addSharedParameter("octopus_vcstype", vcsType);
        if (vcsRootUrl != null) {
            buildStartContext.addSharedParameter("octopus_vcsroot", vcsRootUrl);
        }
    }

    public void register() {
        extensionHolder.registerExtension(BuildStartContextProcessor.class, this.getClass().getName(), this);
    }
}

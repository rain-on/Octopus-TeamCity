package octopus.teamcity.server;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OctopusMetadataBuildStartProcessor implements BuildStartContextProcessor {

    private ExtensionHolder extensionHolder;

    public OctopusMetadataBuildStartProcessor(@NotNull ExtensionHolder extensionHolder) {
        this.extensionHolder = extensionHolder;
    }

    @Override
    public void updateParameters(@NotNull BuildStartContext buildStartContext) {

        final List<SVcsModification> changes = buildStartContext.getBuild().getChanges(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD, true);
        String changesText = "";

        for (SVcsModification change : changes) {
            changesText += change.getDescription();
        }

        buildStartContext.addSharedParameter("comments", changesText);
    }

    public void register() {
        extensionHolder.registerExtension(BuildStartContextProcessor.class, this.getClass().getName(), this);
    }
}

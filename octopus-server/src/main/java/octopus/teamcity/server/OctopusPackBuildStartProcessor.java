package octopus.teamcity.server;

import jetbrains.buildServer.ExtensionHolder;
import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OctopusPackBuildStartProcessor implements BuildStartContextProcessor {

    private ExtensionHolder extensionHolder;

    public OctopusPackBuildStartProcessor(@NotNull ExtensionHolder extensionHolder) {
        this.extensionHolder = extensionHolder;
    }

    @Override
    public void updateParameters(@NotNull BuildStartContext buildStartContext) {

        final List<SVcsModification> changes = buildStartContext.getBuild().getChanges(SelectPrevBuildPolicy.SINCE_FIRST_BUILD, true);
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

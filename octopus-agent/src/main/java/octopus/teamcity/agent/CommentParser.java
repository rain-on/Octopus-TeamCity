package octopus.teamcity.agent;

import jetbrains.buildServer.agent.BuildProgressLogger;

import java.util.List;

public abstract class CommentParser {
    public abstract List<WorkItem> parse(String comment, final BuildProgressLogger buildLogger);
}

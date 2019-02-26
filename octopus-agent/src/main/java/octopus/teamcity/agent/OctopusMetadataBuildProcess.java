/*
 * Copyright 2000-2012 Octopus Deploy Pty. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package octopus.teamcity.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.*;
import octopus.teamcity.common.OctopusConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class OctopusMetadataBuildProcess implements BuildProcess {

    private boolean isFinished;
    private final AgentRunningBuild myRunningBuild;
    private final BuildRunnerContext myContext;

    public OctopusMetadataBuildProcess(@NotNull AgentRunningBuild runningBuild, @NotNull BuildRunnerContext context) {
        myRunningBuild = runningBuild;
        myContext = context;
    }

    public void start() throws RunBuildException {
        final Map<String, String> parameters = myContext.getRunnerParameters();
        final OctopusConstants constants = OctopusConstants.Instance;

        final String outputPath = parameters.get(constants.getMetadataOutputPathKey());
        final String commentParser = parameters.get(constants.getCommentParserKey());

        try {
            final CommentWorkItemHandler commentHandler = new CommentWorkItemHandler();
            commentHandler.processComments(myRunningBuild, commentParser, outputPath);
        } catch (Exception ex) {
            throw new RunBuildException("Error processing comment messages", ex);
        }
    }

    public boolean isInterrupted() {
        return false;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void interrupt() {
    }

    @NotNull
    public BuildFinishedStatus waitFor() {
        isFinished = true;

        return BuildFinishedStatus.FINISHED_SUCCESS;
    }

}

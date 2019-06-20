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

import jetbrains.buildServer.agent.*;
import octopus.teamcity.common.OctopusConstants;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class OctopusMetadataBuildProcess extends OctopusBuildProcess {

    private final File checkoutDir;
    private final Map<String, String> sharedConfigParameters;

    public OctopusMetadataBuildProcess(@NotNull AgentRunningBuild runningBuild, @NotNull BuildRunnerContext context) {
        super(runningBuild, context);

        checkoutDir = runningBuild.getCheckoutDirectory();
        sharedConfigParameters = runningBuild.getSharedConfigParameters();
    }

    @Override
    protected String getLogMessage() {
        return "Pushing package metadata to Octopus server";
    }

    @Override
    protected OctopusCommandBuilder createCommand() {

        final BuildProgressLogger buildLogger = getLogger();

        final Map<String, String> parameters = getContext().getRunnerParameters();
        final OctopusConstants constants = OctopusConstants.Instance;
        final Boolean verboseLogging = Boolean.parseBoolean(parameters.get(constants.getVerboseLoggingKey()));

        final String commentParser = parameters.get(constants.getCommentParserKey());

        final String metaFile = Paths.get(checkoutDir.getPath(), "octopus.metadata").toAbsolutePath().toString();

        try {
            AgentRunningBuild build = getContext().getBuild();

            final OctopusMetadataBuilder builder = new OctopusMetadataBuilder();
            final OctopusPackageMetadata metadata = builder.build(
                    sharedConfigParameters.get("vcstype"),
                    sharedConfigParameters.get("vcsroot"),
                    sharedConfigParameters.get("build.vcs.number"),
                    sharedConfigParameters.get("commits"),
                    commentParser,
                    sharedConfigParameters.get("serverRootUrl"),
                    Long.toString(build.getBuildId()),
                    build.getBuildNumber());

            if (verboseLogging) {
                buildLogger.message("Creating " + metaFile);
            }

            final OctopusMetadataWriter writer = new OctopusMetadataWriter(buildLogger, verboseLogging);
            writer.writeToFile(metadata, metaFile);

        } catch (Exception ex) {
            buildLogger.error("Error processing comment messages " + ex);
            return null;
        }

        return new OctopusCommandBuilder() {
            @Override
            protected String[] buildCommand(boolean masked) {
                final ArrayList<String> commands = new ArrayList<String>();
                final String serverUrl = parameters.get(constants.getServerKey());
                final String apiKey = parameters.get(constants.getApiKey());
                final String spaceName = parameters.get(constants.getSpaceName());
                final String packageId = parameters.get(constants.getPackageIdKey());
                final String packageVersion = parameters.get(constants.getPackageVersionKey());

                final boolean forcePush = Boolean.parseBoolean(parameters.get(constants.getForcePushKey()));

                commands.add("push-metadata");
                commands.add("--server");
                commands.add(serverUrl);
                commands.add("--apikey");
                commands.add(masked ? "SECRET" : apiKey);

                if (spaceName != null && !spaceName.isEmpty()) {
                    commands.add("--space");
                    commands.add(spaceName);
                }

                commands.add("--package-id");
                commands.add(packageId);

                commands.add("--version");
                commands.add(packageVersion);

                commands.add("--metadata-file");
                commands.add(metaFile);

                if (forcePush) {
                    commands.add("--replace-existing");
                }

                return commands.toArray(new String[commands.size()]);
            }
        };
    }
}

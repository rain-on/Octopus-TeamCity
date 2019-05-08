package octopus.teamcity.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.agent.BuildProgressLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OctopusMetadataWriter {

    private BuildProgressLogger buildLogger;
    private Boolean verboseLogging;

    public OctopusMetadataWriter(final BuildProgressLogger buildLogger, final Boolean verboseLogging) {

        this.buildLogger = buildLogger;
        this.verboseLogging = verboseLogging;
    }

    public void writeToFile(final OctopusPackageMetadata octopusPackageMetadata, final String metaFile) throws IOException {
        try {
            final Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
            if (verboseLogging) {
                buildLogger.message("Serializing Octopus metadata");
            }

            final String jsonData = gson.toJson(octopusPackageMetadata);
            if (verboseLogging) {
                buildLogger.message("Serialized Octopus metadata - " + jsonData);
            }

            OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(metaFile), StandardCharsets.UTF_16);
            bw.write(jsonData);
            bw.close();

            if (verboseLogging) {
                buildLogger.message("Wrote " + metaFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            buildLogger.message("Error writing the octopus.metadata file");
            throw e;
        }
    }
}

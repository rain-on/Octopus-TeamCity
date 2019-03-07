package octopus.teamcity.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jetbrains.buildServer.agent.BuildProgressLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OctopusMetadataWriter {

    private BuildProgressLogger buildLogger;

    public OctopusMetadataWriter(BuildProgressLogger buildLogger) {

        this.buildLogger = buildLogger;
    }

    public void writeToFile(final OctopusPackageMetadata octopusPackageMetadata, final String metaFile) throws IOException {
        try {
            final Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
            buildLogger.message("Serializing Octopus metadata");

            final String jsonData = gson.toJson(octopusPackageMetadata);
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
    }
}

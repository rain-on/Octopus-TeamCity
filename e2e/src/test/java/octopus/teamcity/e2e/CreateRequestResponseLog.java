package octopus.teamcity.e2e;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockserver.model.HttpRequest.request;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Format;
import org.mockserver.model.HttpForward;

public class CreateRequestResponseLog extends BaseRecordReplay {

  @Test
  public void doTest(@TempDir Path teamCityDataDir) throws IOException, InterruptedException {
    executeTeamCityBuild(teamCityDataDir);

    final String loggedExpectations =
        mockServer.retrieveRecordedExpectations(request(), Format.JSON);
    final BufferedWriter writer =
        Files.newBufferedWriter(Paths.get(generateLogPath().toString()), UTF_8);
    writer.write(loggedExpectations);
    writer.close();
  }

  @Override
  protected ClientAndServer createMockServer() {
    System.setProperty("mockserver.maxSocketTimeout", "120000");
    final ClientAndServer mockServer = new ClientAndServer(8065);
    mockServer.when(request()).forward(HttpForward.forward().withHost("localhost").withPort(8066));
    return mockServer;
  }
}

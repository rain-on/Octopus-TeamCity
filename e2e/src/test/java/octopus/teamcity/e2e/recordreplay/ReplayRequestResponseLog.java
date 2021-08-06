package octopus.teamcity.e2e.recordreplay;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.Expectation;
import org.mockserver.model.HttpResponse;

public class ReplayRequestResponseLog extends BaseRecordReplay {

  @Test
  public void doTest(@TempDir Path teamCityDataDir) throws IOException, InterruptedException {
    executeTeamCityBuild(teamCityDataDir);
  }

  @Override
  protected ClientAndServer createMockServer() {
    System.setProperty("mockserver.initializationJsonPath", generateLogPath().toString());
    final ClientAndServer clientAndServer = new ClientAndServer(8065);

    for (final Expectation expectation : clientAndServer.retrieveActiveExpectations(null)) {
      // NOTE: content-length header includes raw bytes (with no /n) - however body contains /n's
      // (so replace
      // content-length header).
      final String body = expectation.getHttpResponse().getBody().toString();
      final HttpResponse modifiedResponse =
          expectation
              .getHttpResponse()
              .replaceHeader(CONTENT_LENGTH, Integer.toString(body.length()));
      expectation.thenRespond(modifiedResponse);
      clientAndServer.upsert(expectation);
    }

    return clientAndServer;
  }
}

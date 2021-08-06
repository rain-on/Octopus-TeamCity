package octopus.teamcity.e2e;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.Expectation;
import org.mockserver.model.HttpResponse;

public class LongRunningSystemExists extends BaseRecordReplay {

  @Test
  public void runThis() throws InterruptedException {
    mockServer = createMockServer();
    while (true) {
      Thread.sleep(10000);
    }
  }

  @Override
  protected ClientAndServer createMockServer() {
    System.setProperty("mockserver.initializationJsonPath", generateLogPath().toString());
    final ClientAndServer clientAndServer = new ClientAndServer(8065);

    for (final Expectation expectation : clientAndServer.retrieveActiveExpectations(null)) {
      // NOTE: the contentLength is a bit wrong as it doesn't know if it wants /n or not (content
      // length implies NO,
      // content imoplies yes!
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

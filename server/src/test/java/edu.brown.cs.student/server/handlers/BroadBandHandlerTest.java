package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.broadband.BroadBandData;
import edu.brown.cs.student.main.datasource.broadband.BroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.CachingBroadBandDataSource;
import edu.brown.cs.student.main.datasource.broadband.Location;
import edu.brown.cs.student.main.server.handlers.BroadBandHandler;
import edu.brown.cs.student.mocks.MockedACSAPIBroadBandDatasource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Class containing tests for BroadBandHandlerTest */
public class BroadBandHandlerTest {
  /** Set up the port */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
  }

  @BeforeEach
  public void setup() {
    // Set up mocked data for test environment
    BroadBandData mockedData =
        new BroadBandData("Bay County, Florida", "90.7", "12", "005", new Date().toString());
    BroadBandDatasource mockedSource = new MockedACSAPIBroadBandDatasource(mockedData);
    // Restart the entire Spark server for every test
    Spark.get("broadband", new BroadBandHandler(mockedSource));

    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on endpoint after each test
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }
  /** The location of Bay County, FL, USA */
  final Location bay = new Location("Florida", "Bay County");

  /** Tests an api request with both parameters null. */
  @Test
  public void testAPINullBothParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadBandHandler.BroadbandFailureResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_no_state", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests an api request with state name parameter null. */
  @Test
  public void testAPINullStateParameter() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?county=Bay%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadBandHandler.BroadbandFailureResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_no_state", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests an api request with county name parameter null. */
  @Test
  public void testAPINullCountyParameter() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=Florida");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadBandHandler.BroadbandFailureResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_no_county", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests an api request with successful response and correct returned broadband. */
  @Test
  public void testAPISuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=Florida&county=" + "Bay%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadBandHandler.BroadbandSuccessResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.response_type());

    assertEquals("90.7", response.responseMap().get("broadband"));
    clientConnection.disconnect();
  }

  /**
   * Tests the cache successfully adds correct items doesn't exceed max value and evicts correct
   * items.
   */
  @Test
  public void testCaching() throws IOException {
    // Make a separate endpoint for caching broadband (testing purposes only)
    CachingBroadBandDataSource cachingMockedSource =
        new CachingBroadBandDataSource(
            new MockedACSAPIBroadBandDatasource(
                new BroadBandData(
                    "Bay County, Florida", "90.7", "12", "005", new Date().toString())),
            3);
    Spark.get("cachingbroadband", new BroadBandHandler(cachingMockedSource));

    Spark.awaitInitialization(); // don't continue until the server is listening

    assertEquals(cachingMockedSource.peekCache().size(), 0);

    HttpURLConnection clientConnection =
        tryRequest("cachingbroadband?state=Florida&county=" + "Bay%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();

    assertEquals(cachingMockedSource.peekCache().size(), 1);

    clientConnection = tryRequest("cachingbroadband?state=Michigan&county=" + "Genessee%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();

    assertEquals(cachingMockedSource.peekCache().size(), 2);

    clientConnection = tryRequest("cachingbroadband?state=New%20York&county=" + "uhh%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();

    assertEquals(cachingMockedSource.peekCache().size(), 3);

    clientConnection = tryRequest("cachingbroadband?state=Florida&county=" + "John%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();
    assertEquals(cachingMockedSource.peekCache().size(), 3);

    HashMap<Location, BroadBandData> finalCache = cachingMockedSource.peekCache();
    for (Location loc : finalCache.keySet()) {
      assertEquals(finalCache.get(loc).broadBand(), "90.7");
      // make sure our last thing was removed
      assertNotEquals(loc.county(), "Bay County");
    }

    // make sure the least used one is removed
    // Genessee County is next up to be tossed. Call it again and then call smt else...
    clientConnection = tryRequest("cachingbroadband?state=Michigan&county=" + "Genessee%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();

    assertEquals(cachingMockedSource.peekCache().size(), 3);

    clientConnection = tryRequest("cachingbroadband?state=Maryland&county=" + "YK%20County");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();
    assertEquals(cachingMockedSource.peekCache().size(), 3);

    finalCache = cachingMockedSource.peekCache();
    for (Location loc : finalCache.keySet()) {
      assertEquals(finalCache.get(loc).broadBand(), "90.7");
      // make sure our last USED thing was removed (not genessee county)
      assertNotEquals(loc.county(), "uhh County");
    }
  }
}

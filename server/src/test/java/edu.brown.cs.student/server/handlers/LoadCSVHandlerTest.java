package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Class for testing the LoadCSV handler */
public class LoadCSVHandlerTest {

  /** Opens the port for spark for testing */
  @BeforeAll
  public static void setup_before_everything() {
    //    Spark.port(0);
  }

  /** Shared state for all tests, cleared out after every test run */
  final HashMap<String, String> loadedFiles = new HashMap<>();

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    this.loadedFiles.clear();

    // Restart the entire Spark server for every test
    Spark.get("loadcsv", new LoadCSVHandler(this.loadedFiles, 100));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
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

  /** Tests that LoadCSVHandler throws the correct error for a missing filepath */
  @Test
  public void testLoadCSVHandlerMissingFilePath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    LoadCSVHandler.LoadCSVFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_missing_filepath", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests that LoadCSVHandler throws the correct error for a file not existing */
  @Test
  public void testAPIFileNotFound() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/resources/" + "malforme_signs.csv");

    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    LoadCSVHandler.LoadCSVFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_file_not_found", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests that LoadCSVHandler throws the correct error for a file outside resources folder */
  @Test
  public void testAPIFileOutsideCSVFolder() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/" + "csv/students/students.csv");

    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    LoadCSVHandler.LoadCSVFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_file_outside_resources_folder", response.response_type());

    clientConnection.disconnect();
  }

  /** Tests that LoadCSVHandler succeeds */
  @Test
  public void testAPIFileLoadSuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/resources" + "/students.csv");

    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    LoadCSVHandler.LoadCSVSuccessResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadCSVSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.response_type());

    clientConnection.disconnect();
  }
}

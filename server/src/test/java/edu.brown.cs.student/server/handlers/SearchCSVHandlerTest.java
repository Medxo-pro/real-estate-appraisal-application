package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.SearchCSVHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class SearchCSVHandlerTest {

  /** Setup port */
  @BeforeAll
  public static void setup_before_everything() {
    //    Spark.port(0);
  }

  /** Adapter object */
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */
  HashMap<String, String> loadedFiles = new HashMap<>();

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    this.loadedFiles.clear();

    // Restart the entire Spark server for every test
    Spark.get("loadcsv", new LoadCSVHandler(this.loadedFiles, 100));
    Spark.get("searchcsv", new SearchCSVHandler(this.loadedFiles));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("searchcsv");
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

  /** Testing a call to the searchcsv file when the file wasn't loaded yet */
  @Test
  public void testSearchCSVFileNotLoaded() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv?filename=students");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_file_not_loaded", response.response_type());

    clientConnection.disconnect();
  }

  /** Test switching loaded csv files */
  @Test
  public void testSwitchingCSVFiles() throws IOException {
    // Load the first CSV file
    HttpURLConnection loadConnection1 = tryRequest("loadcsv?filepath=data/resources/ten-star.csv");
    assertEquals(200, loadConnection1.getResponseCode());
    loadConnection1.disconnect();

    // Load a different CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    // Now view the first loaded CSV
    HttpURLConnection viewConnection1 =
        tryRequest("searchcsv?filename=ten-star&ifHeader=true&searchKey=Alice&columnID=StarID");
    assertEquals(200, viewConnection1.getResponseCode());
    viewConnection1.disconnect();

    // Now view the newly loaded CSV
    HttpURLConnection viewConnection2 =
        tryRequest("searchcsv?filename=students&ifHeader=true&searchKey=Alice&columnID=Name");
    assertEquals(200, viewConnection2.getResponseCode());

    // Make sure the filepath is updated and we succeeded
    assertEquals(this.loadedFiles.get("students"), "data/resources/students.csv");
    assertEquals(this.loadedFiles.get("ten-star"), "data/resources/ten-star.csv");

    Moshi moshi = new Moshi.Builder().build();
    SearchCSVHandler.SearchCSVSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVSuccessResponse.class)
            .fromJson(new Buffer().readFrom(viewConnection2.getInputStream()));

    assertEquals("success", response.response_type());
    viewConnection2.disconnect();
  }

  /** Testing a call to searchcsv when the search key is invalid/null */
  @Test
  public void testSearchCSVFileInvalidSearchKey() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=students&ifHeader=true&columnID=Name");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_invalid_search_key", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to the searchcsv file when the file is invalid/null */
  @Test
  public void testSearchCSVFileInvalidFileName() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection = tryRequest("searchcsv?ifHeader=true&columnID=Name");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_invalid_file_name", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when there is no header row in the csv file */
  @Test
  public void testSearchCSVFileNoHeaderRow() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/ten_star_no_header.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest(
            "searchcsv?filename=ten_star_no_header&ifHeader="
                + "false&searchKey=0&columnID=StarID");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_no_header_row_cannot_access_column_name", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when the call column name is not found in the file */
  @Test
  public void testSearchColumnNameNotFound() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=students&ifHeader=true&searchKey=Alice&columnID=DOB");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_column_name_not_found", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when the call column index is negative */
  @Test
  public void testSearchNegativeColumnIndex() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=students&ifHeader=true&searchKey=Alice&columnID=-5");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_column_index_negative", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when the call column index is out of range */
  @Test
  public void testSearchOutOfRangeIndex() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=students&ifHeader=true&searchKey=Alice&columnID=20");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_column_index_out_of_range", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when it is a successful call */
  @Test
  public void testSearchSuccess() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/students.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=students&ifHeader=true&searchKey=Alice&columnID=Name");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.response_type());

    clientConnection.disconnect();
  }

  /** Testing a call to searchcsv when there is a parser faliure */
  @Test
  public void testSearchParserFaliure() throws IOException {
    // Load a CSV file
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?" + "filepath=data/resources/malformed_signs.csv");
    assertEquals(200, loadConnection2.getResponseCode());
    loadConnection2.disconnect();

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?filename=malformed_signs&ifHeader=true&searchKey=Alice&columnID=20");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchCSVFailureResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchCSVFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_parsing", response.response_type());

    clientConnection.disconnect();
  }
}

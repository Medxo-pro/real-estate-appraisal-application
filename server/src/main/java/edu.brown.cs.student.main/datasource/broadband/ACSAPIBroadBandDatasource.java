package edu.brown.cs.student.main.datasource.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import okio.Buffer;

/** A datasource for broadband retrieval via the real ACS API. Does not implement caching. */
public class ACSAPIBroadBandDatasource implements BroadBandDatasource {
  /** Hashmap from state names to codes used for converting user requests to ACS requests. */
  private static HashMap<String, String> STATE_CODES = new HashMap<>();

  /**
   * Private helper method; throws IOException so different callers can handle differently if
   * needed. Sets up an HTTP connection to the inputted URL.
   *
   * @param requestURL the url to create the HTTP connection to
   * @throws DatasourceException if API connection does not succeed
   * @throws IOException if the url connection isn't an HTTP request
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }

  /**
   * Given location, finds the broadband by invoking the ACS API. Makes real web requests.
   *
   * @param location a record containing the state and county to retrieve the data for
   * @return the broadband data obtained
   * @throws DatasourceException if there is an issue retrieving data for this state and county
   * @throws IllegalArgumentException if the state or county is not found or invalid
   */
  @Override
  public BroadBandData getBroadBand(Location location)
      throws DatasourceException, IllegalArgumentException {
    // If we haven't populated the HashMap from state names to their codes (if first call) do so.
    if (STATE_CODES.isEmpty()) {
      fetchStateCodes();
    }

    // Get the code corresponding to the inputted state
    String stateCode = STATE_CODES.get(location.state().toLowerCase());
    // If there's no corresponding code, the state is not valid or recognized
    if (STATE_CODES.get(location.state().toLowerCase()) == null) {
      throw new IllegalArgumentException("state code not found for state " + location.state());
    }

    // Get the code correspond to the inputted county, assuming the county is in the state
    String countyCode = getCountyCode(stateCode, location.county());
    // If there's no corresponding code, the county is not valid or in the state
    if (countyCode == null) {
      throw new IllegalArgumentException(
          "county code not found for county "
              + location.county()
              + " in state "
              + location.state()
              + stateCode);
    }

    // If we succeeded in getting codes, run our helper which calls the ACS API.
    return getBroadBand(stateCode, countyCode);
  }

  /**
   * Given state and county codes, find the broadband at that location by invoking the ACS API.
   *
   * @param stateCode the code of the state to retrieve the data for
   * @param countyCode the code of the county to retrieve the data for
   * @return the broadband data obtained
   * @throws DatasourceException if there is an issue retrieving data for this state and county
   */
  private static BroadBandData getBroadBand(String stateCode, String countyCode)
      throws DatasourceException {
    try {
      // Create the url, send the request, and read in the response
      URL requestURL =
          new URL(
              "https://api.census.gov/data/2021/acs/acs1/subject/variables?"
                  + "get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      HttpURLConnection clientConnection = connect(requestURL);

      Moshi moshi = new Moshi.Builder().build();

      JsonAdapter<List<List<String>>> adapter =
          moshi.adapter(
              Types.newParameterizedType(
                  List.class, Types.newParameterizedType(List.class, String.class)));

      // Parse the raw JSON array of arrays with our resul
      List<List<String>> response =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

      clientConnection.disconnect();

      // Check that the response is of the format we're expecting
      if (response == null || response.size() != 2 || response.get(1).size() != 4) {
        throw new DatasourceException("Malformed response from ACS API");
      }

      // Extract the values from the second array (first one is the headers)
      List<String> values = response.get(1);

      // Parse the necessary fields from the array
      String name = values.get(0);
      String broadBand = values.get(1);
      String responseStateCode = values.get(2);
      String responseCountyCode = values.get(3);

      // Return the result, formatted as a BroadBandData record object
      return new BroadBandData(
          name, broadBand, responseStateCode, responseCountyCode, new Date().toString());
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage(), e);
    }
  }

  /**
   * Populates the STATE_CODES hashmap. Gets the state codes for all states by making a get request
   * to the census api.
   *
   * @throws DatasourceException if it cannot fetch the state code from the API
   */
  private void fetchStateCodes() throws DatasourceException {
    try {
      // Create the url and send the request
      URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

      // Define the type: List<List<String>>
      Type type =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));

      // Initializes an adapter to a List<List<String>> and then uses it to parse the JSON.
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      // Convert the list to a HashMap
      boolean firstLine = true;
      for (List<String> data : body) {
        if (firstLine) {
          firstLine = false;
          continue;
        }
        STATE_CODES.put(data.get(0).toLowerCase(), data.get(1));
      }

    } catch (IOException e) {
      STATE_CODES = null;
      throw new DatasourceException("error fetching state codes: " + e.getMessage());
    }
  }

  /**
   * Gets the county code for the inputted county within the inputted state represented by a state
   * code. Does this by making a get request to the census api to retrieve all counties and their
   * codes within a state.
   *
   * @param stateCode the code for the state the county is in
   * @param county the county we're searching for the code of
   * @return -1 if the county isn't found in the state, the county's code otherwise
   * @throws DatasourceException if it cannot fetch the state code from the API
   */
  public static String getCountyCode(String stateCode, String county) throws DatasourceException {
    try {
      // Get counties in the state
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/" + "sf1?get=NAME&for=county:*&in=state:" + stateCode);

      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();

      // Define the type: List<List<String>>
      Type type =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));

      // Initializes an adapter to a List<List<String>> and then uses it to parse the JSON.
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      // Find our county and its corresponding code. Initialize to and return null if not found.
      String countyCode = null;
      for (List<String> countyData : body) {
        if (countyData.get(0).split(",")[0].equalsIgnoreCase(county)) {
          countyCode = countyData.get(2);
        }
      }
      return countyCode;
    } catch (IOException e) {
      throw new DatasourceException("error fetching county code: " + e.getMessage());
    }
  }
}

package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.server.csvfuncs.SearchCSV;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Supports endpoint searchcsv that will send back rows matching the given search criteria. */
public class SearchCSVHandler implements Route {
  /** The shared state for passing along the filepath. */
  private static HashMap<String, String> LOADED_FILES;
  /** Moshi object used to convert data to json form. */
  private static Moshi moshi = new Moshi.Builder().build();

  /**
   * Constructs a SearchCSVHandler with access to a shared server variable.
   *
   * @param loadedFiles The shared state hashmap containing the paths and names for loaded CSV files
   *     accessible by the server and other endpoint handlers
   */
  public SearchCSVHandler(HashMap<String, String> loadedFiles) {
    this.LOADED_FILES = loadedFiles;
  }

  /**
   * Handles requests to search a CSV file.
   *
   * @param request The request object providing information about the HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return A serialized JSON response indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    // example request:
    // http://localhost:3232/searchcsv?filename=students&fifHeader=true&searchKey=Alice&columnID=2

    // Get the variables from the request
    String paramFileName = request.queryParams("filename");
    boolean ifHeader = Boolean.parseBoolean(request.queryParams("ifHeader"));
    String searchKey = request.queryParams("searchKey");
    String columnID = request.queryParams("columnID");
    String rowsFound;

    // Make sure we're given a loaded file has been loaded
    if (paramFileName == null) {
      response.status(400); // Bad Request
      return new SearchCSVFailureResponse("error_invalid_file_name").serialize();
    } else if (LOADED_FILES.get(paramFileName) == null) {
      response.status(404); // Not found
      return new SearchCSVFailureResponse("error_file_not_loaded").serialize();
    }

    // Get the file path
    String filePath = LOADED_FILES.get(paramFileName);

    // Make sure we're given a searchKey
    if (searchKey == null || searchKey.isEmpty()) {
      response.status(400); // Bad Request
      return new SearchCSVFailureResponse("error_invalid_search_key").serialize();
    }

    // Create a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Use our search class and attempt to search
      rowsFound = new SearchCSV().searchCSV(filePath, ifHeader, searchKey, columnID);

      // Fill response map if we succeeded, and return success response
      responseMap.put("data", rowsFound);
      response.status(200); // OK
      return new SearchCSVSuccessResponse(responseMap).serialize();

    } catch (IOException e) {
      // Descriptive error responses for each IOException our search class can throw
      e.printStackTrace();
      if (e.getMessage().contains("header row not present, cannot access colName")) {
        response.status(400); // Bad Request
        return new SearchCSVFailureResponse("error_no_header_row_cannot_" + "access_column_name")
            .serialize();
      } else if (e.getMessage().contains("not found in dataset")) {
        response.status(404); // Not Found
        return new SearchCSVFailureResponse("error_column_name_not_found").serialize();
      } else {
        response.status(500); // Internal Server Error
        return new SearchCSVFailureResponse("error_IOException").serialize();
      }
    } catch (IllegalArgumentException e) {
      // Descriptive error responses for each IllegalArgumentException our search class can throw
      e.printStackTrace();
      response.status(400); // Bad Request
      if (e.getMessage().contains("must be zero or greater")) {
        return new SearchCSVFailureResponse("error_column_index_negative").serialize();
      } else if (e.getMessage().contains("number of columns")) {
        return new SearchCSVFailureResponse("error_column_index_out_of_range").serialize();
      } else {
        return new SearchCSVFailureResponse("error_illegal_argument").serialize();
      }
    } catch (FactoryFailureException e) {
      e.printStackTrace();
      response.status(500); // Internal Server Error
      return new SearchCSVFailureResponse("error_parsing").serialize();
    }
  }

  /** Response object to send, containing a success method and the csv file loaded. */
  public record SearchCSVSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public SearchCSVSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        JsonAdapter<SearchCSVSuccessResponse> adapter =
            moshi.adapter(SearchCSVSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        throw e;
      }
    }
  }

  /** Response object to send if there are any issues or errors with searching. */
  public record SearchCSVFailureResponse(String response_type) {
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      return moshi.adapter(SearchCSVFailureResponse.class).toJson(this);
    }
  }
}

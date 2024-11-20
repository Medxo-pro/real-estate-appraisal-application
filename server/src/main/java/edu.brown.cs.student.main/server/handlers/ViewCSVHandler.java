package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.server.csvfuncs.ViewCSV;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Supports endpoint viewcsv that will send back entire CSV file's contents as a JSON 2D array. */
public class ViewCSVHandler implements Route {
  /** The shared state for passing along the loaded filepaths. */
  private static HashMap<String, String> LOADED_FILES;
  /** Moshi object used to convert data to json form. */
  private static Moshi moshi = new Moshi.Builder().build();

  /**
   * Constructs a ViewCSVHandler with access to a shared server variable.
   *
   * @param loadedFiles the hashset containing files that have been loaded and their paths,
   *     accessible by the server and other endpoint handlers
   */
  public ViewCSVHandler(HashMap<String, String> loadedFiles) {
    this.LOADED_FILES = loadedFiles;
  }

  /**
   * Handles requests to view a CSV file.
   *
   * @param request The request object providing information about the HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return A serialized JSON response indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    // example request: http://localhost:3232/viewcsv?filename=students
    // Get the filepath from the params
    String paramFileName = request.queryParams("filename");
    // Create a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    // Make sure our file has been loaded
    if (LOADED_FILES.get(paramFileName) == null) {
      response.status(404);
      return new ViewCSVFailureResponse("error_file_not_loaded").serialize();
    }
    // Otherwise, get the filepath
    String filePath = LOADED_FILES.get(paramFileName);

    // Converts CSV contents to List<List<String>
    try {
      // Try viewing the csv file (catch clauses for errors)
      String jsonData = new ViewCSV().viewCSV(filePath);

      // If we succeeded, add the data to the map and return a success method
      responseMap.put("data", jsonData);
      response.status(200);
      return new ViewCSVSuccessResponse(responseMap).serialize();

    } catch (IOException e) {
      e.printStackTrace();
      response.status(500);
      return new ViewCSVFailureResponse("error_parsing_file").serialize();
    } catch (FactoryFailureException e) {
      e.printStackTrace();
      response.status(400);
      return new ViewCSVFailureResponse("error_data_in_incorrect_format").serialize();
    }
  }

  /** Response object to send, containing a success method and the csv file viewable. */
  public record ViewCSVSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public ViewCSVSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Use Moshi which takes in this class and returns it as JSON!
        JsonAdapter<ViewCSVSuccessResponse> adapter = moshi.adapter(ViewCSVSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if there are any issues with viewing the file. */
  public record ViewCSVFailureResponse(String response_type) {
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      return moshi.adapter(ViewCSVFailureResponse.class).toJson(this);
    }
  }
}

package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.server.csvfuncs.LoadCSV;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Supports endpoint LoadCSV that will load a CSV file if one is located at the specified path. */
public class LoadCSVHandler implements Route {
  /** The shared state for passing along the filepath. */
  private static HashMap<String, String> LOADED_FILES;
  /** Moshi object used to convert data to json form. */
  private static final Moshi moshi = new Builder().build();
  /** The maximum number of files that can be loaded at a time */
  private static int maxFiles;

  /**
   * Constructs a LoadCSVHandler with access to a shared server variable.
   *
   * @param loadedFiles A hashmap containing the names of all CSV files that have been loaded and
   *     their filepaths
   * @param maxFiles the maximum number of files that can be loaded at a time
   */
  public LoadCSVHandler(HashMap<String, String> loadedFiles, int maxFiles) {
    this.LOADED_FILES = loadedFiles;
    this.maxFiles = maxFiles;
  }

  /**
   * Handles requests to load a CSV file.
   *
   * @param request The request object providing information about the HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return A serialized JSON response indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    // example request: http://localhost:3232/loadcsv?filepath=data/resources/students.csv

    if (LOADED_FILES.size() >= maxFiles) {
      return new LoadCSVFailureResponse("error_max_number_files_loaded").serialize();
    }

    // Get the filepath from the params
    String paramFilePath = request.queryParams("filepath");

    // Throw an error if there's no filepath param given
    if (paramFilePath == null) {
      response.status(400); // Bad Request
      return new LoadCSVFailureResponse("error_missing_filepath").serialize();
    }

    // Create a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Try to create the loader object with this filepath (throws errors)
      LoadCSV loader = new LoadCSV();
      String fileName = loader.loadCSV(paramFilePath);

      // If we succeeded, continue
      responseMap.put("filepath", paramFilePath);
      responseMap.put("filename", fileName);

      this.LOADED_FILES.put(fileName, paramFilePath);

      response.status(200); // OK
      return new LoadCSVSuccessResponse(responseMap).serialize();

    } catch (FileNotFoundException e) {
      response.status(404); // Not Found

      // Detailed error responses
      if (e.getMessage().contains("File not in resources folder")) {
        return new LoadCSVFailureResponse("error_file_outside_resources_folder").serialize();
      } else {
        return new LoadCSVFailureResponse("error_file_not_found").serialize();
      }
    }
  }

  /** Response object to send, containing a success method and the csv file loaded. */
  public record LoadCSVSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public LoadCSVSuccessResponse(Map<String, Object> responseMap) {
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
        JsonAdapter<LoadCSVSuccessResponse> adapter = moshi.adapter(LoadCSVSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if the file isn't found or isn't in the correct directory. */
  public record LoadCSVFailureResponse(String response_type) {
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      return moshi.adapter(LoadCSVFailureResponse.class).toJson(this);
    }
  }
}

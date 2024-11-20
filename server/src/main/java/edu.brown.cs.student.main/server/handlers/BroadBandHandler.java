package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.DatasourceException;
import edu.brown.cs.student.main.datasource.broadband.BroadBandData;
import edu.brown.cs.student.main.datasource.broadband.BroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.Location;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for the broadband endpoint that send back broadband data from a datasource. */
public class BroadBandHandler implements Route {
  /** Data to search for the broadband through. */
  private final BroadBandDatasource DATA_SOURCE;
  /** Moshi object used to convert data to json form. */
  private static final Moshi moshi = new Moshi.Builder().build();

  /**
   * Constructs a BroadbandHandler with access to a datasource.
   *
   * @param broadBandDatasource the datasource
   */
  public BroadBandHandler(BroadBandDatasource broadBandDatasource) {
    this.DATA_SOURCE = broadBandDatasource;
  }

  /**
   * Handles requests to get a specific broadband.
   *
   * @param request The request object providing information about the HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return A serialized JSON response indicating success or failure.
   */
  @Override
  public Object handle(Request request, Response response) {
    // example request: http://localhost:3232/broadband?state=Florida&county=Broward%20County

    // Get the  request parameters needed for search
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // Make sure they were inputted properly
    if (state == null || state.isEmpty()) {
      response.status(400); // Bad Request
      return new BroadbandFailureResponse("error_no_state").serialize();
    } else if (county == null || county.isEmpty()) {
      response.status(400); // Bad Request
      return new BroadbandFailureResponse("error_no_county").serialize();
    }

    // Create a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    Location location = new Location(state.toLowerCase(), county.toLowerCase());

    try {
      BroadBandData broadBandData = DATA_SOURCE.getBroadBand(location);
      // Add request parameters to response map
      responseMap.put("date", broadBandData.date());
      responseMap.put("county code", broadBandData.countyCode());
      responseMap.put("state code", broadBandData.stateCode());
      responseMap.put("broadband", broadBandData.broadBand());
      responseMap.put("name", broadBandData.name());

      // Detailed error reporting...
    } catch (DatasourceException e) {
      response.status(500); // Internal Server Error
      if (e.getMessage().contains("error fetching state codes")) {
        return new BroadbandFailureResponse("error_datasource_failure_getting_" + "state_codes")
            .serialize();
      } else if (e.getMessage().contains("error fetching county code")) {
        return new BroadbandFailureResponse("error_datasource_failure_getting_" + "county_code")
            .serialize();
      } else {
        return new BroadbandFailureResponse("error_datasource_failure");
      }
    } catch (IllegalArgumentException e) {
      response.status(404); // Not Found
      if (e.getMessage().contains("county code not found for")) {
        return new BroadbandFailureResponse(
                "error_county_" + county + "_not_found_in_state_" + state)
            .serialize();
      } else if (e.getMessage().contains("state code not found for")) {
        return new BroadbandFailureResponse("error_state_" + state + "_not_found").serialize();
      } else {
        response.status(400); // Bad Request for other illegal arguments
        return new BroadbandFailureResponse("error_illegal_argument").serialize();
      }
    }
    response.status(200); // OK
    return new BroadbandSuccessResponse(responseMap).serialize();
  }

  /** Response object to send, containing a success method and the response map. */
  public record BroadbandSuccessResponse(String response_type, Map<String, Object> responseMap) {
    /** Constructor for the response object, assumes success. */
    public BroadbandSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        JsonAdapter<BroadbandSuccessResponse> adapter =
            moshi.adapter(BroadbandSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if there are any issues fetching the broadband. */
  public record BroadbandFailureResponse(String response_type) {
    /**
     * Serializes this response as a Json and returns it.
     *
     * @return this response, serialized as Json
     */
    String serialize() {
      return moshi.adapter(BroadbandFailureResponse.class).toJson(this);
    }
  }
}

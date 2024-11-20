package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.datasource.broadband.ACSAPIBroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.CachingBroadBandDataSource;
import edu.brown.cs.student.main.server.handlers.BroadBandHandler;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.ViewCSVHandler;
import java.util.HashMap;
import spark.Spark;

/** Top-level class. Starts Spark and runs the various handlers (4). */
public class Server {

  /** Shared state used to share the loaded files between handlers. */
  private static HashMap<String, String> LOADED_FILES = new HashMap<>();

  /** Main server method. Starts the server and sets up the handlers and paths. */
  public Server() {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET endpoints and pass the shared state to handlers via
    // constructor injection
    Spark.get("loadcsv", new LoadCSVHandler(Server.LOADED_FILES, 100));
    Spark.get("viewcsv", new ViewCSVHandler(Server.LOADED_FILES));
    Spark.get("searchcsv", new SearchCSVHandler(Server.LOADED_FILES));

    // Pass the real or mock or caching datasource to the BroadBandHandler
    ACSAPIBroadBandDatasource datasource = new ACSAPIBroadBandDatasource();
    CachingBroadBandDataSource cachingBroadBandDataSource =
        new CachingBroadBandDataSource(datasource, 100);
    Spark.get("broadband", new BroadBandHandler(cachingBroadBandDataSource));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  /** Main method which instantiates new Server. */
  public static void main(String[] args) {
    Server server = new Server();
  }
}
